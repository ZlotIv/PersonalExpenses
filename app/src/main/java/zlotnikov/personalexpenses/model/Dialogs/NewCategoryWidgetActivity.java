package zlotnikov.personalexpenses.model.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;

public class NewCategoryWidgetActivity extends AppCompatActivity implements View.OnClickListener{

    private FancyButton colorBtn;
    private int color;
    private EditText categoryET;
    private EditText moneyET;
    private ArrayList<String> categoryList;
    private Realm realm;

    // 1-я часть установки нового дефолтного шрифта
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2-я часть установки нового дефолтного шрифта
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.new_category_dialog);

        realm = Realm.getDefaultInstance();

        moneyET = (EditText)findViewById(R.id.newCategory_moneyET);
        categoryET = (EditText)findViewById(R.id.newCategory_categoryET);
        colorBtn = (FancyButton)findViewById(R.id.newCategory_colorBtn);
        Button negativeBtn = (Button)findViewById(R.id.newCategory_negativeBtn);
        Button positiveBtn = (Button)findViewById(R.id.newCategory_positiveBtn);

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

        moneyET.addTextChangedListener(moneyTextWatcher);
        categoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NewCategoryDialog.LETTERS_COUNT), categoryFilter});
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", color);
    }

    // получаем результат активити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            color = data.getExtras().getInt("color");
            colorBtn.setBackgroundColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newCategory_negativeBtn:
                finishApp();
                break;
            case R.id.newCategory_positiveBtn:
                if (categoryList.contains(categoryET.getText().toString())){
                    Toast.makeText(this, getResources().getString(R.string.category_exist_error), Toast.LENGTH_SHORT).show();
                }
                else if (moneyET.getError() == null && !moneyET.getText().toString().isEmpty() && !categoryET.getText().toString().isEmpty()) {
                    RealmData.addItem(Integer.parseInt(moneyET.getText().toString()), categoryET.getText().toString(),"#" + Integer.toHexString(color));
                    finishApp();
                }
                break;
            case R.id.newCategory_colorBtn:
                Intent intent = new Intent(this, ColorPickerWidgetActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

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

    private void finishApp(){
        if(Build.VERSION.SDK_INT >= 21){
            finishAndRemoveTask();
        } else if(Build.VERSION.SDK_INT >= 16){
            finishAffinity();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
