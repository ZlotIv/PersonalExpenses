package zlotnikov.personalexpenses.model.Dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import org.greenrobot.eventbus.EventBus;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.POJO.ColorEvent;

public class ColorPickerDialog extends DialogFragment implements View.OnClickListener, OnColorSelectedListener {

    private int color;

    public static ColorPickerDialog newInstance() {

        Bundle args = new Bundle();

        ColorPickerDialog fragment = new ColorPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.color_picker_dialog, null);

        ColorPickerView colorPickerView = (ColorPickerView) view.findViewById(R.id.color_picker_view);
        Button positiveBtn = (Button) view.findViewById(R.id.color_positiveBtn);
        Button negativeBtn = (Button) view.findViewById(R.id.color_negativeBtn);


        colorPickerView.addOnColorSelectedListener(this);
        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.color_positiveBtn:
                if (color != 0) {
                    // запустить в шину POJO элемент
                    EventBus.getDefault().post(new ColorEvent(color));
                    this.dismiss();
                }
                break;
            case R.id.color_negativeBtn:
                this.dismiss();
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
