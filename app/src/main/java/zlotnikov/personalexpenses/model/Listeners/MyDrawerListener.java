package zlotnikov.personalexpenses.model.Listeners;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Dialogs.SpecificDateDialog;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;


public class MyDrawerListener implements Drawer.OnDrawerItemClickListener {


    private FragmentManager fragmentManager;
    private Toolbar toolbar;

    public MyDrawerListener(Toolbar toolbar, FragmentManager fragmentManager) {
        this.toolbar = toolbar;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        // в зависимости от id получаем из БД нужные данные и перерисовываем пончик и списки
        switch ((int)drawerItem.getIdentifier()){
            case 1:
                RealmData.getDayList();
                toolbar.setTitle(R.string.drawer_day_stat_title);
                ExpensesListFragment.adapter.update();
                PieChartFragment.pieChart.update();
                PieChartFragment.adapter.updateList();
                break;
            case 2:
                RealmData.getMonthList();
                toolbar.setTitle(R.string.drawer_month_stat_title);
                ExpensesListFragment.adapter.update();
                PieChartFragment.pieChart.update();
                PieChartFragment.adapter.updateList();
                break;
            case 3:
                RealmData.getYearList();
                toolbar.setTitle(R.string.drawer_year_stat_title);
                ExpensesListFragment.adapter.update();
                PieChartFragment.pieChart.update();
                PieChartFragment.adapter.updateList();
                break;
            case 4:
                RealmData.getAllTimeList();
                toolbar.setTitle(R.string.drawer_allTime_stat_title);
                ExpensesListFragment.adapter.update();
                PieChartFragment.pieChart.update();
                PieChartFragment.adapter.updateList();
                break;
            case 5:
                // вызов диалога
                SpecificDateDialog.newInstance(true).show(fragmentManager, "specificDateDialog");
                break;
            case 6:
                // вызов диалога
                SpecificDateDialog.newInstance(false).show(fragmentManager, "specificDateDialog");
                break;
            default:
                break;
        }

        return false;
    }
}
