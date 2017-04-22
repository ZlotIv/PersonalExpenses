package zlotnikov.personalexpenses.model.Dialogs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;


public class DeleteDialog extends DialogFragment implements View.OnClickListener{

    // переменная-переключатель
    private boolean deleteSwitch = false;
    // для каждого случая свой newInstance()
    public static DeleteDialog newInstance() {

        Bundle args = new Bundle();
        DeleteDialog fragment = new DeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static DeleteDialog newInstance(String category) {

        Bundle args = new Bundle();
        args.putString("category", category);
        DeleteDialog fragment = new DeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }
    public static DeleteDialog newInstance(Date from, Date to) {
        Long longFrom = from.getTime();
        Long longTo = to.getTime();
        Bundle args = new Bundle();
        args.putLong("longFrom", longFrom);
        args.putLong("longTo", longTo);
        DeleteDialog fragment = new DeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }
    public static DeleteDialog newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        DeleteDialog fragment = new DeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.delete_dialog, container, false);

        TextView messageTV = (TextView)view.findViewById(R.id.deleteDialog_message_TV);
        Button negativeBtn = (Button)view.findViewById(R.id.deleteDialog_negativeBtn);
        Button positiveBtn = (Button)view.findViewById(R.id.deleteDialog_positiveBtn);
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
        // в зависимости от аргументов устанавливаем текст
        if (getArguments().containsKey("category")){
            messageTV.setText(getResources().getString(R.string.delete_dialog_category));
        }

        else if (getArguments().containsKey("longFrom") && getArguments().containsKey("longTo")){
            messageTV.setText(getResources().getString(R.string.delete_dialog_date));
        }
        else if (getArguments().isEmpty()){
            messageTV.setText(getResources().getString(R.string.delete_dialog_last_entry));
        }
        else if (getArguments().containsKey("position")){
            if (getArguments().getInt("position") == 0){
                messageTV.setText(getResources().getString(R.string.delete_dialog_last_entry));
            } else {
                messageTV.setText(getResources().getString(R.string.delete_dialog_entry));
            }
        }

        return view;
    }
    // если переключатель false (т.е. просто закрыли диалог), то оставляем элемент как есть
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getArguments().containsKey("position")){
            if (!deleteSwitch) ExpensesListFragment.adapter.notifyItemChanged(getArguments().getInt("position"));
        }
    }
    // в зависимости от задачи диалога выполняем определенные действия
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleteDialog_negativeBtn:
                // если переключатель false, то оставляем элемент как есть
                if (getArguments().containsKey("position")){
                    ExpensesListFragment.adapter.notifyItemChanged(getArguments().getInt("position"));
                    deleteSwitch = true;
                    this.dismiss();
                }
                this.dismiss();
                break;


            case R.id.deleteDialog_positiveBtn:
                if (getArguments().containsKey("category")){
                    // удаление категории из БД
                    RealmData.deleteCategory(getArguments().getString("category"));
                    // обновление Views
                    ExpensesListFragment.adapter.update();
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    this.dismiss();
                }
                else if (getArguments().containsKey("longFrom") && getArguments().containsKey("longTo")){

                    Date[] dateArray = {new Date(getArguments().getLong("longFrom")), new Date(getArguments().getLong("longTo"))};
                    RealmData.deleteSpecificList(dateArray);
                    ExpensesListFragment.adapter.update();
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    this.dismiss();
                }
                else if (getArguments().isEmpty()){
                    RealmData.deleteItem(0);
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    ExpensesListFragment.adapter.notifyItemRemoved(0);
                    ExpensesListFragment.layoutManager.scrollToPosition(0);
                    this.dismiss();
                }
                if (getArguments().containsKey("position")){
                    RealmData.deleteItem(getArguments().getInt("position"));
                    PieChartFragment.pieChart.update();
                    PieChartFragment.adapter.updateList();
                    ExpensesListFragment.adapter.notifyItemRemoved(getArguments().getInt("position"));
                    this.dismiss();
                }
                break;
        }
    }
}
