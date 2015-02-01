package com.example.travelguru.utils;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtils {

    public static void setColor(TextView view, String fullText,
            String subText, int color) {
        view.setText(fullText, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fullText.indexOf(subText);
        str.setSpan(new ForegroundColorSpan(color), i, i + subText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
