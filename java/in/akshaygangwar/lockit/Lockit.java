package in.akshaygangwar.lockit;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Lockit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDeviceAdminRights(getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    private void checkDeviceAdminRights(Context context) {
        DevicePolicyManager pm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                LockitAdminReceiver.class);
        boolean admin = pm.isAdminActive(adminReceiver);
        if(!admin) {
            getAdminRights();
        } else {
            turnOffScreenAndExit();
        }
    }

    private void turnOffScreenAndExit () {
        //first lock screen
        turnScreenOff(getApplicationContext());

        //then provide feedback
        ((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

        //schedule end of activity
        final Activity activity = this;
        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    //Ignore
                }
                activity.finish();
            }
        };
        t.start();
    }

    static void turnScreenOff (final Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                LockitAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
        }
    }

    private void getAdminRights() {
        Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName somethingSample = new ComponentName(this, LockitAdminReceiver.class);
        adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, somethingSample);
        startActivityForResult(adminIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                turnScreenOff(getApplicationContext());
            }
        }
    }

}
