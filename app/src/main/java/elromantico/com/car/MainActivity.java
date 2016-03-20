package elromantico.com.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private static Set<String> whitelist = new HashSet<>();
    private static final Map<String, Integer> SIGNS = new HashMap<>();
    private static ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
    private static List<ScanFilter> filters = new ArrayList<>();

    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;

    static {
        whitelist.add("C0:EE:FB:58:B1:DE");
        whitelist.add("10:68:3F:58:81:50");
        whitelist.add("F0:08:F1:F0:52:71");

        SIGNS.put("A12", R.drawable.alpha12);
        SIGNS.put("B2", R.drawable.beta2);
        SIGNS.put("B27", R.drawable.beta27);

        filters.add(new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID.fromString("CDB7950D-73F1-4D4D-8E47-C090502DBD63")))
                .build());
    }

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private GoogleMap mMap;

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

        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }

        mLEScanner = bluetoothAdapter.getBluetoothLeScanner();
        mHandler = new Handler();
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, 30000);

            mLEScanner.startScan(filters, settings, mScanCallback);
        } else {

            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice btDevice = result.getDevice();
            if (result == null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName())) {
                Log.d("KOR", "Nein");
            }

            StringBuilder builder = new StringBuilder(result.getDevice().getName());
            builder.append("\n").append(new String(result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0)), Charset.forName("UTF-8")));

            Log.d("OK", "Found1 " + btDevice.getName() + ", " + builder.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("KOR", "Error Code: " + errorCode);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
