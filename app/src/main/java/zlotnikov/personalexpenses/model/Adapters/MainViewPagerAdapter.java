package zlotnikov.personalexpenses.model.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;
import zlotnikov.personalexpenses.presenter.ExpensesListFragment;
import zlotnikov.personalexpenses.presenter.PieChartFragment;


public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList.add(PieChartFragment.newInstance());
        fragmentList.add(ExpensesListFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
