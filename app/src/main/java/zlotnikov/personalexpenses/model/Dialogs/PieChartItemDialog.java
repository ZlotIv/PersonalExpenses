package zlotnikov.personalexpenses.model.Dialogs;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.POJO.ColorEvent;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;


public class PieChartItemDialog extends DialogFragment implements View.OnClickListener {

    private FancyButton colorBtn;
    private EditText categoryET;
    private String defaultCategory;
    private int defaultColor;
    private int newColor;
    private ArrayList<String> categoryList;

    public static PieChartItemDialog newInstance(String category, String color, int money) {

        Bundle args = new Bundle();

        args.putString("category", category);
        args.putString("color", color);
        args.putInt("money", money);

        PieChartItemDialog fragment = new PieChartItemDialog();
        fragment.setArguments(args);
        return fragment;
    }
    // регистрируем слушателя для EventBus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.pie_chart_item, container, false);

        categoryET = (EditText) view.findViewById(R.id.pieChartItem_category_ET);
        TextView moneyTV = (TextView) view.findViewById(R.id.pieChartItem_money_TV);
        colorBtn = (FancyButton) view.findViewById(R.id.pieChartItem_colorBtn);
        Button positiveBtn = (Button)view.findViewById(R.id.pieChartItem_positiveBtn);
        Button negativeBtn = (Button)view.findViewById(R.id.pieChartItem_negativeBtn);
        Button neutralBtn = (Button)view.findViewById(R.id.pieChartItem_neutralBtn);

        categoryList = sortRealmList(RealmData.returnAllData());

        categoryET.setText(getArguments().getString("category"));
        categoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NewCategoryDialog.LETTERS_COUNT), categoryFilter});
        moneyTV.setText(String.valueOf(getArguments().getInt("money")));
        colorBtn.setBackgroundColor(Color.parseColor(getArguments().getString("color")));
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        neutralBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
        neutralBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);

        defaultCategory = getArguments().getString("category");
        defaultColor = Color.parseColor(getArguments().getString("color"));
        if (savedInstanceState != null){
            newColor = savedInstanceState.getInt("newColor");
            colorBtn.setBackgroundColor(newColor);
        } else {
            newColor = defaultColor;
        }

        return view;
    }
    // закрываем слушателя для EventBus
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("newColor", newColor);
    }
    // подписанный метод на получение данных из EventBus
    @Subscribe
    public void onEvent(ColorEvent event){
        colorBtn.setBackgroundColor(event.getColor());
        newColor = event.getColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pieChartItem_positiveBtn:

                String newCategory = categoryET.getText().toString();
                // меняем и цвет и название для всех данных определенной категории, если такого названия уже нет в БД
                if (!categoryET.getText().toString().isEmpty() && !newCategory.equals(defaultCategory) && newColor != defaultColor){
                    if (categoryList.contains(newCategory)){
                        Toast.makeText(getContext(), getResources().getString(R.string.category_exist_error), Toast.LENGTH_SHORT).show();
                    } else {
                        RealmData.changeCategoryAndColor(defaultCategory, newCategory, "#" + Integer.toHexString(newColor));
                        ExpensesListFragment.adapter.update();
                        PieChartFragment.pieChart.update();
                        PieChartFragment.adapter.updateList();
                        this.dismiss();
                    }
                // меняем название для всех данных определенной категории, если такого названия уже нет в БД
                } else if (!categoryET.getText().toString().isEmpty() && !newCategory.equals(defaultCategory)){
                    if (categoryList.contains(newCategory)){
                        Toast.makeText(getContext(), getResources().getString(R.string.category_exist_error), Toast.LENGTH_SHORT).show();
                    } else {
                        RealmData.changeCategory(defaultCategory, newCategory);
                        ExpensesListFragment.adapter.update();
                        PieChartFragment.pieChart.update();
                        PieChartFragment.adapter.updateList();
                        this.dismiss();
                    }
                // меняем цвет для всех данных определенной категории
                } else if (newColor != defaultColor) {
                    RealmData.changeColor(defaultCategory, "#" + Integer.toHexString(newColor));
                    ExpensesListFragment.adapter.update();
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    this.dismiss();
                } else {
                    this.dismiss();
                }
                break;
            case R.id.pieChartItem_neutralBtn:
                DeleteDialog.newInstance(defaultCategory).show(getFragmentManager(), "deleteDialog");
                this.dismiss();
                break;
            case R.id.pieChartItem_negativeBtn:
                this.dismiss();
                break;
            case R.id.pieChartItem_colorBtn:
                ColorPickerDialog.newInstance().show(getFragmentManager(), "colorPickerDialog");
                break;
        }
    }

    InputFilter categoryFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.toString().matches("[\\p{Alnum}]+")) return source;
            return "";

        }

    };

    private ArrayList<String> sortRealmList(List<Expense> realmList){
        ArrayList<String> list = new ArrayList<>();
        // берем первый элемент из БД
        for (int i = 0; i < realmList.size(); i++){
            // проверка
            boolean count = false;
            // категория элемента
            String category = realmList.get(i).getCategory();
            // проверяем имеется ли такая категория уже в списке
            for (int j = 0; j < list.size(); j++){
                if (list.get(j).equals(category)){
                    // если имеется то меняем счетчик
                    count = true;
                }
            }
            // если не содержит, то записываем
            if (!count) {
                list.add(category);
            }
        }

        return list;
    }
}
