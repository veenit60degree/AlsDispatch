package com.als.dispatchNew.customfont.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

public class PoppinsBoldTextView extends TextView {
    public PoppinsBoldTextView(Context context) {
        super(context);
        init(null);
    }

    public PoppinsBoldTextView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PoppinsBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PoppinsBoldTextView(Context context,  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }



    private void init(AttributeSet attrs) {
        if (attrs != null) {
            try {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_bold.ttf");
                setTypeface(myTypeface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
