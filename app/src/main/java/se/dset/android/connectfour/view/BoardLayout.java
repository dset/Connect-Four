package se.dset.android.connectfour.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/* A ViewGroup that displays its children in a grid where each cell is a square.

Note to Lunicore: This is a class that I implemented for an earlier project that I copied
into this project with minor modifications. */
public class BoardLayout extends ViewGroup {
    private static final int DEFAULT_NUM_COLUMNS = 3;

    private int numColumns;

    public BoardLayout(Context context) {
        super(context);
        numColumns = DEFAULT_NUM_COLUMNS;
    }

    public BoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        numColumns = DEFAULT_NUM_COLUMNS;
    }

    public BoardLayout(Context context, AttributeSet attrs, int defStyle) {
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        /* Draws the checkered background of the board. */
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int numRows = getNumRows();
        int childSize = Math.min(width / numColumns, height / numRows);

        int paddingLeft = (width - (numColumns * childSize)) / 2;
        int paddingTop = height - (numRows * childSize);

        Paint p = new Paint();
        p.setColor(Color.parseColor("#ededed"));
        canvas.drawRect(paddingLeft, paddingTop, paddingLeft + childSize, paddingTop + childSize, p);

        for (int row = 0; row < numRows; row++) {
            for (int column = (row % 2); column < numColumns; column += 2) {
                int left = paddingLeft + column * childSize;
                int top = paddingTop + row * childSize;
                canvas.drawRect(left, top, left + childSize, top + childSize, p);
            }
        }

        super.dispatchDraw(canvas);
    }
}

