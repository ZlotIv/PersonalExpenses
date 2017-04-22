package zlotnikov.personalexpenses.model.Dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.MainActivity;
import zlotnikov.personalexpenses.presenter.PieChartFragment;

public class DeleteLastItemWidgetActivity extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;
    // 1-я часть установки нового дефолтного шрифта
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // открытие БД
        realm = Realm.getDefaultInstance();
        // 2-я часть установки нового дефолтного шрифта
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.delete_dialog);

        Button positiveBtn = (Button)findViewById(R.id.deleteDialog_positiveBtn);
        Button negativeBtn = (Button)findViewById(R.id.deleteDialog_negativeBtn);
        TextView messageTV = (TextView)findViewById(R.id.deleteDialog_message_TV);
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        messageTV.setText(getResources().getString(R.string.delete_dialog_last_entry));
        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleteDialog_negativeBtn:
                finishApp();
                break;
            case R.id.deleteDialog_positiveBtn:
                if (!RealmData.returnAllData().isEmpty()){
                    RealmData.deleteLastItemFromAllData();
                    finishApp();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.delete_dialog_empty), Toast.LENGTH_SHORT).show();
                    finishApp();
                }
                break;
        }
    }
    // метод полного закрытия приложения
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
        // закрытие БД
        realm.close();
    }
}
