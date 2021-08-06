package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device admin permission received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "are you sure?";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device admin permission revoked", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        // here you must re-lock your app. make your activity know of this event and make it call startLockTask again!
    }

}
