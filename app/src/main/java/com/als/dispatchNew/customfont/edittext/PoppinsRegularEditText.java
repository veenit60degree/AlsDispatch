package com.als.dispatchNew.customfont.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class PoppinsRegularEditText extends EditText {

    public PoppinsRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PoppinsRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PoppinsRegularEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_regular.ttf");
            setTypeface(tf);
        }
    }
}
