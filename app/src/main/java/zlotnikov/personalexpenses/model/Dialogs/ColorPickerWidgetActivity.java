package zlotnikov.personalexpenses.model.Dialogs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import zlotnikov.personalexpenses.R;

public class ColorPickerWidgetActivity extends AppCompatActivity implements View.OnClickListener, OnColorSelectedListener {

    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker_dialog);

        ColorPickerView colorPickerView = (ColorPickerView)findViewById(R.id.color_picker_view);
        Button positiveBtn = (Button)findViewById(R.id.color_positiveBtn);
        Button negativeBtn = (Button)findViewById(R.id.color_negativeBtn);


        colorPickerView.addOnColorSelectedListener(this);
        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.color_positiveBtn:
                if (color != 0) {
                    // вернуть цвет
                    Intent intent = new Intent();
                    intent.putExtra("color", color);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.color_negativeBtn:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onColorSelected(int i) {
        color = i;
    }

}
