package zlotnikov.personalexpenses.model.Dialogs;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

public class NewCategoryDialog extends DialogFragment implements View.OnClickListener{

    public static final int LETTERS_COUNT = 13;

    private FancyButton colorBtn;
    private int color;
    private EditText categoryET;
    private EditText moneyET;
    private ArrayList<String> categoryList;

    public static NewCategoryDialog newInstance() {

        Bundle args = new Bundle();

        NewCategoryDialog fragment = new NewCategoryDialog();
        fragment.setArguments(args);
        return fragment;
    }
    // регистрируем получателя EventBus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.myNewCategoryDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.new_category_dialog, container, false);

        moneyET = (EditText)view.findViewById(R.id.newCategory_moneyET);
        categoryET = (EditText)view.findViewById(R.id.newCategory_categoryET);
        colorBtn = (FancyButton) view.findViewById(R.id.newCategory_colorBtn);
        Button negativeBtn = (Button)view.findViewById(R.id.newCategory_negativeBtn);
        Button positiveBtn = (Button)view.findViewById(R.id.newCategory_positiveBtn);

        categoryList = sortRealmList(RealmData.returnAllData());

        if (savedInstanceState != null){
            color = savedInstanceState.getInt("color");
            colorBtn.setBackgroundColor(color);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                color = getResources().getColor(R.color.colorPrimary, null);
            } else {
                color = getResources().getColor(R.color.colorPrimary);
            }
        }
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        colorBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
        // установка фильтра для суммы и категории
        moneyET.addTextChangedListener(moneyTextWatcher);
        categoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(LETTERS_COUNT), categoryFilter});

        return view;
    }
    // закрываем получателя EventBus
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    // подписанный метод на получение EventBus
    @Subscribe
    public void onEvent(ColorEvent event){
        colorBtn.setBackgroundColor(event.getColor());
        color = event.getColor();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newCategory_negativeBtn:
                this.dismiss();
                break;
            case R.id.newCategory_positiveBtn:
                // если категория уже существует
                if (categoryList.contains(categoryET.getText().toString())){
                    Toast.makeText(getContext(), getResources().getString(R.string.category_exist_error), Toast.LENGTH_SHORT).show();
                }
                // если нет
                else if (moneyET.getError() == null && !moneyET.getText().toString().isEmpty() && !categoryET.getText().toString().isEmpty()) {
                    RealmData.addItem(Integer.parseInt(moneyET.getText().toString()), categoryET.getText().toString(),"#" + Integer.toHexString(color));
                    this.dismiss();
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    ExpensesListFragment.adapter.notifyItemInserted(0);
                    ExpensesListFragment.layoutManager.scrollToPosition(0);
                }
                break;
            case R.id.newCategory_colorBtn:
                ColorPickerDialog.newInstance().show(getFragmentManager(), "colorPickerDialog");
                break;
        }
    }
    // фильтр для суммы
    TextWatcher moneyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            char[] array = s.toString().toCharArray();
            if (array.length != 0) {
                if (array[0] == '0')
                    moneyET.setError(getResources().getString(R.string.newEntry_dialog_error));
            }
        }
    };
    // RegEx для категории
    InputFilter categoryFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.toString().matches("[\\p{Alnum}]+")) return source;
            return "";
        }

    };
    // сортировка данных БД
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
