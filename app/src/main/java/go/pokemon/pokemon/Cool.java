package go.pokemon.pokemon;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Hook the methods!
 *
 * @author Created by pauline on 7/15/16.
 */
public class Cool implements IXposedHookLoadPackage, SensorEventListener {

	private Context mContext;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Object mThisObject;
	private Location mLocation;

	private float mPlayerLatitude, mPlayerLongitude;
	private float mSensorThreshold;
	private float mMoveDistanceLatitude, mMoveDistanceLongitude;
	private long mLastUpdate;
	private int mMinimumTimeInterval;
	private int[] mWhateverArray;

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.nianticlabs.pokemongo")) {
			return;
		}

		findAndHookConstructor("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, Context.class, long.class, new XC_MethodHook() {

					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						super.afterHookedMethod(param);

						Log.d(Constant.TAG, "Constructor hooked");
						mContext = (Context) param.args[0];
						Toast.makeText(mContext, "Sensor module loaded.", Toast.LENGTH_SHORT)
								.show();

						mSensorThreshold = Prefs.getXFloat(mContext, Prefs.KEY_SENSOR_THRESHOLD);
						mMinimumTimeInterval = Prefs.getXInt(mContext, Prefs.KEY_UPDATE_INTERVAL);
						mMoveDistanceLatitude =
								Prefs.getXFloat(mContext, Prefs.KEY_MOVE_MULTIPLIER_LAT);
						mMoveDistanceLongitude =
								Prefs.getXFloat(mContext, Prefs.KEY_MOVE_MULTIPLIER_LONG);
						mPlayerLatitude = Prefs.getXFloat(mContext, Prefs.KEY_RESPAWN_LAT);
						mPlayerLongitude = Prefs.getXFloat(mContext, Prefs.KEY_RESPAWN_LONG);
						Log.d(Constant.TAG,
								"mSensorThreshold = " + Utils.toDecimalString(mSensorThreshold) +
										"\nmMinimumTimeInterval = " +
										Utils.toDecimalString(mMinimumTimeInterval) +
										"\nmMoveDistanceLatitude = " +
										Utils.toDecimalString(mMoveDistanceLatitude) +
										"\nmMoveDistanceLongitude = " +
										Utils.toDecimalString(mMoveDistanceLongitude) +
										"\nmPlayerLatitude = " +
										Utils.toDecimalString(mPlayerLatitude) +
										"\nmPlayerLongitude = " +
										Utils.toDecimalString(mPlayerLongitude));

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

						mSensorManager.registerListener(Cool.this, mSensor,
								SensorManager.SENSOR_DELAY_NORMAL);
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "onPause", new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						super.beforeHookedMethod(param);

						mSensorManager.unregisterListener(Cool.this, mSensor);
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "locationUpdate", Location.class, int[].class,
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
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
					mPlayerLongitude -= mMoveDistanceLongitude *
							(sensorX > 0 ? sensorX - mSensorThreshold : sensorX + mSensorThreshold);
					isPositionChanged = true;
				}

				float calibratedY = sensorY - 3; // For hand-held comfort
				if (calibratedY > mSensorThreshold || calibratedY < -mSensorThreshold) {
					mPlayerLatitude -= mMoveDistanceLatitude *
							(calibratedY > 0 ? calibratedY - mSensorThreshold :
									calibratedY + mSensorThreshold);
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
