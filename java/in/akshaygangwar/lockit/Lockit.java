/*
*
* Lockit
  Copyright (C) 2016  Akshay Gangwar

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

  http://www.akshaygangwar.in
*
**/

package in.akshaygangwar.lockit;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Lockit extends AppCompatActivity {

    public void main () {}

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
        //schedule end of activity
        this.finish();
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
