package zlotnikov.personalexpenses.model.POJO;

// класс для EventBus
public class ColorEvent {
    private int color;

    public ColorEvent(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
