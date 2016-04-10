package soler.lluis.androidusb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    public static String sendString = "";
    public static String sendSubstring = "";
    Button startButton, sendButton, clearButton, stopButton;
    TextView textView;
    EditText editText;
    Switch switchPin12;
    Switch switchPin13;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String receivedString = null;
            try {
                receivedString = new String(arg0, "UTF-8");

//                // Check if the echo matches with the sent message
//                int recLng = receivedString.length();
//                int subLng = sendSubstring.length();
//                tvAppend(textView, "Data Received: " + receivedString + "\n");
//                tvAppend(textView, "Expected substring: " + sendSubstring + "\n");
//                String checkSubstring;
//                if (recLng > 0 && subLng > 0){
//                    checkSubstring = sendSubstring.substring(0,recLng);
//                    tvAppend(textView, "Check substring: " + checkSubstring + "\n");
//                    if (checkSubstring.equals(receivedString)){
//                        if (recLng == subLng){
//                            // Received matches with what was sent
//                            tvAppend(textView, "Match! \n");
//                            sendSubstring = "";
//                            sendString = "";
//                        } else {
//                            // Received partially matches with what was sent
//                            tvAppend(textView, "Partial match! " + receivedString + " & " + checkSubstring + "\n");
//                            sendSubstring = sendSubstring.substring(recLng,subLng);
//                        }
//                    } else {
//                        // Received does not match with what was sent
//                        tvAppend(textView, "Received " + receivedString + " does not match with expected " + checkSubstring + "\n");
//                    }
//                    tvAppend(textView, "Remaining substring: " + sendSubstring + "\n");
//                }

                receivedString.concat("\n");
                tvAppend(textView, receivedString);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(textView,"Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        editText = (EditText) findViewById(R.id.editText);
        //TextView.setMovementMethod(new ScrollingMovementMethod());
        textView = (TextView) findViewById(R.id.textView);
        switchPin12 = (Switch) findViewById(R.id.togglePin12);
        switchPin13 = (Switch) findViewById(R.id.togglePin13);
        switchPin12.setChecked(false);
        switchPin13.setChecked(false);
        switchPin12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    sendString = "a";
                    sendSubstring = sendString;
                    serialPort.write(sendString.getBytes());
                    tvAppend(textView, "\nData Sent: " + sendString + "\n");
                } else {
                    sendString = "b";
                    sendSubstring = sendString;
                    serialPort.write(sendString.getBytes());
                    tvAppend(textView, "\nData Sent: " + sendString + "\n");
                }
            }
        });
        switchPin13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    sendString = "c";
                    sendSubstring = sendString;
                    serialPort.write(sendString.getBytes());
                    tvAppend(textView, "\nData Sent: " + sendString + "\n");
                }else{
                    sendString = "d";
                    sendSubstring = sendString;
                    serialPort.write(sendString.getBytes());
                    tvAppend(textView, "\nData Sent: " + sendString + "\n");
                }
            }
        });

        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }


    public void setUiEnabled(boolean bool) {
        // Enable/disable buttons if communication has/hasn't been established
        startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);
    }

    public void onClickStart(View view) {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    public void onClickSend(View view) {
        sendString = editText.getText().toString();
        sendSubstring = sendString;
        serialPort.write(sendString.getBytes());
        tvAppend(textView, "\nData Sent: " + sendString + "\n");
    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView,"\nSerial Connection Closed! \n");
    }

    public void onClickClear(View view) {
        textView.setText(" ");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

}
