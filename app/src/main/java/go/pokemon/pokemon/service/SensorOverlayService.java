package go.pokemon.pokemon.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import go.pokemon.pokemon.R;
import go.pokemon.pokemon.lib.Prefs;

/**
 * Service to create overlay
 * Created by hiking on 2016/7/17.
 */
public class SensorOverlayService extends Service {

	@BindView(R.id.textView_sensor_x) TextView mSensorXTextView;
	@BindView(R.id.textView_sensor_y) TextView mSensorYTextView;

	private WindowManager mWindowManager;
	private View mRootView;

	@Override
	public IBinder onBind(Intent intent) {
		return null; // Not used
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mRootView = View.inflate(this, R.layout.overlay_debug, null);
		ButterKnife.setDebug(true);
		ButterKnife.bind(this, mRootView);
		setUpViews();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getExtras() != null) {
			onDataReceived(intent.getExtras());
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void onDataReceived(Bundle bundle) {
		float sensorThreshold = Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD);

		DecimalFormat sensorFormat = new DecimalFormat("0.00");
		sensorFormat.setPositivePrefix("+");

		double sensorX = bundle.getDouble("sensorX");
		mSensorXTextView.setText("Sensor X: " + sensorFormat.format(sensorX));
		boolean sensorXOverThreshold = sensorX >= sensorThreshold || sensorX <= -sensorThreshold;
		mSensorXTextView.setTextColor(ContextCompat.getColor(this,
				sensorXOverThreshold ? R.color.yellow_500 : R.color.white_text_secondary));

		double sensorY = bundle.getDouble("sensorY");
		mSensorYTextView.setText("Sensor Y: " + sensorFormat.format(sensorY));
		boolean sensorYOverThreshold = sensorY >= sensorThreshold || sensorY <= -sensorThreshold;
		mSensorYTextView.setTextColor(ContextCompat.getColor(this,
				sensorYOverThreshold ? R.color.yellow_500 : R.color.white_text_secondary));
	}

	public static Bundle createSensorEventBundle(SensorEvent sensorEvent) {
		float sensorX = sensorEvent.values[0];
		float sensorY = sensorEvent.values[1];

		Bundle bundle = new Bundle();
		bundle.putDouble("sensorX", sensorX);
		bundle.putDouble("sensorY", sensorY - 5);
		return bundle;
	}

	private void setUpViews() {
		// Set up window
		WindowManager.LayoutParams params =
				new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.START;
		params.x = 0;
		params.y = 0;
		mWindowManager.addView(mRootView, params);

		// Drag window listener
		mRootView.setOnTouchListener(new View.OnTouchListener() {

			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				WindowManager.LayoutParams params =
						(WindowManager.LayoutParams) v.getLayoutParams();
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						initialX = params.x;
						initialY = params.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						return true;
					case MotionEvent.ACTION_UP:
						return true;
					case MotionEvent.ACTION_MOVE:
						params.x = initialX + (int) (event.getRawX() - initialTouchX);
						params.y = initialY + (int) (event.getRawY() - initialTouchY);
						mWindowManager.updateViewLayout(mRootView, params);
						return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mRootView != null) {
			mWindowManager.removeView(mRootView);
		}
	}
}

