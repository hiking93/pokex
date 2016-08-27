package com.sparkslab.pokex;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sparkslab.pokex.lib.FirebaseAnalyticsHelper;
import com.sparkslab.pokex.lib.Prefs;
import com.sparkslab.pokex.lib.Utils;
import com.sparkslab.pokex.service.SensorOverlayService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Configuration screen
 *
 * @author Created by pauline on 7/15/16.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

	@BindView(R.id.editText_sensor_threshold) EditText mSensorThresholdEditText;
	@BindView(R.id.editText_sensor_calibration_x) EditText mSensorCalibrationXEditText;
	@BindView(R.id.editText_sensor_calibration_y) EditText mSensorCalibrationYEditText;
	@BindView(R.id.editText_update_interval) EditText mUpdateIntervalEditText;
	@BindView(R.id.editText_move_latitude_multiplier) EditText mMoveLatitudeMultiplierEditText;
	@BindView(R.id.editText_move_longitude_multiplier) EditText mMoveLongitudeMultiplierEditText;
	@BindView(R.id.editText_respawn_latitude) EditText mRespawnLatitudeEditText;
	@BindView(R.id.editText_respawn_longitude) EditText mRespawnLongitudeEditText;

	private Messenger mService;
	private ServiceConnection mServiceConnection;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private int mSensorUpdateInterval;
	private long mLastSensorUpdate;
	private boolean mIsSensorEnabled = true;
	private float mSensorCalibrationX, mSensorCalibrationY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initValues();
		ButterKnife.bind(this);
		setUpViews();

		FirebaseAnalyticsHelper.onAppOpen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reset_all: {
				new AlertDialog.Builder(this).setTitle(R.string.reset_all)
						.setMessage(R.string.reset_all_confirm_message)
						.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Prefs.setAllToDefault(MainActivity.this);
								setUpViews();
							}
						}).setNegativeButton(R.string.cancel, null).show();
				return true;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
	}

	private void initValues() {
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorUpdateInterval = Prefs.getInt(this, Prefs.KEY_UPDATE_INTERVAL);
		mSensorCalibrationX = Prefs.getFloat(this, Prefs.KEY_SENSOR_CALIBRATION_X);
		mSensorCalibrationY = Prefs.getFloat(this, Prefs.KEY_SENSOR_CALIBRATION_Y);
	}

	private void setUpViews() {
		mSensorThresholdEditText
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD)));
		mSensorThresholdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD);
					}

					if (mService != null) {
						try {
							mService.send(SensorOverlayService.createThresholdUpdateMessage(
									Prefs.getFloat(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD)));
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_SENSOR_THRESHOLD)));
				}
			}
		});

		mSensorCalibrationXEditText.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_SENSOR_CALIBRATION_X)));
		mSensorCalibrationXEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_X, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_X);
					}

					mSensorCalibrationX =
							Prefs.getFloat(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_X);

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_SENSOR_CALIBRATION_X)));
				}
			}
		});

		mSensorCalibrationYEditText.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_SENSOR_CALIBRATION_Y)));
		mSensorCalibrationYEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_Y, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_Y);
					}

					mSensorCalibrationY =
							Prefs.getFloat(MainActivity.this, Prefs.KEY_SENSOR_CALIBRATION_Y);

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_SENSOR_CALIBRATION_Y)));
				}
			}
		});

		mUpdateIntervalEditText
				.setText(Utils.toDecimalString(Prefs.getInt(this, Prefs.KEY_UPDATE_INTERVAL)));
		mUpdateIntervalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						int value = Integer.parseInt(((EditText) view).getText().toString());
						Prefs.setInt(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL);
					}

					mSensorUpdateInterval =
							Prefs.getInt(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL);

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getInt(view.getContext(), Prefs.KEY_UPDATE_INTERVAL)));
				}
			}
		});

		mMoveLatitudeMultiplierEditText.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_MOVE_MULTIPLIER_LAT)));
		mMoveLatitudeMultiplierEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LAT, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LAT);
					}

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_MOVE_MULTIPLIER_LAT)));
				}
			}
		});

		mMoveLongitudeMultiplierEditText.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_MOVE_MULTIPLIER_LONG)));
		mMoveLongitudeMultiplierEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LONG, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LONG);
					}

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_MOVE_MULTIPLIER_LONG)));
				}
			}
		});

		mRespawnLatitudeEditText
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_RESPAWN_LAT)));
		mRespawnLatitudeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LAT, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_RESPAWN_LAT);
					}

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_RESPAWN_LAT)));
				}
			}
		});

		mRespawnLongitudeEditText
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_RESPAWN_LONG)));
		mRespawnLongitudeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					try {
						float value = Float.parseFloat(((EditText) view).getText().toString());
						Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LONG, value);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, R.string.invalid_value_reset,
								Toast.LENGTH_SHORT).show();
						Prefs.setToDefault(MainActivity.this, Prefs.KEY_RESPAWN_LONG);
					}

					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_RESPAWN_LONG)));
				}
			}
		});
	}

	private void checkToEnableOverlay() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
			Toast.makeText(this, R.string.grant_permission_overlay, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			return;
		}
		startSensorListening();
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkToEnableOverlay();
	}

	@Override
	protected void onPause() {
		stopSensorListening();

		super.onPause();
	}

	private void startSensorListening() {
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}

		SensorOverlayService.ResultCallback callback = new SensorOverlayService.ResultCallback() {

			@Override
			public void onSensorSwitchToggle(boolean enabled) {
				mIsSensorEnabled = enabled;
			}
		};
		Intent intent = SensorOverlayService.getServiceIntent(callback);
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName className, IBinder binder) {
				mService = new Messenger(binder);
			}

			@Override
			public void onServiceDisconnected(ComponentName className) {
				mService = null;
			}
		};
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stopSensorListening() {
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
			mServiceConnection = null;
		}
		stopService(new Intent(this, SensorOverlayService.class));
		mSensorManager.unregisterListener(this, mSensor);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (!mIsSensorEnabled) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if ((currentTime - mLastSensorUpdate) > mSensorUpdateInterval) {
			mLastSensorUpdate = currentTime;
			if (mService != null) {
				try {
					mService.send(SensorOverlayService
							.createSensorEventMessage(sensorEvent, mSensorCalibrationX,
									mSensorCalibrationY));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}
}
