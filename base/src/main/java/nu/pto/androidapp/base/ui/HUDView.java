package nu.pto.androidapp.base.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import nu.pto.androidapp.base.R;

public class HUDView extends View {

    private float completionLevel;


    public HUDView(Context context) {
        super(context);
    }

    public HUDView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HUDView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCompletionLevel(float completionLevel) {
        this.completionLevel = completionLevel;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(getContext().getResources().getColor(R.color.hud_color));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, paint);

        if (completionLevel == 1) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkmark);
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight() - bitmap.getHeight()) / 2, new Paint());
        } else {
            Paint paint1 = new Paint();
            paint1.setColor(Color.WHITE);
            paint1.setStyle(Paint.Style.FILL);
            RectF rectF = new RectF(getWidth() / 5, getWidth() / 5, getWidth() * 4 / 5, getWidth() * 4 / 5);
            canvas.drawArc(rectF, 270, -360 * completionLevel, true, paint1);
        }
    }
}
