package com.grandpaj.BikeDash;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.grandpaj.library2.bluetooth.BluetoothChatFragment;
//import com.grandpaj.library2.bluetooth.logger.Log;

import java.util.zip.CRC32;

import DataDisplay.DataText;

/**
 * Created by Linda on 5/16/2016.
 */
public class RemoteManager extends BluetoothChatFragment {

    private Button mConnectButton;
    private TextView mBatteryUsed;
    private DataText vBattery;
    private TextView iBattery;
    private TextView qBRemaining;
    public double   QRecharged = 5.0;
    private FragmentActivity activity;
    /**
    *@param numCRCmismatches total number of CRC mismatches in received strings
     */
    private int numCRCmismatches = 0;



    private static final String TAG = "RemoteManager: ";


    public RemoteManager() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        android.util.Log.d(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_battery,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        android.util.Log.d(TAG,"onViewCreated");
        mConnectButton = (Button) view.findViewById(R.id.button_send);
        mBatteryUsed = (TextView) view.findViewById(R.id.qbatteryused);
        iBattery = (TextView) view.findViewById(R.id.ibattery);
        vBattery = (DataText) view.findViewById(R.id.vbattery);
        qBRemaining = (TextView) view.findViewById(R.id.qbatpctremaining);
        activity = getActivity();
    }

    /**
     * Set up the UI and background operations for chat.
     */
    @Override
    public void setupChat() {
        android.util.Log.d(TAG, "setupChat()");

        // Initialize the connect button with a listener that for click events
        mConnectButton.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  // start connection process
                                                  connectRemote(v);
                                              }
                                          }
        );

        mBatteryUsed.setOnLongClickListener(new View.OnLongClickListener() {
                                              public boolean onLongClick(View v) {
                                                  // start connection process
                                                  resetBatteryUsed(v);
                                                  return true;
                                              }
                                          }
        );

        super.setupChat();
    }


    @Override
    public void processRemoteLine(String inputString){
        Log.d(TAG,"begin processRemoteLine() "+inputString);

        String line=inputString.toLowerCase();
        //split line at "=" if there is one
        String[] lineParts = line.split("=");
        //split line at the : if there is one
        String[] crcParts = inputString.split(":");
        boolean crcMatches = true;

        //check CRC only if there is variable part and a checksum in the string
        //otherwise the CRC will be fixed in the string and doesn't need to be calculated or there is no CRC to check.
        String enc="";
        if ( (lineParts.length > 1) && (crcParts.length > 1) ) {
            // see if the CRC code matches
            CRC32 crc = new CRC32();
            crc.update(crcParts[0].getBytes());
            enc = String.format("%08X", crc.getValue());
            crcMatches = crcParts[1].equalsIgnoreCase(enc);
            if (!crcMatches) numCRCmismatches++;

        }
/*
        if ((null != activity) && (lineParts[0].equalsIgnoreCase("vb")) ) {
            Toast.makeText(activity, "processRemoteLine() "
                    + inputString +";"+crcMatches+";"+ enc, Toast.LENGTH_SHORT).show();
        }
*/

        if ((lineParts.length > 1) && crcMatches) {
            //there was an equal sign in the line and the crc matches or is not present

            //split the last part of the line after the = at : if there is one
            String[] valueParts = lineParts[1].split(":");
            // set the item in lineParts[0] to the value in valueParts[0]
            String tagToBeSet = lineParts[0];
            Double value = Double.valueOf(valueParts[0]);
              String trimmedText = valueParts[0].trim();

            if (tagToBeSet.equalsIgnoreCase("vb")){
                vBattery.setText(trimmedText);
                //set the background colour based on voltage
            }
            else if (tagToBeSet.equalsIgnoreCase("ib")){
                iBattery.setText(trimmedText);
            }
            else if (tagToBeSet.equalsIgnoreCase("qbused")){
                mBatteryUsed.setText(trimmedText);
                Double qRemaining = QRecharged - value;
                Double pctRemaining = qRemaining/QRecharged*100.0;
                qBRemaining.setText(String.format( "%.1f", pctRemaining));
                //set the background colour based on remaining charge.
            }
        }

    }

    public void resetBatteryUsed(View view) {
        String mySnackbarText = "Battery Charge Reset";
        Snackbar mySnackbar = Snackbar.make(view,mySnackbarText,Snackbar.LENGTH_LONG);
        mySnackbar.show();
        //send reset string to remote "qr:C9F64A58"
        sendMessage("rq:C9F64A58");

    }

/*
    //turn on bluetooth and let the user choose a remote device
    public boolean connectToRemote() {
        Log.d(TAG,"begin connectToRemote().");
        return false;
    }
*/
}
