package go.pokemon.pokemon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		EditText editTextSensorThreshold = (EditText) findViewById(R.id.editText_sensor_threshold);
		editTextSensorThreshold
				.setText(Utils.toDecimalString(Prefs.getFloat(this, Prefs.KEY_SENSOR_THRESHOLD)));
		editTextSensorThreshold.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				Prefs.setFloat(MainActivity.this, Prefs.KEY_SENSOR_THRESHOLD,
						Float.parseFloat(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
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
				Prefs.setInt(MainActivity.this, Prefs.KEY_UPDATE_INTERVAL,
						Integer.parseInt(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
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
				Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LAT,
						Float.parseFloat(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
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
				Prefs.setFloat(MainActivity.this, Prefs.KEY_MOVE_MULTIPLIER_LONG,
						Float.parseFloat(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
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
				Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LAT,
						Float.parseFloat(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
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
				Prefs.setFloat(MainActivity.this, Prefs.KEY_RESPAWN_LONG,
						Float.parseFloat(charSequence.toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}
}
