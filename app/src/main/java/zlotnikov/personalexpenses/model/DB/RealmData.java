package zlotnikov.personalexpenses.model.DB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.realm.Realm;
import io.realm.Sort;

public class RealmData {
    // форматы для записи даты
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM.yyyy");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    // главная переменная "запрос"
    public static List<Expense> query;
    // получаем данные за день
    public static void getDayList(){

        Date date = null;
        try {
            date = DAY_FORMAT.parse(DAY_FORMAT.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Ошибка Парсинга дня");
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query = realm.where(Expense.class).greaterThan("date", date).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
    }
    // получаем данные за месяц
    public static void getMonthList(){

        Date date = null;
        try {
            date = MONTH_FORMAT.parse(MONTH_FORMAT.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Ошибка Парсинга месяца");
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query = realm.where(Expense.class).greaterThan("date", date).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
    }
    // получаем данные за год
    public static void getYearList(){

        Date date = null;
        try {
            date = YEAR_FORMAT.parse(YEAR_FORMAT.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Ошибка Парсинга года");
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query = realm.where(Expense.class).greaterThan("date", date).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
    }
    // получаем данные за определенное время
    public static void getSpecificList(Date[] array){

        Date from = array[0];
        Date to = array[1];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        calendar.add(Calendar.DATE, 1);
        to = calendar.getTime();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query = realm.where(Expense.class).greaterThan("date", from).lessThan("date", to).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
    }
    // получаем данные за всё время
    public static void getAllTimeList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query = realm.where(Expense.class).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
    }
    // возвращаем список за определенное время
    public static List<Expense> returnSpecificList(Date[] array){
        Date from = array[0];
        Date to = array[1];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        calendar.add(Calendar.DATE, 1);
        to = calendar.getTime();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense>realmResults = realm.where(Expense.class).greaterThan("date", from).lessThan("date", to).findAllSorted("date", Sort.DESCENDING);
        realm.commitTransaction();
        return realmResults;
    }
    // удаляем за определенное время
    public static void deleteSpecificList(Date[] array){
        Date from = array[0];
        Date to = array[1];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        calendar.add(Calendar.DATE, 1);
        to = calendar.getTime();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> list = realm.where(Expense.class).greaterThan("date", from).lessThan("date", to).findAllSorted("date", Sort.DESCENDING);
        for (Expense i :list) i.deleteFromRealm();
        realm.commitTransaction();
    }
    // удаляем данные с определенным название(категорию)
    public static void deleteCategory(String category){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> list = realm.where(Expense.class).equalTo("category", category).findAll();
        for (Expense i : list){
            i.deleteFromRealm();
        }
        realm.commitTransaction();
    }
    // удаляем определенный элемент
    public static void deleteItem(int position){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        query.get(position).deleteFromRealm();
        realm.commitTransaction();
    }
    // удаляем последний элемент
    public static void deleteLastItemFromAllData(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Expense lastItem = realm.where(Expense.class).findAllSorted("date").last();
        lastItem.deleteFromRealm();
        realm.commitTransaction();
    }
    // добавляем эелемент
    public static void addItem(int money, String category, String color){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Expense expense = realm.createObject(Expense.class);
        expense.setDate(new Date());
        expense.setMoney(money);
        expense.setCategory(category);
        expense.setColor(color);
        realm.commitTransaction();
    }
    // изменяем название категории
    public static void changeCategory(String oldCategory, String newCategory){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> list = realm.where(Expense.class).equalTo("category", oldCategory).findAll();
        for (Expense i : list){
            i.setCategory(newCategory);
        }
        realm.commitTransaction();
    }
    // изменяем название категории и цвет
    public static void changeCategoryAndColor(String oldCategory, String newCategory, String color){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> list = realm.where(Expense.class).equalTo("category", oldCategory).findAll();
        for (Expense i : list){
            i.setCategory(newCategory);
            i.setColor(color);
        }
        realm.commitTransaction();
    }
    // изменяем цвет категории
    public static void changeColor(String category, String color){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> list = realm.where(Expense.class).equalTo("category", category).findAll();
        for (Expense i : list){
            i.setColor(color);
        }
        realm.commitTransaction();
    }
    // возвращаем все данные
    public static List<Expense> returnAllData(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<Expense> realmResults = realm.where(Expense.class).findAll();
        realm.commitTransaction();
        return realmResults;
    }
}
