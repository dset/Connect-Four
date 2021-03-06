package se.dset.android.connectfour.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/* Represents a single cell in the board. */
public class BoardCellView extends View {
    public BoardCellView(Context context) {
        super(context);
    }

    public BoardCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /* Ensures that this view is always a square. */
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
