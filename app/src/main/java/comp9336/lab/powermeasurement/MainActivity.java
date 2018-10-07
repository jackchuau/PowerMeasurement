package comp9336.lab.powermeasurement;

import android.view.*;
import android.widget.*;
import android.content.*;
import android.os.Bundle;
import android.os.BatteryManager;
import android.net.wifi.WifiManager;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean gps_ok, wifi_ok;
    private TextView text_view, text_view2;
    String chargeLevel, chargeMethod, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_view = (TextView)findViewById(R.id.textView);
        text_view2 = (TextView)findViewById(R.id.textView2);
    }

    public void check_GPS() {

        LocationManager manager;
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps_ok = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public void check_wifi() {

        WifiManager wifi;
        wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        wifi_ok = wifi.isWifiEnabled();

    }

    public void show_status(View source) {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float battery = level / (float)scale;
        chargeLevel = String.valueOf(battery * 100) + "%";

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
                chargeMethod = "USB";
            else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC)
                chargeMethod = "AC";
            else
                chargeMethod = "Wireless";

            text = "Current level of battery is: " + chargeLevel + ".\n\n" + "Mobile " +
                    "is charging via " + chargeMethod + ".";

            text_view.setText(text);

        }

        else {

            text = "Current level of battery is: " + chargeLevel + ".\n\n" + "Mobile " +
                    "is not charging.";

            text_view.setText(text);

        }

    }

    public void begin_test(View source) throws InterruptedException {

        int before_level, after_level;
        float before, after;
        String show_text;

        check_GPS();
        check_wifi();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        before_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        before = before_level / (float)scale;
        text_view2.setText("Please waite .....");
        Thread.sleep(60000);




        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        after_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        after = after_level / (float)scale;

        if (!gps_ok && !wifi_ok) {
            show_text = "Normal usage of mobile phone for 10 minutes: \n\n";
        }
        else if (gps_ok && !wifi_ok) {
            show_text = "Using GPS for 1 minutes: \n\n";
        }
        else if (!gps_ok && wifi_ok){
            show_text = "Using Wi-Fi for 1 minutes: \n\n";
        }
        else {
            show_text = "Using Wi-Fi and GPS for 1 minutes: \n\n";
        }

        show_text += "initial level of battery: " + before * 100 + "%.\n\n";
        show_text += "Final level: " + after * 100 + "%.\n\n";
        show_text += "Consumed battery: " + (before - after) * 100 + "%.\n";

        text_view2.setText(show_text);

    }
}
