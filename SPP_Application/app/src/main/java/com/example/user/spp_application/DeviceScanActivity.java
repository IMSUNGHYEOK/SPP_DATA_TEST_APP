package com.example.user.spp_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

public class DeviceScanActivity extends AppCompatActivity implements View.OnClickListener{

    private  static final String TAG = "DeviceScanActivity";

    public static final int REQUEST_CONNECT_DEVICE =1;
    public static final int REQUEST_ENABLE_BT = 2;

    public static TextView txt_DeviceState;

    public static final int MODE_REQUEST = 1;

    private  int mBtn;
    private static final int STATE_SENDING = 1 ;
    private static final int STATE_NO_SENDING = 2 ;
    private int mSendingState ;

    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_WRTIE = 2;
    public static final int MESSAGE_READ = 3;
    public static final int MESSAGE_READ_NONE = 4;

    private BluetoothService btService = null;
    private StringBuffer mOutStringBuffer;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
            case MESSAGE_STATE_CHANGE:
                if(D)
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Success Connect Device", Toast.LENGTH_SHORT).show();
                        txt_DeviceState.setText(txt_DeviceState.getText().toString()+" 과 연결됨");
                        break;

                    case BluetoothService.STATE_FAIL:
                        Toast.makeText(getApplicationContext(), "Failed Connect Device", Toast.LENGTH_SHORT).show();
                        txt_DeviceState.setText(txt_DeviceState.getText().toString()+" 과 연결되지 않음");
                        break;
                }
            break;
            case MESSAGE_WRTIE:
                String writeMessage = null;
                if(mBtn==1){
                    writeMessage = edit_Write1.getText().toString()+
                            edit_Write2.getText().toString() + edit_Write3.getText().toString() + edit_Write4.getText().toString()+
                            edit_Write5.getText().toString() + edit_Write6.getText().toString() + edit_Write7.getText().toString()+
                            edit_Write8.getText().toString() + edit_Write9.getText().toString() + edit_Write10.getText().toString()+
                            edit_Write11.getText().toString() + edit_Write12.getText().toString() + edit_Write13.getText().toString()+
                            edit_Write14.getText().toString() + edit_Write15.getText().toString() + edit_Write16.getText().toString()+
                            edit_Write17.getText().toString();
                    mBtn = -1;
                }else{
                    byte[] writeBuf = (byte[]) msg.obj;
                    writeMessage = new String(writeBuf);
                }
                break;
             case MESSAGE_READ:
                 byte[] readBuf = (byte[]) msg.obj;
                 if(readBuf!=null)
                    txt_receiveData.setText(txt_receiveData.getText().toString() + byteArrayToHex(readBuf));
                 break;
                case MESSAGE_READ_NONE:
                    txt_receiveData.setText(txt_receiveData.getText().toString() + " Device로부터 수신된 Data가 없습니다.");
                    break;
            }
        }
    };


    Button btn_Start;
    Button btn_Send;

    private EditText edit_Write1;
    private EditText edit_Write2;
    private EditText edit_Write3;
    private EditText edit_Write4;
    private EditText edit_Write5;
    private EditText edit_Write6;
    private EditText edit_Write7;
    private EditText edit_Write8;
    private EditText edit_Write9;
    private EditText edit_Write10;
    private EditText edit_Write11;
    private EditText edit_Write12;
    private EditText edit_Write13;
    private EditText edit_Write14;
    private EditText edit_Write15;
    private EditText edit_Write16;
    private EditText edit_Write17;

    public static TextView txt_receiveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mBtn = -1;
        btn_Start = (Button) findViewById(R.id.btn_Start);
        btn_Start.setOnClickListener(this);
        btn_Send = (Button) findViewById(R.id.btn_Send);
        btn_Send.setOnClickListener(this);
        txt_DeviceState = (TextView)findViewById(R.id.txt_deviceState);
        txt_receiveData = (TextView)findViewById(R.id.txt_receiveData);

        edit_Write1 = (EditText) findViewById(R.id.edit_Write1);
        edit_Write1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write1.length()==2)
                    edit_Write2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write2 = (EditText) findViewById(R.id.edit_Write2);
        edit_Write2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write2.length()==2)
                    edit_Write3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write3 = (EditText) findViewById(R.id.edit_Write3);
        edit_Write3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write3.length()==2)
                    edit_Write4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write4 = (EditText) findViewById(R.id.edit_Write4);
        edit_Write4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write4.length()==2)
                    edit_Write5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write5 = (EditText) findViewById(R.id.edit_Write5);
        edit_Write5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write5.length()==2)
                    edit_Write6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write6 = (EditText) findViewById(R.id.edit_Write6);
        edit_Write6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write6.length()==2)
                    edit_Write7.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write7 = (EditText) findViewById(R.id.edit_Write7);
        edit_Write7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write7.length()==2)
                    edit_Write8.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write8 = (EditText) findViewById(R.id.edit_Write8);
        edit_Write8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write8.length()==2)
                    edit_Write9.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write9 = (EditText) findViewById(R.id.edit_Write9);
        edit_Write9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write9.length()==2)
                    edit_Write10.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write10 = (EditText) findViewById(R.id.edit_Write10);
        edit_Write10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write10.length()==2)
                    edit_Write11.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write11 = (EditText) findViewById(R.id.edit_Write11);
        edit_Write11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write11.length()==2)
                    edit_Write12.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write12 = (EditText) findViewById(R.id.edit_Write12);
        edit_Write12.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write12.length()==2)
                    edit_Write13.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write13 = (EditText) findViewById(R.id.edit_Write13);
        edit_Write13.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write13.length()==2)
                    edit_Write14.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write14 = (EditText) findViewById(R.id.edit_Write14);
        edit_Write14.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write14.length()==2)
                    edit_Write15.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write15 = (EditText) findViewById(R.id.edit_Write15);
        edit_Write15.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write15.length()==2)
                    edit_Write16.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write16 = (EditText) findViewById(R.id.edit_Write16);
        edit_Write16.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write16.length()==2)
                    edit_Write17.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write17 = (EditText) findViewById(R.id.edit_Write17);

        //create BlutoothService
        if(btService ==null) {
            btService = new BluetoothService(this, mHandler);
            mOutStringBuffer = new StringBuffer("");
        }
    }

    //Bluetooth Request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                //if(requestCode == Activity.RESULT_OK)
                    btService.getDeviceInfo(data);
                break;

            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK){
                    btService.scanDevice();
                }else{
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Start:
                if(btService.getDeviceState()){
                    btService.enableBluetooth();
                }else{
                    finish();
                }
                break;
            case R.id.btn_Send:
                if(btService.getState() == BluetoothService.STATE_CONNECTED){

                    plusZero();
                    txt_receiveData.setText("Data : ");
                    sendMessage(hexStringToByteArray(edit_Write1.getText().toString()+
                            edit_Write2.getText().toString() + edit_Write3.getText().toString() + edit_Write4.getText().toString()+
                            edit_Write5.getText().toString() + edit_Write6.getText().toString() + edit_Write7.getText().toString()+
                            edit_Write8.getText().toString() + edit_Write9.getText().toString() + edit_Write10.getText().toString()+
                            edit_Write11.getText().toString() + edit_Write12.getText().toString() + edit_Write13.getText().toString()+
                            edit_Write14.getText().toString() + edit_Write15.getText().toString() + edit_Write16.getText().toString()+
                            edit_Write17.getText().toString()),MODE_REQUEST);
                }else{
                    Toast.makeText(getApplicationContext(), "먼저 디바이스를 연결해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //btService.write(hexStringToByteArray(edit_writeData.getText().toString()));
               /* Log.i(TAG, "Write Data by Txt : " + edit_writeData.getText().toString());
                Log.i(TAG, "Write Data by HexStr: " + byteArrayToHex(edit_writeData.getText().toString().getBytes()) );
                Log.i(TAG,"Write Data by HexByte: " + hexStringToByteArray(edit_writeData.getText().toString()).toString());*/
                break;
        }
    }

    private synchronized void sendMessage(byte[] message, int mode){
        if(mSendingState == STATE_SENDING){
            try{
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        mSendingState = STATE_SENDING;

        if(btService.getState() != BluetoothService.STATE_CONNECTED){
            mSendingState = STATE_NO_SENDING;
            return;
        }

        if(message.length>0){
            byte[] send = message;
            btService.write(send, mode);

            mOutStringBuffer.setLength(0);
        }

        mSendingState = STATE_NO_SENDING;
        notify();
    }

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String str){
        int len = str.length();
        byte[] data = new byte[len/2];
        for(int i=0; i<len; i+=2)
        {
            data[i/2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                    + Character.digit(str.charAt(i+1), 16));
        }
        return data;
    }

    public void plusZero()
    {
        if(edit_Write1.length()<2 && edit_Write1.length()!=0)
            edit_Write1.setText("0"+edit_Write1.getText().toString());
        if(edit_Write2.length()<2&& edit_Write2.length()!=0)
            edit_Write2.setText("0"+edit_Write2.getText().toString());
        if(edit_Write3.length()<2&& edit_Write3.length()!=0)
            edit_Write3.setText("0"+edit_Write3.getText().toString());
        if(edit_Write4.length()<2&& edit_Write4.length()!=0)
            edit_Write4.setText("0"+edit_Write4.getText().toString());
        if(edit_Write5.length()<2&& edit_Write5.length()!=0)
            edit_Write5.setText("0"+edit_Write5.getText().toString());
        if(edit_Write6.length()<2&& edit_Write6.length()!=0)
            edit_Write6.setText("0"+edit_Write6.getText().toString());
        if(edit_Write7.length()<2&& edit_Write7.length()!=0)
            edit_Write7.setText("0"+edit_Write7.getText().toString());
        if(edit_Write8.length()<2&& edit_Write8.length()!=0)
            edit_Write8.setText("0"+edit_Write8.getText().toString());
        if(edit_Write9.length()<2&& edit_Write9.length()!=0)
            edit_Write9.setText("0"+edit_Write9.getText().toString());
        if(edit_Write10.length()<2&& edit_Write10.length()!=0)
            edit_Write10.setText("0"+edit_Write10.getText().toString());
        if(edit_Write11.length()<2&& edit_Write11.length()!=0)
            edit_Write11.setText("0"+edit_Write11.getText().toString());
        if(edit_Write12.length()<2&& edit_Write12.length()!=0)
            edit_Write12.setText("0"+edit_Write12.getText().toString());
        if(edit_Write13.length()<2&& edit_Write13.length()!=0)
            edit_Write13.setText("0"+edit_Write13.getText().toString());
        if(edit_Write14.length()<2&& edit_Write14.length()!=0)
            edit_Write14.setText("0"+edit_Write14.getText().toString());
        if(edit_Write15.length()<2&& edit_Write15.length()!=0)
            edit_Write15.setText("0"+edit_Write15.getText().toString());
        if(edit_Write16.length()<2 && edit_Write16.length()!=0)
            edit_Write16.setText("0"+edit_Write16.getText().toString());
        if(edit_Write17.length()<2 && edit_Write17.length()!=0)
            edit_Write17.setText("0"+edit_Write17.getText().toString());
    }
}
