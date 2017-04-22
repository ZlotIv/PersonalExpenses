package zlotnikov.personalexpenses.presenter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.Adapters.ExpensesListRecyclerAdapter;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Listeners.MySwipeHelper;

public class ExpensesListFragment extends Fragment {

    public static ExpensesListRecyclerAdapter adapter;
    public static LinearLayoutManager layoutManager;


    public static ExpensesListFragment newInstance(){return new ExpensesListFragment();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses_list, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.expensesList_recyclerView);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(getContext()));
        adapter = new ExpensesListRecyclerAdapter(RealmData.query);
        recyclerView.setAdapter(adapter);
        // установка линии под(над) элементом списка
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        // TouchHelper для возможности удалить элемент свайпом влево
        ItemTouchHelper helper = new ItemTouchHelper(new MySwipeHelper(/*adapter,*/ getFragmentManager()));
        helper.attachToRecyclerView(recyclerView);

        return view;
    }
}
