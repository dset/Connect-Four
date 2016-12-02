package se.dset.android.connectfour;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class BoardCell extends View {
    public BoardCell(Context context) {
        super(context);
    }

    public BoardCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
