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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockit);
        checkDeviceAdminRights(getApplicationContext());
        //turnOffScreenAndExit();
    }

    private void checkDeviceAdminRights(Context context) {
        DevicePolicyManager pm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                LockitAdminReceiver.class);
        boolean admin = pm.isAdminActive(adminReceiver);
        if(!admin) {
            TextView textView = (TextView) findViewById(R.id.statusTextView);
            textView.setText("Lockit needs administrator rights to be able to Lock your screen.");
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
            Toast.makeText(context, "Not admin", Toast.LENGTH_LONG).show();
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
                TextView textView = (TextView)findViewById(R.id.statusTextView);
                textView.setText("This screen can be dismissed, and will not show up on subsequent usages of this app.\nThanks for using Lockit.");
                turnScreenOff(getApplicationContext());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lockit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
