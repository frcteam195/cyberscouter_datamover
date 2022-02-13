package com.frcteam195.cyberscouter_datamover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
// import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final private static int ThirtyThree = 33;
    private ComponentName _serviceComponentName = null;
    private Button button;

    private Handler mConfigHandler;

    public static AppCompatActivity _activity;

    private Thread fetcherThread;
    final private static int START_PROGRESS = 0;
    final private static int UPDATE_CONFIG = 1;

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter _bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (null != _bluetoothAdapter && !_bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, ThirtyThree);
            } else {
                updateStatusIndicator(Color.GREEN);
                startBackgroundActivity();
            }
        } else {
            updateStatusIndicator(Color.RED);
        }

        button = findViewById(R.id.button_fetch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch();
            }
        });

        button = findViewById(R.id.button_push);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                push();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ThirtyThree) {
            for(int i=0; i<permissions.length; ++i) {
                if(permissions[i] == Manifest.permission.BLUETOOTH_CONNECT && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    updateStatusIndicator(Color.GREEN);
                    startBackgroundActivity();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String last_fetch_key = getResources().getString(R.string.str_last_fetch_key);
        String last_fetch = sharedPref.getString(last_fetch_key, "None");
        String last_push_key = getResources().getString(R.string.str_last_push_key);
        String last_push = sharedPref.getString(last_push_key, "None");

        TextView tv = findViewById(R.id.textView_last_fetch);
        String s = getResources().getString(R.string.str_last_fetch);
        String sout = s + ": " + last_fetch;
        tv.setText(sout);

        tv = findViewById(R.id.textView_last_push);
        s = getResources().getString(R.string.str_last_push);
        sout = s + ": " + last_push;
        tv.setText(sout);
    }

    void startBackgroundActivity() {
        try {
            if (null == _serviceComponentName) {
                _activity = this;
                Intent backgroundIntent = new Intent(getApplicationContext(), BackgroundUpdater.class);
                _serviceComponentName = startService(backgroundIntent);
                if (null == _serviceComponentName) {
                    MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!");
                }
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!\n\n" +
                    "The error is:\n" + e.getMessage());
        }

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
    }

    void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
        button = findViewById(R.id.button_fetch);
        if(color == Color.GREEN) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

    void fetch() {
        String currentDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());


        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.str_last_fetch_key), currentDate);
        editor.putString(getString(R.string.str_last_push_key), "None");
        editor.apply();
        onResume();
    }

    void push() {
        String currentDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.str_last_push_key), currentDate);
        editor.apply();
        onResume();
    }

}