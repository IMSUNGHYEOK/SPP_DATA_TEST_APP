package com.example.user.spp_application;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothService {

    private static final String TAG = "BluetoothService";

    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int REQUEST_CONNECT_DEVICE =1;
    public static final int REQUEST_ENABLE_BT = 2;

    private int mState;

    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    public static final int STATE_FAIL = 7;

    public int mMode;


    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;


    public BluetoothService(Activity ac, Handler h){
        mActivity = ac;
        mHandler = h;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /// Check bluetooth Service Support
    public  boolean getDeviceState() {
        Log.d(TAG, "Check Bluetooth Support");

        if(btAdapter ==null){
            Log.d(TAG, "Bluetooth is not available");
            return false;
        }else{
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    private synchronized void setState(int mState){
        Log.d(TAG, "setState() " + this.mState + " -> " + mState);
        this.mState = mState;

        mHandler.obtainMessage(DeviceScanActivity.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
    }

    public synchronized int getState(){
        return mState;
    }

    public synchronized void start(){
        Log.d(TAG, "start");// Cancel any thread attempting to make a connection
        if (mConnectThread == null) {
        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread == null) {
        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    //Get Device Information
    public void getDeviceInfo(Intent data){
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        Log.d(TAG, "Get Device address: " + address);

        connect(device);

        Log.i(TAG, "DEVICE NAME : "+device.getName().toString());
        DeviceScanActivity.txt_DeviceState.setText(device.getName().toString());
    }

    public synchronized void connect(BluetoothDevice device){
        Log.d(TAG, "connect to: " + device);

        if(mState == STATE_CONNECTING){
            if(mConnectThread == null){
            }else{
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if(mConnectedThread == null){
        }else{
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected");

        if(mConnectThread == null){
        }else{
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread == null){
        }else{
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(STATE_CONNECTED);
    }

    public synchronized void stop(){
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            Log.i("connect","ConnectThread initialized");
            mmDevice = device;
            BluetoothSocket tmp = null;

            try{
                tmp = device.createRfcommSocketToServiceRecord(mUUID);
                //tmp.getInputStream();
            }catch (IOException e){
                Log.e(TAG, "Connection Failed");
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "Start mConnectThread");
            setName("Connect Thread");

            btAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
                Log.d(TAG, "Connect Success");
                //DeviceScanActivity.txt_DeviceState.setText(DeviceScanActivity.txt_DeviceState.getText().toString() + " 과 연결됨");
            }catch (IOException e){
                connectionFailed();
                Log.e(TAG, "Connect Failed");
                //DeviceScanActivity.txt_DeviceState.setText(DeviceScanActivity.txt_DeviceState.getText().toString() + " 과 연결되지않음");
                try{
                    mmSocket.close();
                }catch (IOException e2){}

                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this){mConnectThread = null;}
            connected(mmSocket, mmDevice);
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "Connect Failed");
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.i(TAG,"ConnectedThread initialized");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            super.run();
            Log.i("connected", "BEGIN mConnectedThread");
            byte[] buffer = new byte[20];
            int bytes;

            while (true){
                try { // InputStream으로부터 값을 받는 읽는 부분(값을 받는다)
                    bytes = mmInStream.read(buffer);
                    if(buffer == null){
                        mHandler.obtainMessage(DeviceScanActivity.MESSAGE_READ_NONE,bytes,-1,buffer).sendToTarget();
                        DeviceScanActivity.txt_receiveData.setText(DeviceScanActivity.txt_receiveData.getText().toString() + " Device로부터 수신된 Data가 없습니다.");
                    }
                    mHandler.obtainMessage(DeviceScanActivity.MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    Log.i("READ MSG", "READ MSG - Bytes : " +bytes+"-Buffer : " + buffer);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] data, int mode){
            try {
                mmOutStream.write(data);
                mMode = mode;
                if(mode == DeviceScanActivity.MODE_REQUEST){
                    mHandler.obtainMessage(DeviceScanActivity.MESSAGE_STATE_CHANGE,-1,-1,data).sendToTarget();
                }
            }catch (IOException e){
                Log.e(TAG, "Exception during write");
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "Connect Failed");
            }
        }

    }

    public void write(byte[] data, int mode){
        ConnectedThread connectedThread;
        synchronized (this){
            if(mState != STATE_CONNECTED)
                return;
            connectedThread = mConnectedThread;
        }
        connectedThread.write(data, mode);
    }

    public void run(){
        ConnectedThread connectedThread;
        synchronized (this){
            if(mState != STATE_CONNECTED)
                return;
            connectedThread = mConnectedThread;
        }
    }

    private void connectionFailed(){
        setState(STATE_FAIL);
    }

    private void connectionLost(){
        setState(STATE_LISTEN);
    }
    /// Turn on bluetooth
    public void enableBluetooth(){
        Log.i(TAG, "Check the enable Bluetooth");

        if(btAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth Enable Now");

            scanDevice();
        }else{
            Log.d(TAG, "Bluetooth Enable Requeset");

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    //Device Scan
    public void scanDevice(){
        Log.d(TAG, "Scan Device");

        Intent serverIntent = new Intent(mActivity,DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
}
