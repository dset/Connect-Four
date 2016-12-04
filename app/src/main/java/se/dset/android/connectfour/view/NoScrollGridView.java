package se.dset.android.connectfour.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class NoScrollGridView extends ViewGroup {
    private static final int DEFAULT_NUM_COLUMNS = 3;

    private int numColumns;

    public NoScrollGridView(Context context) {
        super(context);
        numColumns = DEFAULT_NUM_COLUMNS;
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        numColumns = DEFAULT_NUM_COLUMNS;
    }

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        numColumns = DEFAULT_NUM_COLUMNS;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int numRows = getNumRows();
        int childSize = Math.min(measuredWidth / numColumns, measuredHeight / numRows);

        int spec = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.measure(spec, spec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;

        int numRows = getNumRows();
        int childSize = Math.min(width / numColumns, height / numRows);

        int paddingLeft = (width - (numColumns * childSize)) / 2;
        int paddingTop = height - (numRows * childSize);

        int index = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            int row = index / numColumns;
            int col = index - row * numColumns;
            int l = paddingLeft + col * childSize;
            int t = paddingTop + row * childSize;
            child.layout(l, t, l + childSize, t + childSize);
            index++;
        }
    }

    private int getNumRows() {
        return ((getCorrectedChildCount() - 1) / numColumns) + 1;
    }

    private int getCorrectedChildCount() {
        int correctedChildCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() != View.GONE) {
                correctedChildCount++;
            }
        }

        return correctedChildCount;
    }
}

