package zlotnikov.personalexpenses;

import android.app.Application;

import io.realm.Realm;

// класс инициализации БД
public class RealmInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
