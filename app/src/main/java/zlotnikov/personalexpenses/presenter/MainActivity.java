package zlotnikov.personalexpenses.presenter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import io.realm.Realm;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.Adapters.MainViewPagerAdapter;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Listeners.MyDrawerListener;
import zlotnikov.personalexpenses.model.Listeners.MyFabListener;

public class MainActivity extends AppCompatActivity {

    // массив иконок для ViewPager
    private final int[] tabIcons = {R.drawable.pie_chart, R.drawable.expenses_list};
    // плавающая меню-кнопка
    private FloatingActionsMenu fabMenu;
    // переменная БД
    public Realm realm;
    // Заголовок
    public Toolbar toolbar;

    // 1-я часть установки нового дефолтного шрифта
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2-я часть установки нового дефолтного шрифта
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/OpenSans-Light.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build());

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.drawer_month_stat_title);
        // получаем данные за месяц и меняем заголовок
        RealmData.getMonthList();
        setSupportActionBar(toolbar);
        // устанавливаем штору
        setDrawer(toolbar);
        // инициализация переменной БД
        realm = Realm.getDefaultInstance();
        // инициализация ViewPager и установка адаптера для него
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        // инициализация TabLayout с иконками
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        // инициализация меню-кнопки и её подкнопок
        fabMenu = (FloatingActionsMenu)findViewById(R.id.fab);
        FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fabPlus);
        FloatingActionButton fabMinus = (FloatingActionButton) findViewById(R.id.fabMinus);
        // слушатель для меню-кнопки
        MyFabListener listener = new MyFabListener(fabMenu, this.getSupportFragmentManager());
        // установка для подкнопки "плюс"
        fabPlus.setIcon(R.drawable.plus_fab);
        fabPlus.setColorNormalResId(R.color.colorGreen);
        fabPlus.setColorPressedResId(R.color.colorGreenPressed);
        fabPlus.setOnClickListener(listener);
        // установка для подкнопки "минус"
        fabMinus.setIcon(R.drawable.minus_fab);
        fabMinus.setColorNormalResId(R.color.colorRed);
        fabMinus.setColorPressedResId(R.color.colorRedPressed);
        fabMinus.setOnClickListener(listener);
    }
    // если меню-кнопка раскрыта, то на нажатие "назад" закрыть её
    @Override
    public void onBackPressed() {
        if (fabMenu.isExpanded()){fabMenu.collapse();}
        else {super.onBackPressed();}

    }
    // метод установки шторы
    private Drawer setDrawer(Toolbar toolbar){
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerWidthDp(250)
                // установка заголовка для шторы
                .withAccountHeader(new AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.drawer_header)
                        .withHeaderBackgroundScaleType(ImageView.ScaleType.FIT_XY)
                        .withDividerBelowHeader(true)
                        .build())
                .addDrawerItems(new SectionDrawerItem().withName(R.string.drawer_stat).withDivider(false).withTypeface(Typeface.DEFAULT)
                        , new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_day_stat).withTypeface(Typeface.DEFAULT)
                        , new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_month_stat).withTypeface(Typeface.DEFAULT)
                        , new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_year_stat).withTypeface(Typeface.DEFAULT)
                        , new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_allTime_stat).withTypeface(Typeface.DEFAULT)
                        , new PrimaryDrawerItem().withIdentifier(5).withName(R.string.drawer_specific_stat).withTypeface(Typeface.DEFAULT)
                        , new DividerDrawerItem()
                        , new PrimaryDrawerItem().withIdentifier(6).withName(R.string.drawer_clear_stat).withTypeface(Typeface.DEFAULT))
                .withOnDrawerItemClickListener(new MyDrawerListener(toolbar, getSupportFragmentManager()))
                .build();
    }

    //при уничтожении активити закрыть БД
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
