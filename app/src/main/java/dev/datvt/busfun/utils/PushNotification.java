package dev.datvt.busfun.utils;

import android.app.Application;

import com.urbanairship.Logger;
import com.urbanairship.UAirship;

/**
 * Created by datvt on 5/11/2016.
 */
public class PushNotification extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {

                // Enable user notifications
                airship.getPushManager().setUserNotificationsEnabled(true);
                airship.getPushManager().setPushEnabled(true);
            }
        });

        String channelId = UAirship.shared().getPushManager().getChannelId();
        Logger.info("My Application Channel ID: " + channelId);

        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

    }
}
