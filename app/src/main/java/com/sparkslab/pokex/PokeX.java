package com.sparkslab.pokex;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sparkslab.pokex.lib.Constant;
import com.sparkslab.pokex.lib.Prefs;
import com.sparkslab.pokex.lib.Utils;
import com.sparkslab.pokex.service.SensorOverlayService;

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
public class PokeX implements IXposedHookLoadPackage, SensorEventListener {

	private Context mContext;
	private ServiceConnection mServiceConnection;
	private Messenger mService;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Object mThisObject;
	private Location mLocation;

	private float mPlayerLatitude, mPlayerLongitude;
	private float mSensorThreshold;
	private float mSensorCalibrationX, mSensorCalibrationY;
	private float mMoveDistanceLatitude, mMoveDistanceLongitude;
	private int mSensorUpdateInterval;
	private int[] mWhateverArray;

	private long mLastSensorUpdate;
	private boolean mIsSensorEnabled = true;

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

						// Initial player position
						mPlayerLatitude = Prefs.getXFloat(mContext, Prefs.KEY_RESPAWN_LAT);
						mPlayerLongitude = Prefs.getXFloat(mContext, Prefs.KEY_RESPAWN_LONG);

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

						Log.d(Constant.TAG, "onResume");

						Prefs.refreshX(mContext); // Update params
						mSensorThreshold = Prefs.getXFloat(mContext, Prefs.KEY_SENSOR_THRESHOLD);
						mSensorUpdateInterval = Prefs.getXInt(mContext, Prefs.KEY_UPDATE_INTERVAL);
						mSensorCalibrationX =
								Prefs.getXInt(mContext, Prefs.KEY_SENSOR_CALIBRATION_X);
						mSensorCalibrationY =
								Prefs.getXInt(mContext, Prefs.KEY_SENSOR_CALIBRATION_Y);
						mMoveDistanceLatitude =
								Prefs.getXFloat(mContext, Prefs.KEY_MOVE_MULTIPLIER_LAT);
						mMoveDistanceLongitude =
								Prefs.getXFloat(mContext, Prefs.KEY_MOVE_MULTIPLIER_LONG);
						Log.d(Constant.TAG,
								"mSensorThreshold = " + Utils.toDecimalString(mSensorThreshold) +
										"\nmSensorUpdateInterval = " +
										Utils.toDecimalString(mSensorUpdateInterval) +
										"\nmMoveDistanceLatitude = " +
										Utils.toDecimalString(mMoveDistanceLatitude) +
										"\nmMoveDistanceLongitude = " +
										Utils.toDecimalString(mMoveDistanceLongitude) +
										"\nmPlayerLatitude = " +
										Utils.toDecimalString(mPlayerLatitude) +
										"\nmPlayerLongitude = " +
										Utils.toDecimalString(mPlayerLongitude));

						startSensorListening();
						Log.w("PokeEventBus", "registered!");
					}
				});

		findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager",
				lpparam.classLoader, "onPause", new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						super.beforeHookedMethod(param);

						Log.d(Constant.TAG, "onPause");

						stopSensorListening();
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

	private void startSensorListening() {
		if (mServiceConnection != null) {
			mContext.unbindService(mServiceConnection);
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
		mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

		mSensorManager.registerListener(PokeX.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stopSensorListening() {
		mContext.unbindService(mServiceConnection);
		mServiceConnection = null;
		mSensorManager.unregisterListener(this, mSensor);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (!mIsSensorEnabled) {
			return;
		}

		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float sensorX = sensorEvent.values[0];
			float sensorY = sensorEvent.values[1];

			long currentTime = System.currentTimeMillis();
			if ((currentTime - mLastSensorUpdate) > mSensorUpdateInterval) {
				mLastSensorUpdate = currentTime;
				boolean isPositionChanged = false;

				float calibratedX = -sensorX + mSensorCalibrationX;
				float calibratedY = -sensorY + mSensorCalibrationY;

				if (calibratedX * calibratedX + calibratedY * calibratedY >=
						mSensorThreshold * mSensorThreshold) {
					float sensorUpdateIntervalSecond = mSensorUpdateInterval / 1000f;
					mPlayerLongitude += mMoveDistanceLongitude * sensorUpdateIntervalSecond *
							(calibratedX > 0 ? calibratedX - mSensorThreshold :
									calibratedX + mSensorThreshold);
					mPlayerLatitude += mMoveDistanceLatitude * sensorUpdateIntervalSecond *
							(calibratedY > 0 ? calibratedY - mSensorThreshold :
									calibratedY + mSensorThreshold);
					isPositionChanged = true;
				}

				if (mService != null) {
					try {
						mService.send(SensorOverlayService
								.createSensorEventMessage(sensorEvent, mSensorCalibrationX,
										mSensorCalibrationY));
					} catch (RemoteException e) {
						e.printStackTrace();
					}
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

		if (mService != null) {
			try {
				mService.send(
						SensorOverlayService.createLocationUpdateMessage(latitude, longitude));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
