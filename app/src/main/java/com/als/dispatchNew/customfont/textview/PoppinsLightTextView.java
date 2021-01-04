package com.als.dispatchNew.customfont.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

public class PoppinsLightTextView extends TextView {

    public PoppinsLightTextView(Context context) {
        super(context);
        init(null);
    }

    public PoppinsLightTextView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PoppinsLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PoppinsLightTextView(Context context,  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }



    private void init(AttributeSet attrs) {
        if (attrs != null) {
            try {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_light.ttf");
                setTypeface(myTypeface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
