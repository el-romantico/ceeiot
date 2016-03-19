package elromantico.com.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static double PI = 3.14159265359;

    private static Set<String> whitelist = new HashSet<>();
    static {
        whitelist.add("C0:EE:FB:58:B1:DE");
    }

    private Location location;
    private Direction direction;
    private CircularFifoBuffer locations = new CircularFifoBuffer(1000);

    private TextView signView;
    private TextView locationView;
    private TextView speedView;

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (whitelist.contains(device.getAddress())) {
                    String[] components = device.getName().split("\\|");

                    String signName = components[0];
                    String[] signLocation = components[1].split("\\_");

                    double latitude = Double.parseDouble(signLocation[0]);
                    double longtitude = Double.parseDouble(signLocation[1]);

                    location = new Location(latitude, longtitude, System.currentTimeMillis());
                    locations.add(location);
                    locationView.setText("Location: " + location.toString());

                    Object[] locationsObj = locations.toArray();
                    speedView.setText("Speed: " + calculateSpeed((Location) locationsObj[0], (Location) locationsObj[locationsObj.length-1]));
                }
            }
        }
    };

    private static double degToRad(double deg) {
        return deg * PI / 180;
    }

    private static double calculateSpeed(Location start, Location end) {
        double dLat = degToRad(end.latitude - start.latitude);
        double dLon = degToRad(end.longtitude - start.longtitude);
        double lat1 = degToRad(start.latitude);
        double lat2 = degToRad(end.latitude);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = 6371 * c / 1000;
        double duration = (end.timestamp - start.timestamp) / 3600000;
        return distance / duration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signView = (TextView) findViewById(R.id.sign);
        locationView = (TextView) findViewById(R.id.location);
        speedView = (TextView) findViewById(R.id.speed);

        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }
        bluetoothAdapter.startDiscovery();
    }
}
