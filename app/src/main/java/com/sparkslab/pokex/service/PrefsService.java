package com.sparkslab.pokex.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.sparkslab.pokex.lib.Prefs;

/**
 * Service to create overlay
 *
 * @author Created by hiking on 2016/7/17.
 */
public class PrefsService extends Service {

	public static final int RESULT_FETCHED_PREFS = 200;

	public interface ResultCallback {

		void onPrefsFetched(Bundle prefs);
	}

	public static Intent getServiceIntent(@Nullable ResultCallback callback) {
		Intent intent = new Intent();
		intent.setComponent(PrefsService.getComponentName());
		if (callback != null) {
			intent.putExtras(PrefsService.getReceiverExtras(callback));
		}
		return intent;
	}

	private static ComponentName getComponentName() {
		return new ComponentName("com.sparkslab.pokex", "com.sparkslab.pokex.service.PrefsService");
	}

	private static Bundle getReceiverExtras(final ResultCallback callback) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("receiver", new ResultReceiver(null) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);

				switch (resultCode) {
					case RESULT_FETCHED_PREFS: {
						callback.onPrefsFetched(resultData);
					}
					break;
				}
			}
		});
		return bundle;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Object extra = intent.getParcelableExtra("receiver");
			if (extra instanceof ResultReceiver) {
				ResultReceiver resultReceiver = ((ResultReceiver) extra);
				resultReceiver.send(RESULT_FETCHED_PREFS, Prefs.getAll(this));
			}
		}
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

