package liuxiaocong.com.waterloading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuXiaocong on 12/22/2016.
 */

public class WaterLoadImageView extends ImageView {
    final String TAG = "WaterLoadImageView";
    private int mImageWidth;
    private int mImageHeight;
    private boolean mAutoPlay = true;
    public static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private ColorStateList mWaterColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
    Drawable mDrawable;
    private Paint mBitmapPaint;
    private BitmapShader mBitmapShader;
    private List<Integer> mCurrentWaterWidthList = new ArrayList<>();
    private int mDuration = 1500;
    ValueAnimator anim1;
    ValueAnimator anim2;

    public WaterLoadImageView(Context context) {
        super(context);
    }

    public WaterLoadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterLoadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterLoadImageView, defStyleAttr, 0);
        mImageWidth = a.getDimensionPixelSize(R.styleable.WaterLoadImageView_image_width, 50);
        mImageHeight = a.getDimensionPixelSize(R.styleable.WaterLoadImageView_image_height, 50);
        mWaterColor = a.getColorStateList(R.styleable.WaterLoadImageView_water_color);
        mAutoPlay = a.getBoolean(R.styleable.WaterLoadImageView_auto_play, true);
        mDuration = a.getInt(R.styleable.WaterLoadImageView_water_duration, 1500);
        if (mWaterColor == null) {
            mWaterColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
        }
        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoPlay) {
            startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (anim1 != null) {
            anim1.cancel();
            anim1 = null;
        }
        if (anim2 != null) {
            anim2.cancel();
            anim2 = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null) {
            mDrawable = getDrawable();
        }
        if (mDrawable == null) {
            return;
        }
        if (mBitmapShader == null) {
            initBitmapShader();
            initBitmapPaint();
        }
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        if (mCurrentWaterWidthList != null && mCurrentWaterWidthList.size() > 0) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int dis = mCurrentWaterWidthList.get(0) - mImageWidth / 2;
            int alpha = 255 - (int) (255 * ((float) dis / (float) (getWidth() / 2 - mImageWidth / 2)));
            paint.setColor(Color.parseColor("#FFFFFF"));
            if (alpha < 0) {
                alpha = 0;
            }
            paint.setAlpha(alpha);
            //Log.d(TAG, "One alpha:" + alpha);
            canvas.drawCircle(centerX, centerY, mCurrentWaterWidthList.get(0), paint);
        }
        if (mCurrentWaterWidthList != null && mCurrentWaterWidthList.size() > 1) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int dis = mCurrentWaterWidthList.get(1) - mImageWidth / 2;
            int alpha = 255 - (int) (255 * ((float) dis / (float) (getWidth() / 2 - mImageWidth / 2)));
            paint.setColor(Color.parseColor("#FFFFFF"));
            if (alpha < 0) {
                alpha = 0;
            }
            paint.setAlpha(alpha);
            canvas.drawCircle(centerX, centerY, mCurrentWaterWidthList.get(1), paint);
        }

        canvas.drawCircle(centerX, centerY, mImageWidth / 2, mBitmapPaint);
    }

    private void initBitmapShader() {
        Bitmap bm = ((BitmapDrawable) mDrawable).getBitmap();
//
//        Canvas canvas = new Canvas(bm);
//        mDrawable.setBounds(0, 0, mImageWidth, mImageHeight);
//        mDrawable.draw(canvas);
        Bitmap target = Bitmap.createScaledBitmap(bm, mImageWidth, mImageHeight, false);
        mBitmapShader = new BitmapShader(target, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale;
        float dx = 0;
        float dy = 0;
        Matrix mShaderMatrix = new Matrix();
        mShaderMatrix.set(null);
        mShaderMatrix.postTranslate((getWidth() - mImageWidth) / 2, (getHeight() - mImageHeight) / 2);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private void initBitmapPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint.setShader(mBitmapShader);
    }

    public void startAnimation() {
        post(new Runnable() {
            @Override
            public void run() {
                startFirstAnimation();
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                startSecondAnimation();
            }
        }, 500);
    }

    private void startFirstAnimation() {
        mCurrentWaterWidthList.add(0);
        anim1 = ValueAnimator.ofInt(mImageWidth / 2, getWidth() / 2);
        anim1.setDuration(mDuration);
        anim1.setRepeatCount(ValueAnimator.INFINITE);

        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                mCurrentWaterWidthList.set(0, width);
                postInvalidate();
            }
        });
        anim1.start();
    }

    private void startSecondAnimation() {
        mCurrentWaterWidthList.add(0);
        anim2 = ValueAnimator.ofInt(mImageWidth / 2, getWidth() / 2);
        anim2.setDuration(mDuration);
        anim2.setRepeatCount(ValueAnimator.INFINITE);

        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                mCurrentWaterWidthList.set(1, width);
                postInvalidate();
            }
        });
        anim2.start();
    }
}
