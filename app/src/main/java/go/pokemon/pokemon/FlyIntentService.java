package go.pokemon.pokemon;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import de.robv.android.xposed.XposedHelpers;


public class FlyIntentService extends IntentService {

    public FlyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
