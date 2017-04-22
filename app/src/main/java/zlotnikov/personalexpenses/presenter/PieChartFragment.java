package zlotnikov.personalexpenses.presenter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.PieChart;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.Adapters.LegendRecyclerViewAdapter;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.MyPieChart;

public class PieChartFragment extends Fragment{

    // пончик
    public static MyPieChart pieChart;
    public static LinearLayoutManager layoutManager;
    public static LegendRecyclerViewAdapter adapter;

    public static Fragment newInstance(){return new PieChartFragment();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        // инициализация пончика
        pieChart = new MyPieChart(getContext(), getFragmentManager(), (PieChart) view.findViewById(R.id.chart));

        // инициализация списка под пончиком
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.legend_RecyclerView);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(getContext()));
        // передаем в качестве параметра переменную-запрос
        adapter = new LegendRecyclerViewAdapter(RealmData.query, getFragmentManager());
        recyclerView.setAdapter(adapter);

        return view;
    }
}
