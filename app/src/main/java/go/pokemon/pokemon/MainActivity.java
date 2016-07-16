package go.pokemon.pokemon;

import android.content.Context;
import android.content.SharedPreferences;
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

        final SharedPreferences sharedPreferences = getSharedPreferences("pokemon", Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText editTextSensorThreshold = (EditText) findViewById(R.id.editText_sensor_threshold);
        editTextSensorThreshold.setText(sharedPreferences.getString("sensor_threshold", "1.5"));
        editTextSensorThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("sensor_threshold", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextMinimumTimeInterval = (EditText) findViewById(R.id.editText_minimum_time_interval);
        editTextMinimumTimeInterval.setText(sharedPreferences.getString("minimum_time_interval", "250"));
        editTextMinimumTimeInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("minimum_time_interval", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextMoveDistanceLatitude = (EditText) findViewById(R.id.editText_move_distance_latitude);
        editTextMoveDistanceLatitude.setText(sharedPreferences.getString("move_distance_latitude", "0.00005"));
        editTextMoveDistanceLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("move_distance_latitude", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextMoveDistanceLongitude = (EditText) findViewById(R.id.editText_move_distance_longitude);
        editTextMoveDistanceLongitude.setText(sharedPreferences.getString("move_distance_longitude", "0.00005"));
        editTextMoveDistanceLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("move_distance_longitude", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextRespawnLocationLatitude = (EditText) findViewById(R.id.editText_respawn_location_latitude);
        editTextRespawnLocationLatitude.setText(sharedPreferences.getString("respawn_location_latitude", "40.7589"));
        editTextRespawnLocationLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("respawn_location_latitude", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextRespawnLocationLongitude = (EditText) findViewById(R.id.editText_respawn_location_longitude);
        editTextRespawnLocationLongitude.setText(sharedPreferences.getString("respawn_location_longitude", "-73.9851"));
        editTextRespawnLocationLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("respawn_location_longitude", charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
