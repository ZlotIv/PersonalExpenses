package zlotnikov.personalexpenses.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

// класс кастомного круга для списков
public class CircleView extends View {

    private Paint paint;
    private int circleColor;

    public CircleView(Context context) {
        super(context);
        paint = new Paint();
        // сглаживание краёв
        paint.setAntiAlias(true);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    // установка цвета и перерисовка
    public void setCircleColor(int color){
        this.circleColor = color;
        invalidate();
    }
    public int getCircleColor(){
        return circleColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // установка высоты и ширины
        int w = getWidth();
        int h = getHeight();
        // отступы
        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        // фактические ширина и высота
        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);
        // радиус
        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);
        // установка цвета и рисование
        paint.setColor(circleColor);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
