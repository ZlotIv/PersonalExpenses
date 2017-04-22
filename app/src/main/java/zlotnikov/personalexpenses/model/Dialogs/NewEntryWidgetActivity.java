package zlotnikov.personalexpenses.model.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;

public class NewEntryWidgetActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText moneyET;
    private Spinner spinner;
    private ArrayList<String> colorList;
    private ArrayList<String> categoryList;
    // 1-я часть установки нового дефолтного шрифта
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RealmData.returnAllData().isEmpty()){
            // ужасно, но выбора нет
            Intent intent = new Intent(this, NewCategoryWidgetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        // 2-я часть установки нового дефолтного шрифта
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.new_entry_dialog);

        Button positiveBtn = (Button)findViewById(R.id.newEntry_positiveBtn);
        Button neutralBtn = (Button)findViewById(R.id.newEntry_neutralBtn);
        Button negativeBtn = (Button)findViewById(R.id.newEntry_negativeBtn);
        moneyET = (EditText)findViewById(R.id.newEntry_moneyET);
        moneyET.addTextChangedListener(moneyTextWatcher);
        spinner = (Spinner)findViewById(R.id.newEntry_spinner);
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        neutralBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        sortRealmList(RealmData.returnAllData());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);

        spinner.setAdapter(adapter);

        negativeBtn.setOnClickListener(this);
        neutralBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
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
                    // добавляем данные в БД
                    RealmData.addItem(Integer.parseInt(moneyET.getText().toString()), spinner.getSelectedItem().toString(), colorList.get(spinner.getSelectedItemPosition()));
                    finishApp();
                }
                break;
            case R.id.newEntry_neutralBtn:
                Intent intent = new Intent(this, NewCategoryWidgetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.newEntry_negativeBtn:
                finishApp();
                break;
        }
    }
    // фильтря для суммы
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
    // метод закрытия приложения
    private void finishApp(){
        if(Build.VERSION.SDK_INT >= 21){
            finishAndRemoveTask();
        } else if(Build.VERSION.SDK_INT >= 16){
            finishAffinity();
        } else {
            finish();
        }
    }
}
