package zlotnikov.personalexpenses.model.Listeners;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Dialogs.PieChartItemDialog;

public class MyPieChartListener implements OnChartValueSelectedListener {

    private FragmentManager fragmentManager;
    private Context context;
    private PieChart pieChart;

    public MyPieChartListener(Context context, FragmentManager fragmentManager, PieChart pieChart) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.pieChart = pieChart;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        pieChart.highlightValues(null);
        // получаем ломтик пончика
        PieEntry pieEntry = (PieEntry) e;

        // если ломтик не содержит такую то запись, то получить данные из БД о ломтике и передать в диалог
        if (!pieEntry.getLabel().equals(context.getResources().getString(R.string.pie_chart_empty))) {
            String category = pieEntry.getLabel();
            String color = "";
            int money = 0;
            for (Expense i : RealmData.query) {
                if (i.getCategory().equals(category)) {
                    if (!color.equals(i.getCategory())) {
                        color = i.getColor();
                    }
                    money += i.getMoney();
                }
            }
            onNothingSelected();
            PieChartItemDialog.newInstance(category, color, money).show(fragmentManager, "pieChartItemDialog");
        }
    }
    @Override
    public void onNothingSelected() {
    }
}
