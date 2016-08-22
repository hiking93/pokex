package go.pokemon.pokemon.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import go.pokemon.pokemon.R;
import go.pokemon.pokemon.lib.Prefs;
import go.pokemon.pokemon.module.SensorView;

/**
 * Service to create overlay
 * Created by hiking on 2016/7/17.
 */
public class SensorOverlayService extends Service {

	private static final int MESSAGE_SENSOR_EVENT = 1;
	private static final int MESSAGE_LOCATION_UPDATE = 2;
	private static final int MESSAGE_THRESHOLD_UPDATE = 3;

	public static final int RESULT_SENSOR_SWITCH_TOGGLE = 100;

	private View mRootView;
	@BindView(R.id.textView_sensor_x) TextView mSensorXTextView;
	@BindView(R.id.textView_sensor_y) TextView mSensorYTextView;
	@BindView(R.id.textView_latitude) TextView mLatitudeTextView;
	@BindView(R.id.textView_longitude) TextView mLongitudeTextView;
	@BindView(R.id.sensorView) SensorView mSensorView;
	@BindView(R.id.switch_enable_sensor) Switch mEnableSensorSwitch;

	private WindowManager mWindowManager;
	private DecimalFormat mSensorFormat, mLocationFormat;

	private ResultReceiver mResultReceiver;
	private final Messenger mMessenger = new Messenger(new Handler() {

		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
				case MESSAGE_SENSOR_EVENT: {
					onSensorEvent(message);
				}
				break;
				case MESSAGE_LOCATION_UPDATE: {
					onLocationUpdate(message);
				}
				break;
				case MESSAGE_THRESHOLD_UPDATE: {
					onThresholdUpdate(message);
				}
				break;
				default: {
					super.handleMessage(message);
				}
			}
		}
	});

	public interface ResultCallback {

		void onSensorSwitchToggle(boolean enabled);
	}

	public static Intent getServiceIntent(ResultCallback callback) {
		Intent intent = new Intent();
		intent.setComponent(SensorOverlayService.getComponentName());
		intent.putExtras(SensorOverlayService.getReceiverExtras(callback));
		return intent;
	}

	private static ComponentName getComponentName() {
		return new ComponentName("go.pokemon.pokemon",
				"go.pokemon.pokemon.service.SensorOverlayService");
	}

	private static Bundle getReceiverExtras(final ResultCallback callback) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("receiver", new ResultReceiver(null) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);

				switch (resultCode) {
					case SensorOverlayService.RESULT_SENSOR_SWITCH_TOGGLE: {
						callback.onSensorSwitchToggle(resultData.getBoolean("enabled"));
					}
					break;
				}
			}
		});
		return bundle;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Object extra = intent.getParcelableExtra("receiver");
		if (extra instanceof ResultReceiver) {
			mResultReceiver = ((ResultReceiver) extra);
		}

		return mMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		initValues();
		inflateViews();
		setUpViews();
	}

	private void initValues() {
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mSensorFormat = new DecimalFormat("0.00");
		mSensorFormat.setPositivePrefix("+");
		mLocationFormat = new DecimalFormat("0.00000");
		mLocationFormat.setPositivePrefix("+");
	}

	private void inflateViews() {
		mRootView = View.inflate(this, R.layout.overlay_debug, null);
		ButterKnife.bind(this, mRootView);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public static Message createSensorEventMessage(SensorEvent sensorEvent, float calibrationX,
	                                               float calibrationY) {
		float sensorX = sensorEvent.values[0];
		float sensorY = sensorEvent.values[1];

		Bundle bundle = new Bundle();
		bundle.putDouble("sensorX", sensorX - calibrationX);
		bundle.putDouble("sensorY", sensorY - calibrationY);

		Message message = Message.obtain();
		message.what = MESSAGE_SENSOR_EVENT;
		message.setData(bundle);

		return message;
	}

	public void onSensorEvent(Message message) {
		Bundle bundle = message.getData();

		double sensorX = bundle.getDouble("sensorX");
		double sensorY = bundle.getDouble("sensorY");
		float sensorThreshold = Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD);

		mSensorXTextView.setText("Sensor X: " + mSensorFormat.format(sensorX));
		mSensorYTextView.setText("Sensor Y: " + mSensorFormat.format(sensorY));

		boolean sensorOverThreshold =
				sensorX * sensorX + sensorY * sensorY >= sensorThreshold * sensorThreshold;
		int sensorTextColor = ContextCompat.getColor(this,
				sensorOverThreshold ? R.color.colorAccent : R.color.white_text_secondary);
		mSensorXTextView.setTextColor(sensorTextColor);
		mSensorYTextView.setTextColor(sensorTextColor);

		mSensorView.setSensorValues((float) sensorX, (float) sensorY);
	}

	public static Message createLocationUpdateMessage(double latitude, double longitude) {
		Bundle bundle = new Bundle();
		bundle.putDouble("latitude", latitude);
		bundle.putDouble("longitude", longitude);

		Message message = Message.obtain();
		message.what = MESSAGE_LOCATION_UPDATE;
		message.setData(bundle);
		return message;
	}

	public void onLocationUpdate(Message message) {
		Bundle bundle = message.getData();

		double latitude = bundle.getDouble("latitude");
		double longitude = bundle.getDouble("longitude");

		mLatitudeTextView.setText("LAT:" + mLocationFormat.format(latitude));
		mLatitudeTextView.setVisibility(View.VISIBLE);
		mLongitudeTextView.setText("LNG:" + mLocationFormat.format(longitude));
		mLongitudeTextView.setVisibility(View.VISIBLE);
	}

	public static Message createThresholdUpdateMessage(float threshold) {
		Bundle bundle = new Bundle();
		bundle.putDouble("threshold", threshold);

		Message message = Message.obtain();
		message.what = MESSAGE_THRESHOLD_UPDATE;
		message.setData(bundle);
		return message;
	}

	public void onThresholdUpdate(Message message) {
		Bundle bundle = message.getData();

		float threshold = (float) bundle.getDouble("threshold");

		mSensorView.setThreshold(threshold);
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

		mSensorView.setThreshold(Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD));
		mEnableSensorSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
						if (mResultReceiver != null) {
							Bundle bundle = new Bundle();
							bundle.putBoolean("enabled", checked);
							mResultReceiver.send(RESULT_SENSOR_SWITCH_TOGGLE, bundle);

							float alpha = checked ? 1f : 0.38f;
							mSensorXTextView.setAlpha(alpha);
							mSensorYTextView.setAlpha(alpha);
							mSensorView.setAlpha(alpha);
							mLatitudeTextView.setAlpha(alpha);
							mLongitudeTextView.setAlpha(alpha);
						}
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

