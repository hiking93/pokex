package go.pokemon.pokemon.module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import go.pokemon.pokemon.lib.Utils;

/**
 * View to show sensor status
 * Created by hiking on 2016/7/18.
 */
public class SensorView extends View {

	private float mThreshold;
	private float mMaxValue = 10f;
	private float mXValue, mYValue;

	private Paint mPaint;

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
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = canvas.getWidth();
		int height = canvas.getWidth();

		// Outer (max)
		float outerRadius = width * .45f;
		mPaint.setColor(0xb3ffffff);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(width / 2, height / 2, outerRadius, mPaint);

		// Threshold
		float thresholdRadius = outerRadius * mThreshold / mMaxValue;
		mPaint.setColor(0xffffeb3b);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(width / 2, height / 2, thresholdRadius, mPaint);

		// Sensor indicator
		float sensorIndicatorX = width / 2 + outerRadius * mXValue / mMaxValue;
		float sensorIndicatorY = height / 2 + outerRadius * mYValue / mMaxValue;
		float sensorIndicatorRadius = width * .05f;
		mPaint.setColor(0xffffffff);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
		canvas.drawCircle(sensorIndicatorX, sensorIndicatorY, sensorIndicatorRadius, mPaint);
	}
}
