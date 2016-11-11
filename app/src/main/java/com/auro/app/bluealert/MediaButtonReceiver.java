package com.auro.app.bluealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by AURO on 3/19/2016.
 */
public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        KeyEvent keyEvent =intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN)
            System.out.println("JUST DO SOME CRAP!!");


    }


}

