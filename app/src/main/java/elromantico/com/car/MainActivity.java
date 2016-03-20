package elromantico.com.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private static Set<String> whitelist = new HashSet<>();
    private static final Map<String, Integer> SIGNS = new HashMap<>();
    private static final ConnectedSignPool pool = new ConnectedSignPool();

    static {
        whitelist.add("C0:EE:FB:58:B1:DE");
        whitelist.add("10:68:3F:58:81:50");
        whitelist.add("F0:08:F1:F0:52:71");

        SIGNS.put("A12", R.drawable.alpha12);
        SIGNS.put("B2", R.drawable.beta2);
        SIGNS.put("B27", R.drawable.beta27);
        SIGNS.put("E17", R.drawable.epsilon17);
    }

    private Location location;
    private Direction direction;
    private CircularFifoBuffer locations = new CircularFifoBuffer(1000);
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                    String[] signLocation = components[1].split("\\_");

                    double latitude = Double.parseDouble(signLocation[0]);
                    double longitude = Double.parseDouble(signLocation[1]);

                    long currentTime = System.currentTimeMillis();
                    location = new Location(latitude, longitude, currentTime);

                    LatLng markerPosition = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(markerPosition));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15.0f));

                    locations.add(location);

                    Object[] locationsObj = locations.toArray();
                    if (locationsObj.length > 1) {
                        locationView.append("Speed: " + SpeedCalculator.calculateSpeed((Location) locationsObj[locationsObj.length - 2],
                                (Location) locationsObj[locationsObj.length - 1]) + "\n");
                    } else {
                        locationView.append("Speed: Only one!\n");
                    }

                    String signName = components[0];
                    pool.insert(ConnectedSignFactory.create(signName, currentTime));

                    updateSignsUI(pool.toOrderedSignList());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothAdapter.startDiscovery();
            }
        }
    };

    private void updateSignsUI(List<ConnectedSign> orderedConnectedSigns){
        ImageView[] signs = new ImageView[5];
        signs[0] = (ImageView) findViewById(R.id.bigSign);
        signs[1] = (ImageView) findViewById(R.id.smallSign1);
        signs[2] = (ImageView) findViewById(R.id.smallSign2);
        signs[3] = (ImageView) findViewById(R.id.smallSign3);
        signs[4] = (ImageView) findViewById(R.id.smallSign4);

        for(int i = 0; i < signs.length; i++) {
            if(orderedConnectedSigns.size() > i) {
                signs[i].setImageDrawable(getDrawable(SIGNS.get(orderedConnectedSigns.get(i).signName)));
                signs[i].setVisibility(View.VISIBLE);
            } else {
                signs[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });

        //signView = (TextView) findViewById(R.id.sign);
        locationView = (TextView) findViewById(R.id.location);
        //speedView = (TextView) findViewById(R.id.speed);

        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }
        bluetoothAdapter.startDiscovery();

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        ImageView bigSign = (ImageView) findViewById(R.id.bigSign);
//
//        int cx = bigSign.getWidth() / 2;
//        int cy = bigSign.getHeight() / 2;
//        float finalRadius = (float) Math.hypot(cx, cy);
//
//        Animator animator = ViewAnimationUtils.createCircularReveal(bigSign, cx, cy, 0, finalRadius);
//
//        bigSign.setVisibility(View.VISIBLE);
//        animator.start();
//    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);

        super.onDestroy();
    }
}
