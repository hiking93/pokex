package go.pokemon.pokemon;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by pauline on 7/15/16.
 */
public class Cool implements IXposedHookLoadPackage, SensorEventListener {

	private Context mContext;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private XSharedPreferences mSharedPreferences;
	private Object mThisObject;
	private Location mLocation;

	private double mPlayerLatitude, mPlayerLongitude;
	private double mSensorThreshold;
	private double mMoveDistanceLatitude, mMoveDistanceLongitude;
	private long mLastUpdate;
	private int mMinimumTimeInterval;
	private int[] mWhateverArray;

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		Log.d("PokemonDebug", "handleLoadPackage: " + lpparam.packageName);

		if (!lpparam.packageName.equals("com.nianticlabs.pokemongo")) {
			return;
		}

		findAndHookConstructor("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, Context.class, long.class, new XC_MethodHook() {

					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						super.afterHookedMethod(param);
						Log.d("PokemonDebug", "Constructor hooked");

						mContext = (Context) param.args[0];

						mSharedPreferences =
								new XSharedPreferences("go.pokemon.pokemon", "pokemon");

						mSensorThreshold = Double.parseDouble(
								mSharedPreferences.getString("sensor_threshold", "3.0"));
						mMinimumTimeInterval = Integer.parseInt(
								mSharedPreferences.getString("minimum_time_interval", "250"));
						mMoveDistanceLatitude = Double.parseDouble(
								mSharedPreferences.getString("move_distance_latitude", "0.00005"));
						mMoveDistanceLongitude = Double.parseDouble(
								mSharedPreferences.getString("move_distance_longitude", "0.00005"));
						mPlayerLatitude = Double.parseDouble(mSharedPreferences
								.getString("respawn_location_latitude", "40.7589"));
						mPlayerLongitude = Double.parseDouble(mSharedPreferences
								.getString("respawn_location_longitude", "-73.9851"));

						mSensorManager =
								(SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
						mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "onResume", new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						super.beforeHookedMethod(param);
						Log.d("PokemonDebug", "onResume");

						mSensorManager.registerListener(Cool.this, mSensor,
								SensorManager.SENSOR_DELAY_NORMAL);
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "onPause", new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						super.beforeHookedMethod(param);
						Log.d("PokemonDebug", "onPause");

						mSensorManager.unregisterListener(Cool.this, mSensor);
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "locationUpdate", Location.class, int[].class,
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
						Log.d("PokemonDebug", "locationUpdate");

						Location location = (Location) param.args[0];
						if (location != null) {
							mLocation = location;
							mThisObject = param.thisObject;
							mWhateverArray = (int[]) param.args[1];
						}
						return null;
					}
				});
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float sensorX = sensorEvent.values[0];
			float sensorY = sensorEvent.values[1];

			long currentTime = System.currentTimeMillis();
			if ((currentTime - mLastUpdate) > mMinimumTimeInterval) {
				mLastUpdate = currentTime;

				boolean isPositionChanged = false;
				if (sensorX > mSensorThreshold || sensorX < -mSensorThreshold) {
					mPlayerLongitude += mMoveDistanceLongitude *
							(sensorX > 0 ? sensorX - mSensorThreshold : sensorX + mSensorThreshold);
					isPositionChanged = true;
				}

				if (sensorY > mSensorThreshold || sensorY < -mSensorThreshold) {
					mPlayerLatitude += mMoveDistanceLatitude *
							(sensorY > 0 ? sensorY - mSensorThreshold : sensorY + mSensorThreshold);
					isPositionChanged = true;
				}

				if (isPositionChanged) {
					gotoPlace(mPlayerLatitude, mPlayerLongitude);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}

	private void gotoPlace(double latitude, double longitude) {
		if (mLocation == null || mThisObject == null || mWhateverArray == null) {
			return;
		}

		mLocation.setLatitude(latitude);
		mLocation.setLongitude(longitude);
		XposedHelpers.callMethod(mThisObject, "nativeLocationUpdate", mLocation, mWhateverArray,
				mContext);
	}
}
