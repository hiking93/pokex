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
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sparkslab.pokex.lib.Constant;
import com.sparkslab.pokex.lib.Prefs;
import com.sparkslab.pokex.lib.StringUtils;
import com.sparkslab.pokex.service.PrefsService;
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

	private Bundle mPrefsBundle;
	private float mPlayerLatitude, mPlayerLongitude;
	private float mSensorThreshold;
	private float mSensorCalibrationX, mSensorCalibrationY;
	private float mMoveDistanceLatitude, mMoveDistanceLongitude;
	private int mSensorUpdateInterval;

	private Location mLocation;
	private Object mThisObject;
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

						mContext.startService(
								PrefsService.getServiceIntent(new PrefsService.ResultCallback() {

									@Override
									public void onPrefsFetched(Bundle prefs) {
										mPrefsBundle = prefs;
										Log.d(Constant.TAG, StringUtils.toString(mPrefsBundle));

										// Initial player position
										mPlayerLatitude =
												mPrefsBundle.getFloat(Prefs.KEY_RESPAWN_LAT);
										mPlayerLongitude =
												mPrefsBundle.getFloat(Prefs.KEY_RESPAWN_LONG);
										mSensorThreshold =
												mPrefsBundle.getFloat(Prefs.KEY_SENSOR_THRESHOLD);
										mSensorUpdateInterval =
												mPrefsBundle.getInt(Prefs.KEY_UPDATE_INTERVAL);
										mSensorCalibrationX = mPrefsBundle
												.getFloat(Prefs.KEY_SENSOR_CALIBRATION_X);
										mSensorCalibrationY = mPrefsBundle
												.getFloat(Prefs.KEY_SENSOR_CALIBRATION_Y);
										mMoveDistanceLatitude = mPrefsBundle
												.getFloat(Prefs.KEY_MOVE_MULTIPLIER_LAT);
										mMoveDistanceLongitude = mPrefsBundle
												.getFloat(Prefs.KEY_MOVE_MULTIPLIER_LONG);
									}
								}));

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

						startSensorListening();
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
		Intent intent = SensorOverlayService.getServiceIntent(mIsSensorEnabled, callback);
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
		if (mServiceConnection != null) {
			mContext.unbindService(mServiceConnection);
			mServiceConnection = null;
		}
		mContext.stopService(new Intent(mContext, SensorOverlayService.class));
		mSensorManager.unregisterListener(this, mSensor);
	}

	private boolean isPrefsLoaded() {
		return mPrefsBundle != null;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (!mIsSensorEnabled || !isPrefsLoaded()) {
			return;
		}

		Sensor sensor = sensorEvent.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float sensorX = sensorEvent.values[0];
			float sensorY = sensorEvent.values[1];

			long currentTime = System.currentTimeMillis();
			if ((currentTime - mLastSensorUpdate) > mSensorUpdateInterval) {
				mLastSensorUpdate = currentTime;

				double calibratedX = -sensorX + mSensorCalibrationX;
				double calibratedY = -sensorY + mSensorCalibrationY;

				if (calibratedX * calibratedX + calibratedY * calibratedY >=
						mSensorThreshold * mSensorThreshold) {
					float sensorUpdateIntervalSecond = mSensorUpdateInterval / 1000f;
					double vectorLength =
							Math.sqrt(calibratedX * calibratedX + calibratedY * calibratedY);
					double effectiveLength = vectorLength - mSensorThreshold;
					double effectiveRatio = effectiveLength / vectorLength;

					double effectiveX = calibratedX * effectiveRatio;
					mPlayerLongitude += mMoveDistanceLongitude * sensorUpdateIntervalSecond *
							effectiveX;

					double effectiveY = calibratedY * effectiveRatio;
					mPlayerLatitude += mMoveDistanceLatitude * sensorUpdateIntervalSecond *
							effectiveY;
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

				gotoPlace(mPlayerLatitude, mPlayerLongitude);
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
