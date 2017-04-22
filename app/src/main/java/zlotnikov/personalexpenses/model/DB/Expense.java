package zlotnikov.personalexpenses.model.DB;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.Required;
// класс для записи в БД
public class Expense extends RealmObject{
    // @Required поле не может быть пустым
    @Required
    private Integer money;
    // @Index сортировка по этому полю будет быстрее, но запись станет медленнее(то что нужно)
    @Index
    @Required
    private Date date;
    @Required
    private String category;
    @Required
    private String color;

    public Expense() {
    }

    public Expense(int money, Date date, String category, String color) {
        this.money = money;
        this.date = date;
        this.category = category;
        this.color = color;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMoney() {
        return money;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getColor() {
        return color;
    }
}
