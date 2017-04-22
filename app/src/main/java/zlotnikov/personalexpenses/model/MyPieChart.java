package zlotnikov.personalexpenses.model;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Listeners.MyPieChartListener;

// пончик
public class MyPieChart extends PieChart {


    private PieChart mChart;
    private PieData pieData;
    private int totalSum;
    private boolean updateCount = false;
    private List<Integer> colorList;
    private List<PieEntry> pieEntryList;


    public MyPieChart(Context context, FragmentManager fragmentManager, PieChart pieChart) {
        super(context);
        this.mChart = pieChart;
        // прозрачный фон
        mChart.setBackgroundColor(Color.TRANSPARENT);
        // установить проценты
        mChart.setUsePercentValues(true);
        // убрать подпись
        mChart.getDescription().setEnabled(false);
        // отступы
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mChart.setExtraOffsets(23, 0, 23, 0);
        } else {
            mChart.setExtraOffsets(23, 0, 23, 0);
        }
        // сила прокрутки
        mChart.setDragDecelerationFrictionCoef(0.8f);
        // видно ли дыру в центре
        mChart.setDrawHoleEnabled(true);
        // цвет дыры
        mChart.setHoleColor(Color.TRANSPARENT);
        // видны ли ломтики за дырой
        mChart.setDrawSlicesUnderHole(false);
        // цвет для тени от дыры
        mChart.setTransparentCircleColor(Color.WHITE);
        // глубина тени
        mChart.setTransparentCircleAlpha(105);
        // радиус дыры
        mChart.setHoleRadius(60f);
        // шрифт
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        // размер текста
        mChart.setCenterTextSize(20);
        // показывать ли текст в центре
        mChart.setDrawCenterText(true);
        // радиус тени
        mChart.setTransparentCircleRadius(65f);
        // угол
        mChart.setRotationAngle(0);
        // позволить ли прокручивать
        mChart.setRotationEnabled(true);
        // увеличение ломтика при нажатии
        mChart.setHighlightPerTapEnabled(true);
        // установка цвета для названия ломтика
        mChart.setEntryLabelColor(Color.BLACK);
        // установка текста для названия ломтика
        mChart.setEntryLabelTextSize(16);
        // не показывать легенду пончика
        mChart.getLegend().setEnabled(false);
        // слушатель
        mChart.setOnChartValueSelectedListener(new MyPieChartListener(context, fragmentManager, mChart));

        // установка настроек
        setSettings();

        // текст в центре
        mChart.setCenterText(String.valueOf(totalSum));
        mChart.setData(pieData);
        // анимация зарисовки пончика
        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        mChart.invalidate();
    }

    private void sortRealmList(List<Expense> realmList){
        pieEntryList = new ArrayList<>();
        colorList = new ArrayList<>();
        // берем первый элемент из БД
        for (int i = 0; i < realmList.size(); i++){
            // выключатель
            boolean count = false;
            // сумма
            float sum = 0;
            // категория элемента
            String category = realmList.get(i).getCategory();
            // проверяем имеется ли такая категория уже в списке
            for (int j = 0; j < pieEntryList.size(); j++){
                if (pieEntryList.get(j).getLabel().equals(category)){
                    // если имеется то меняем счетчик
                    count = true;
                }
            }
            // если не содержит, то идем по первому списку дальше и проверяем есть ли сходство в категориях
            if (!count) {
                for (int k = i; k < realmList.size(); k++) {
                    // если есть, то плюсуем сумму
                    if (realmList.get(k).getCategory().equals(category)) {
                        sum += realmList.get(k).getMoney();
                    }
                }
                // по завершению, записываем
                pieEntryList.add(new PieEntry(sum, category));
                colorList.add(Color.parseColor(realmList.get(i).getColor()));
            }
        }
    }
    // обновление пончика
    public void update(){
        updateCount = true;
        setSettings();
        // текст в центре
        mChart.setCenterText(String.valueOf(totalSum));
        mChart.setData(pieData);
        // анимация зарисовки пончика
        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        mChart.invalidate();
    }
    // установка настроек
    private void setSettings(){
        totalSum = 0;
        sortRealmList(RealmData.query);
        PieDataSet pieDataSet;
        // если лист пустой
        if (pieEntryList.isEmpty()){
            List<PieEntry> emptyList = new ArrayList<>();
            emptyList.add(new PieEntry(100f, getContext().getResources().getString(R.string.pie_chart_empty)));
            pieDataSet = new PieDataSet(emptyList, "");
            pieDataSet.setDrawValues(false);
            // цвета ломтиков
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        } else {
            // если не пустой
            pieDataSet = new PieDataSet(pieEntryList, "");
            for (PieEntry pieEntry : pieEntryList){
                totalSum += (int)pieEntry.getValue();
            }
            // цвета ломтиков
            pieDataSet.setColors(colorList);
        }
        //pieDataSet.setDrawValues(false);
        pieDataSet.setSliceSpace(0.01f);
        // вертикальная длинна палочки
        pieDataSet.setValueLinePart1OffsetPercentage(80.f);
        // вертикальное смещение палочки
        pieDataSet.setValueLinePart1Length(0.2f);
        // горизонтальное смещение палочки
        pieDataSet.setValueLinePart2Length(0.4f);
        // установка текста Y снаружи
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        if (!updateCount) {
            pieData = new PieData(pieDataSet);
        } else {
            pieData.setDataSet(pieDataSet);
        }
        // установка процентов
        pieData.setValueFormatter(new PercentFormatter());
        // размер процентов
        pieData.setValueTextSize(12f);
        Typeface typeface = Typeface.DEFAULT_BOLD;
        pieData.setValueTypeface(typeface);
        pieData.setValueTextColor(Color.BLACK);
        if (updateCount){
            pieData.notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
    }
}
