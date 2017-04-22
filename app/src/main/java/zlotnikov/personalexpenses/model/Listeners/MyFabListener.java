package zlotnikov.personalexpenses.model.Listeners;

import android.support.v4.app.FragmentManager;
import android.view.View;
import toan.android.floatingactionmenu.FloatingActionsMenu;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Dialogs.DeleteDialog;
import zlotnikov.personalexpenses.model.Dialogs.NewCategoryDialog;
import zlotnikov.personalexpenses.model.Dialogs.NewEntryDialog;


public class MyFabListener implements View.OnClickListener {

    private FloatingActionsMenu fabMenu;
    private FragmentManager fragmentManager;

    public MyFabListener(FloatingActionsMenu fabMenu, FragmentManager fragmentManager) {
        this.fabMenu = fabMenu;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabPlus:
                // если данных нет
                if (RealmData.returnAllData().isEmpty()){
                    // то запускаем диалог новой категории
                    NewCategoryDialog.newInstance().show(fragmentManager, "newCategoryDialog");
                } else {
                    // иначе запускаем диалог новой записи
                    NewEntryDialog.newInstance().show(fragmentManager, "newItemDialog");
                }
                // если меню-кнопка раскрыта, то закрыть
                if (fabMenu.isExpanded()) fabMenu.collapse();
                break;
            case R.id.fabMinus:
                // если данных есть, то запускаем диалог удаления
                if (!RealmData.returnAllData().isEmpty()) {
                    DeleteDialog.newInstance().show(fragmentManager, "deleteDialog");
                }
                // если меню-кнопка раскрыта, то закрыть
                if (fabMenu.isExpanded()) fabMenu.collapse();
                break;
            default:
                break;
        }
    }
}
