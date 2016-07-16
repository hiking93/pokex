package go.pokemon.pokemon;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

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
    //    private SharedPreferences.Editor mEditor;
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
        if (!lpparam.packageName.equals("com.nianticlabs.pokemongo")) return;

        findAndHookConstructor("com.nianticlabs.nia.location.NianticLocationManager", lpparam.classLoader, Context.class, long.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                mContext = (Context) param.args[0];

                mSharedPreferences = new XSharedPreferences("go.pokemon.pokemon", "pokemon");
//                mEditor = mContext.getSharedPreferences("pokemon", Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE).edit();

                mSensorThreshold = Double.parseDouble(mSharedPreferences.getString("sensor_threshold", "1.5"));
                mMinimumTimeInterval = Integer.parseInt(mSharedPreferences.getString("minimum_time_interval", "250"));
                mMoveDistanceLatitude = Double.parseDouble(mSharedPreferences.getString("move_distance_latitude", "0.00005"));
                mMoveDistanceLongitude = Double.parseDouble(mSharedPreferences.getString("move_distance_longitude", "0.00005"));
                mPlayerLatitude = Double.parseDouble(mSharedPreferences.getString("respawn_location_latitude", "40.7589"));
                mPlayerLongitude = Double.parseDouble(mSharedPreferences.getString("respawn_location_longitude", "-73.9851"));

                mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        });

        findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager", lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                mSensorManager.registerListener(Cool.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager", lpparam.classLoader, "onPause", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

//                Log.i("fuck", "onPause " + String.valueOf(mPlayerLatitude) + "/" + String.valueOf(mPlayerLongitude));
//                mEditor.putString("respawn_location_latitude", String.valueOf(mPlayerLatitude));
//                mEditor.putString("respawn_location_longitude", String.valueOf(mPlayerLongitude));
//                mEditor.apply();

                mSensorManager.unregisterListener(Cool.this, mSensor);
            }
        });

        findAndHookMethod("com.nianticlabs.nia.location.NianticLocationManager", lpparam.classLoader, "locationUpdate", Location.class, int[].class, new XC_MethodReplacement() {
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
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            long currentTime = System.currentTimeMillis();
            if ((currentTime - mLastUpdate) > mMinimumTimeInterval) {
                mLastUpdate = currentTime;

                boolean isChange = false;
                if (x > mSensorThreshold) {
                    mPlayerLongitude -= mMoveDistanceLongitude;
                    isChange = true;
                } else if (x < -mSensorThreshold) {
                    mPlayerLongitude += mMoveDistanceLongitude;
                    isChange = true;
                }

                if (y > mSensorThreshold) {
                    mPlayerLatitude -= mMoveDistanceLatitude;
                    isChange = true;
                } else if (y < -mSensorThreshold) {
                    mPlayerLatitude += mMoveDistanceLatitude;
                    isChange = true;
                }

                if (isChange) {
                    gotoPlace(mPlayerLatitude, mPlayerLongitude);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void gotoPlace(double latitude, double longitude) {
        if (mLocation == null || mThisObject == null || mWhateverArray == null) return;

        mLocation.setLatitude(latitude);
        mLocation.setLongitude(longitude);
        XposedHelpers.callMethod(mThisObject, "nativeLocationUpdate", mLocation, mWhateverArray, mContext);

//        Log.i("fuck", "gotoPlace " + String.valueOf(mPlayerLatitude) + "/" + String.valueOf(mPlayerLongitude));
//        mEditor.putString("respawn_location_latitude", String.valueOf(mPlayerLatitude));
//        mEditor.putString("respawn_location_longitude", String.valueOf(mPlayerLongitude));
//        mEditor.apply();
    }
}
