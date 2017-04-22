package zlotnikov.personalexpenses.model.Listeners;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import zlotnikov.personalexpenses.model.Adapters.ExpensesListRecyclerAdapter;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Dialogs.DeleteDialog;
import zlotnikov.personalexpenses.presenter.PieChartFragment;

public class MySwipeHelper extends ItemTouchHelper.SimpleCallback{

    //private ExpensesListRecyclerAdapter adapter;
    private FragmentManager fragmentManager;

    public MySwipeHelper(/*ExpensesListRecyclerAdapter adapter,*/ FragmentManager fragmentManager) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        //this.adapter = adapter;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // на свайп запускать диалог и передать позицию элемента
        DeleteDialog.newInstance(viewHolder.getAdapterPosition()).show(fragmentManager, "deleteDialog");
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
}
