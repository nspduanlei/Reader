package com.example.testscroll.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Author: duanlei
 * Date: 2015-12-09
 */
public class ReadView extends TextView {

    LayoutListener mLayoutListener;

    public ReadView(Context context) {
        super(context);
        init();
    }

    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //resize();
        if (mLayoutListener != null)
            mLayoutListener.onLayout(getCharNum());
    }


//    /**
//     * 去除当前页无法显示的字
//     * @return 去掉的字数
//     */
//    public int resize() {
//        CharSequence oldContent = getText();
//        CharSequence newContent = oldContent.subSequence(0, getCharNum());
//        setText(newContent);
//        return oldContent.length() - newContent.length();
//    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }

    public interface LayoutListener {
        void onLayout(int charNum);
    }

    public void setLayoutListener(LayoutListener listener) {
        mLayoutListener = listener;
    }
}