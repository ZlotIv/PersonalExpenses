package zlotnikov.personalexpenses.model.Dialogs;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;


public class SpecificDateDialog extends DialogFragment implements View.OnClickListener, OnRangeSelectedListener{

    private Date[] dateArray = new Date[2];
    private Date from;
    private Date to;
    // переменная для вида операции(показать или удалить)
    private boolean operation;

    public static SpecificDateDialog newInstance(boolean operation) {

        Bundle args = new Bundle();
        args.putBoolean("operation", operation);
        SpecificDateDialog fragment = new SpecificDateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            from = new SimpleDateFormat("yyyy").parse("2017");
            to = new Date();
            operation = getArguments().getBoolean("operation");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.specific_date_dialog, container, false);
        Button negativeBtn = (Button)view.findViewById(R.id.specificDate_negativeBtn);
        Button positiveBtn = (Button)view.findViewById(R.id.specificDate_positiveBtn);
        MaterialCalendarView calendarView = (MaterialCalendarView)view.findViewById(R.id.specificDate_calendarView);
        // т.к. изменили дефолтный шрифт, то для кнопок диалога ставим программно предыдущий дефолтный
        positiveBtn.setTypeface(Typeface.DEFAULT);
        negativeBtn.setTypeface(Typeface.DEFAULT);

        calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .setMinimumDate(from)
                .setMaximumDate(to)
                .commit();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.setOnRangeSelectedListener(this);

        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.specificDate_negativeBtn:
                this.dismiss();
                break;
            case R.id.specificDate_positiveBtn:
                // для операции true
                if (operation) {
                    // если данные есть с БД то показываем их
                    if (dateArray[0] != null && dateArray[1] != null) {
                        if (RealmData.returnSpecificList(dateArray).isEmpty()){
                            Toast.makeText(getContext(), getResources().getString(R.string.datePicker_dialog_date_error), Toast.LENGTH_SHORT).show();
                        } else {
                            RealmData.getSpecificList(dateArray);
                            ExpensesListFragment.adapter.update();
                            PieChartFragment.pieChart.update();
                            PieChartFragment.adapter.updateList();
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.drawer_specific_stat_title));
                        }
                        this.dismiss();
                        break;
                    }
                // для операции false удаляем данные
                } else if (!operation) {
                    // если данные есть с БД то удаляем их
                    if (dateArray[0] != null && dateArray[1] != null) {
                        if (RealmData.returnSpecificList(dateArray).isEmpty()) {
                            Toast.makeText(getContext(), getResources().getString(R.string.datePicker_dialog_date_error), Toast.LENGTH_SHORT).show();
                        } else {
                            DeleteDialog.newInstance(dateArray[0], dateArray[1]).show(getFragmentManager(), "deleteDialog");
                        }
                        this.dismiss();
                        break;
                    }
                }
        }
    }
    // получаем даты "ОТ" и "ДО"
    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        dateArray[0] = dates.get(0).getDate();
        dateArray[1] = dates.get(dates.size() - 1).getDate();
    }
}
