package com.frcteam195.cyberscouter_datamover;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
//import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.Locale;

public class BackgroundUpdater extends Service {
    boolean keepRunning;
//    Thread updaterThread;
    Thread pingerThread;
    BluetoothAdapter _bluetoothAdapter;

    private static final int _updaterInterval = 20000;
    private static final int _pingerInterval = 5000;

    public BackgroundUpdater() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        keepRunning = true;

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        _bluetoothAdapter = bluetoothManager.getAdapter();


//        if (null == updaterThread) {
//            updaterThread = new Thread(new updateRunner());
//            updaterThread.start();
//        }

        if (null == pingerThread) {
            pingerThread = new Thread(new commStatusRunner());
            pingerThread.start();
        }

        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        keepRunning = false;
//        updaterThread.interrupt();
    }

    private class updateRunner implements Runnable {

        @Override
        public void run() {
//            int cnt = 0;
//            while (keepRunning) {
//                cnt++;
//                try {
//                    CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(getApplicationContext());
//                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
//                    CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);
//
//                    if (null != cfg && !BluetoothComm.bLastBTCommFailed()) {
//                        try {
//                            CyberScouterMatchScouting[] csmsa = CyberScouterMatchScouting.getMatchesReadyToUpload(db,
//                                    cfg.getEvent_id(), cfg.getAlliance_station_id());
//                            if (null != csmsa) {
//                                for (CyberScouterMatchScouting csms : csmsa) {
//                                    String ret = csms.setMatchesRemote(MainActivity._activity);
//                                    if (ret.equalsIgnoreCase("success")) {
//                                        CyberScouterMatchScouting.updateMatchUploadStatus(db, csms.getMatchScoutingID(), UploadStatus.UPLOADED);
//                                        popToast(String.format(Locale.getDefault(), "Match %d, Team %s was uploaded successfully.", csms.getMatchNo(), csms.getTeam()));
//                                    } else {
//                                        popToast(String.format(Locale.getDefault(), "Loop #%d failed to upload a match.", cnt));
//                                    }
//                                }
//                            } else {
////                                popToast(String.format(Locale.getDefault(), "Loop #%d no matches to upload.", cnt));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        Thread.sleep(10);
//                        try {
//                            CyberScouterTeams[] csta = CyberScouterTeams.getTeamsReadyToUpload(db);
//                            if (null != csta && !BluetoothComm.bLastBTCommFailed()) {
//                                for (CyberScouterTeams cst : csta) {
//                                    String ret = cst.setTeamsRemote(MainActivity._activity);
//                                    if (ret.equalsIgnoreCase("success")) {
//                                        String l_column = CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS;
//                                        Integer l_value = UploadStatus.UPLOADED;
//                                        CyberScouterTeams.updateTeamMetric(db, l_column, l_value, cst.getTeam());
//                                        popToast(String.format(Locale.getDefault(), "Team %d was uploaded successfully.", cst.getTeam()));
//                                    } else {
//                                        popToast(String.format(Locale.getDefault(), "Loop #%d failed to upload a team.", cnt));
//                                    }
//                                }
//                            } else {
//                                //                              popToast(String.format(Locale.getDefault(), "Loop #%d no teams to upload.", cnt));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    Thread.sleep(_updaterInterval);
//
//                } catch (InterruptedException ie) {
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return;
        }

    }

    private void popToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class commStatusRunner implements Runnable {

        @Override
        public void run() {
            while (keepRunning) {
                try {
                    Thread.yield();
                        if (BluetoothComm.pingServer(MainActivity._activity)) {
                            BluetoothComm.setLastBTCommSucceeded();
                        } else {
                            BluetoothComm.setLastBTCommFailed();
                        }

                    Thread.yield();
                    sendCommStatusIndicatorUpdate();

                    Thread.sleep(_pingerInterval);

                } catch (Exception e) {
                    e.printStackTrace();
                    BluetoothComm.setLastBTCommFailed();
                    sendCommStatusIndicatorUpdate();
                }
            }
        }
    }

    protected void sendCommStatusIndicatorUpdate() {
        int color = Color.GREEN;
        if (BluetoothComm.bLastBTCommFailed())
            color = Color.RED;
        Intent i = new Intent(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER);
        i.putExtra("onlinestatus", color);
        getApplicationContext().sendBroadcast(i);
    }

}
