package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import first.slidingmenu.R;

/**
 * Created by dell on 2016/7/14.
 */
public class SlidingMenuHorizontalScrollView extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mSlidingRightPadding;
    private int mMenuWidth;
    private boolean isOpen = true;

    private boolean once;
    /**
     * 未自定义属性是调用
     * @param context
     * @param attrs
     */
    public SlidingMenuHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        mScreenWidth = outMetrics.widthPixels;
//        //将dp转化为px
//        mSlidingRightPadding  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics());

    }

    /**
     * 使用自定义属性时调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SlidingMenuHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenuHorizontalScrollView,defStyleAttr,0);
        int sum = a.getIndexCount();
        for(int i = 0;i < sum;i++) {

            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenuHorizontalScrollView_rightPadding:
                    mSlidingRightPadding = a.getDimensionPixelOffset(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics()));
                    break;

            }

        }
        a.recycle();

    }

    public SlidingMenuHorizontalScrollView(Context context) {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mSlidingRightPadding;

            mContent.getLayoutParams().width = mScreenWidth;
            mWapper.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            this.scrollTo(mMenuWidth,0);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth,0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0,0);
                    isOpen = true;
                }
                return true;

        }

        return super.onTouchEvent(ev);

    }
    /**
     * 打开菜单
     * */
    public void open() {
        if(isOpen)return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     * */
    public void close() {
        if(!isOpen)return;
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    /**
     * 切换菜单
     * */
    public void change() {
        if(isOpen) {
            close();
        }else {
            open();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        float scale = l* 1.0f/mMenuWidth; //1~0
        float rightScale = (float) (0.7 + 0.3 * scale);

        Log.d("TAG", mMenuWidth + "");
        Log.d("TAG",mScreenWidth+"");
        mMenu.setTranslationX(l*0.8f);

        /**
         * 缩放与透明度效果
         * */
        mContent.setPivotX(0);
        mContent.setPivotY(mContent.getHeight()/2);
        mContent.setScaleX(rightScale);
        mContent.setScaleY(rightScale);

        mMenu.setAlpha((float) (1 - 0.3 * scale));
        mMenu.setScaleX((float) (1 - 0.3 * scale));
        mMenu.setScaleY((float) (1 - 0.3 * scale));
    }
}
