package nu.pto.androidapp.base.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import nu.pto.androidapp.base.R;

/**
 * Created by narek on 1/16/14.
 * <p/>
 * Button with font name attribute
 */
public class PtoButton extends Button {

    public PtoButton(Context context) {
        super(context);
        init(null);
    }

    public PtoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PtoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        if (isInEditMode()) return;

        if (attributeSet != null) {

            TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.TextView);
            String fontName = array.getString(R.styleable.TextView_font_name);

            if (fontName != null) {
                setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Fonts/" + fontName));
            }
        }
    }
}
