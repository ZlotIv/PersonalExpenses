package zlotnikov.personalexpenses.model.Dialogs;

import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;

public class NewEntryDialog extends DialogFragment implements View.OnClickListener{

    private EditText moneyET;
    private Spinner spinner;
    private ArrayList<String> colorList;
    private ArrayList<String> categoryList;


    public static NewEntryDialog newInstance() {

        Bundle args = new Bundle();

        NewEntryDialog fragment = new NewEntryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.new_entry_dialog, container, false);
        Button negativeBtn = (Button)view.findViewById(R.id.newEntry_negativeBtn);
        Button neutralBtn = (Button)view.findViewById(R.id.newEntry_neutralBtn);
        Button positiveBtn = (Button)view.findViewById(R.id.newEntry_positiveBtn);
        moneyET = (EditText)view.findViewById(R.id.newEntry_moneyET);
        moneyET.addTextChangedListener(moneyTextWatcher);
        spinner = (Spinner)view.findViewById(R.id.newEntry_spinner);
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        neutralBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);
        // сортируем данные из БД
        sortRealmList(RealmData.returnAllData());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);

        spinner.setAdapter(adapter);

        negativeBtn.setOnClickListener(this);
        neutralBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);

        return view;
    }

    private void sortRealmList(List<Expense> realmList){
        categoryList = new ArrayList<>();
        colorList = new ArrayList<>();
        // берем первый элемент из БД
        for (int i = 0; i < realmList.size(); i++){
            // проверка
            boolean count = false;
            // категория элемента
            String category = realmList.get(i).getCategory();
            String color = realmList.get(i).getColor();
            // проверяем имеется ли такая категория уже в списке
            for (int j = 0; j < categoryList.size(); j++){
                if (categoryList.get(j).equals(category)){
                    // если имеется то меняем счетчик
                    count = true;
                }
            }
            // если не содержит, то записываем
            if (!count) {
                categoryList.add(category);
                colorList.add(color);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newEntry_positiveBtn:
                if (moneyET.getError() == null && !moneyET.getText().toString().isEmpty()) {
                    // добавление элемента в БД
                    RealmData.addItem(Integer.parseInt(moneyET.getText().toString()), spinner.getSelectedItem().toString(), colorList.get(spinner.getSelectedItemPosition()));
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    ExpensesListFragment.adapter.notifyItemInserted(0);
                    ExpensesListFragment.layoutManager.scrollToPosition(0);
                    this.dismiss();
                }
                break;
            case R.id.newEntry_neutralBtn:
                // запускаем диалог новой категории
                NewCategoryDialog.newInstance().show(getFragmentManager(), "newCategoryDialog");
                this.dismiss();
                break;
            case R.id.newEntry_negativeBtn:
                this.dismiss();
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
}
