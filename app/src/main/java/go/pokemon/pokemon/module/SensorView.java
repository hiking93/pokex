package go.pokemon.pokemon.module;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import go.pokemon.pokemon.R;
import go.pokemon.pokemon.lib.Constant;
import go.pokemon.pokemon.lib.Utils;

/**
 * View to show sensor status
 * Created by hiking on 2016/7/18.
 */
public class SensorView extends View {

	private float mThreshold;
	private float mMaxValue = 10f;
	private float mXValue, mYValue;
	private float mDrawXValue, mDrawYValue;
	private long mLastSensorUpdateTime;
	private int mSmoothingDuration = 100;

	private Paint mPaint;
	private int mActiveColor, mActiveColorSecondary;

	public SensorView(Context context) {
		super(context);
		initValues();
	}

	public SensorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initValues();
	}

	public SensorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initValues();
	}

	private void initValues() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mActiveColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
		mActiveColorSecondary = mActiveColor & 0x33ffffff;
	}

	public float getThreshold() {
		return mThreshold;
	}

	public void setThreshold(float threshold) {
		this.mThreshold = threshold;
		invalidate();
	}

	public float getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(float maxValue) {
		this.mMaxValue = maxValue;
		invalidate();
	}

	public void setSensorValues(float x, float y) {
		mXValue = x;
		mYValue = y;

		// Calculate smoothing animation duration
		long now = System.currentTimeMillis();
		if (mLastSensorUpdateTime > 0) {
			int timePassed = (int) (now - mLastSensorUpdateTime);
			mSmoothingDuration = (int) (mSmoothingDuration * .9 + timePassed * .1);
		}
		mLastSensorUpdateTime = now;

		if (Constant.ENABLE_SMOOTHING) {
			// Smoothing for drawing
			ValueAnimator xAnim =
					ValueAnimator.ofFloat(mDrawXValue, x).setDuration(mSmoothingDuration);
			xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					mDrawXValue = (Float) valueAnimator.getAnimatedValue();
					invalidate();
				}
			});
			ValueAnimator yAnim =
					ValueAnimator.ofFloat(mDrawYValue, y).setDuration(mSmoothingDuration);
			yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					mDrawYValue = (Float) valueAnimator.getAnimatedValue();
					invalidate();
				}
			});
			xAnim.start();
			yAnim.start();
		} else {
			mDrawXValue = x;
			mDrawYValue = y;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = canvas.getWidth();
		int height = canvas.getWidth();

		// Calibrated sensor values, x positive on the right
		float rawX = -mXValue;
		float rawY = mYValue;
		float smoothX = -mDrawXValue;
		float smoothY = mDrawYValue;
		boolean overThreshold = rawX * rawX + rawY * rawY >= mThreshold * mThreshold;

		// Outer (max)
		float outerRadius = width * .45f;
		mPaint.setColor(overThreshold ? mActiveColorSecondary : 0x33ffffff);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(width / 2, height / 2, outerRadius, mPaint);
		mPaint.setColor(0xb3ffffff);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(width / 2, height / 2, outerRadius, mPaint);

		// Threshold
		float thresholdRadius = outerRadius * mThreshold / mMaxValue;
		mPaint.setColor(0x33000000);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(width / 2, height / 2, thresholdRadius, mPaint);
		mPaint.setColor(mActiveColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(width / 2, height / 2, thresholdRadius, mPaint);

		// Sensor indicator
		float sensorIndicatorX = width / 2 + outerRadius * smoothX / mMaxValue;
		float sensorIndicatorY = height / 2 + outerRadius * smoothY / mMaxValue;
		float sensorIndicatorRadius = width * .05f;
		mPaint.setColor(0x66ffffff);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(sensorIndicatorX, sensorIndicatorY, sensorIndicatorRadius, mPaint);
		mPaint.setColor(0xffffffff);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(sensorIndicatorX, sensorIndicatorY, sensorIndicatorRadius, mPaint);
	}
}
