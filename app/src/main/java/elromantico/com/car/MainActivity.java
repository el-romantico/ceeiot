package elromantico.com.car;

import android.animation.Animator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private static Set<String> whitelist = new HashSet<>();

    static {
        whitelist.add("C0:EE:FB:58:B1:DE");
        whitelist.add("10:68:3F:58:81:50");
        whitelist.add("F0:08:F1:F0:52:71");
    }

    private Location location;
    private Direction direction;
    private CircularFifoBuffer locations = new CircularFifoBuffer(1000);

    private GoogleMap mMap;
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
                    double longitude = Double.parseDouble(signLocation[1]);

                    location = new Location(latitude, longitude, System.currentTimeMillis());

                    LatLng markerPosition = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(markerPosition));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));

                    locations.add(location);
                    locationView.setText("Location: " + location.toString());

                    Object[] locationsObj = locations.toArray();
                    if (locationsObj.length > 1) {
                        speedView.setText("Speed: " + SpeedCalculator.calculateSpeed((Location) locationsObj[locationsObj.length - 2],
                                (Location) locationsObj[locationsObj.length - 1]));
                    } else {
                        speedView.setText("Speed: Only one!");
                    }
                }
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView bigSign = (ImageView) findViewById(R.id.bigSign);

        int cx = bigSign.getWidth() / 2;
        int cy = bigSign.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator animator = ViewAnimationUtils.createCircularReveal(bigSign, cx, cy, 0, finalRadius);

        bigSign.setVisibility(View.VISIBLE);
        animator.start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);

        super.onDestroy();
    }
}
