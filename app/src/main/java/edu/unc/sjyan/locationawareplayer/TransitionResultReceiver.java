package edu.unc.sjyan.locationawareplayer;

import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Stephen on 3/28/16.
 */
public class TransitionResultReceiver extends ResultReceiver {

    private Receiver mReceiver;
    public String stringField;
    public int intField;

    public TransitionResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public TransitionResultReceiver fromIntent(Intent intent) {
        stringField = intent.getStringExtra("stringField");
        intField = intent.getIntExtra("intField", -1);
        return this;
    }

    public TransitionResultReceiver toIntent(Intent intent) {
        intent.putExtra("stringField", stringField);
        intent.putExtra("intField", intField);
        return this;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
