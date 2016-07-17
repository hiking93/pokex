package go.pokemon.pokemon;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import go.pokemon.pokemon.lib.Prefs;
import go.pokemon.pokemon.lib.Utils;
import go.pokemon.pokemon.service.SensorOverlayService;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

	private static final int REQUEST_PERMISSION_DRAW_OVER_OTHER_APPS = 404;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		setUpViews();
	}

	private void setUpViews() {
		EditText editTextSensorThreshold = (EditText) findViewById(R.id.editText_sensor_threshold);
		editTextSensorThreshold
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD)));
		editTextSensorThreshold.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (!Utils.isFloat(charSequence)) {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD);
				} else {
					Prefs.setFloat(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD,
							Float.parseFloat(charSequence.toString()));
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextSensorThreshold.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_SENSOR_THRESHOLD)));
				}
			}
		});

		EditText editTextMinimumTimeInterval =
				(EditText) findViewById(R.id.editText_minimum_time_interval);
		editTextMinimumTimeInterval
				.setText(Utils.toDecimalString(Prefs.getInt(this, Prefs.KEY_UPDATE_INTERVAL)));
		editTextMinimumTimeInterval.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (Utils.isInt(charSequence)) {
					Prefs.setInt(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL,
							Integer.parseInt(charSequence.toString()));
				} else if (Utils.isFloat(charSequence)) {
					Prefs.setInt(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL,
							(int) Float.parseFloat(charSequence.toString()));
				} else {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextMinimumTimeInterval.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getInt(view.getContext(), Prefs.KEY_UPDATE_INTERVAL)));
				}
			}
		});

		EditText editTextMoveDistanceLatitude =
				(EditText) findViewById(R.id.editText_move_distance_latitude);
		editTextMoveDistanceLatitude.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_MOVE_MULTIPLIER_LAT)));
		editTextMoveDistanceLatitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (!Utils.isFloat(charSequence)) {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LAT);
				} else {
					Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LAT,
							Float.parseFloat(charSequence.toString()));
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextMoveDistanceLatitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_MOVE_MULTIPLIER_LAT)));
				}
			}
		});

		EditText editTextMoveDistanceLongitude =
				(EditText) findViewById(R.id.editText_move_distance_longitude);
		editTextMoveDistanceLongitude.setText(
				Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_MOVE_MULTIPLIER_LONG)));
		editTextMoveDistanceLongitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (!Utils.isFloat(charSequence)) {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LONG);
				} else {
					Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LONG,
							Float.parseFloat(charSequence.toString()));
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextMoveDistanceLongitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_MOVE_MULTIPLIER_LONG)));
				}
			}
		});

		EditText editTextRespawnLocationLatitude =
				(EditText) findViewById(R.id.editText_respawn_location_latitude);
		editTextRespawnLocationLatitude
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_RESPAWN_LAT)));
		editTextRespawnLocationLatitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (!Utils.isFloat(charSequence)) {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_RESPAWN_LAT);
				} else {
					Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LAT,
							Float.parseFloat(charSequence.toString()));
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextRespawnLocationLatitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_RESPAWN_LAT)));
				}
			}
		});

		EditText editTextRespawnLocationLongitude =
				(EditText) findViewById(R.id.editText_respawn_location_longitude);
		editTextRespawnLocationLongitude
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_RESPAWN_LONG)));
		editTextRespawnLocationLongitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (!Utils.isFloat(charSequence)) {
					Prefs.setToDefault(MainActivity.this, Prefs.KEY_RESPAWN_LONG);
				} else {
					Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LONG,
							Float.parseFloat(charSequence.toString()));
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		editTextRespawnLocationLongitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					((EditText) view).setText(Utils.toDecimalString(
							Prefs.getFloat(view.getContext(), Prefs.KEY_RESPAWN_LONG)));
				}
			}
		});
	}

	private void checkToEnableOverlay() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
			Toast.makeText(this, "Enable drawing over other apps", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, REQUEST_PERMISSION_DRAW_OVER_OTHER_APPS);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_PERMISSION_DRAW_OVER_OTHER_APPS) {
			checkToEnableOverlay();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopSensorListening();
	}

	private void startSensorListening() {
		startService(new Intent(this, SensorOverlayService.class));
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stopSensorListening() {
		stopService(new Intent(this, SensorOverlayService.class));
		mSensorManager.unregisterListener(this, mSensor);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Intent intent = new Intent(this, SensorOverlayService.class);
		intent.putExtras(SensorOverlayService.createSensorEventBundle(sensorEvent));
		startService(intent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}
}
