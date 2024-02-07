package com.pdascan;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Map;
import java.util.HashMap;
import android.content.IntentFilter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
public class PdaScanModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String XM_SCAN_ACTION = "com.android.server.scannerservice.broadcast";
    private static final String IDATA_SCAN_ACTION = "android.intent.action.SCANRESULT";
    private static final String YBX_SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";
    private static final String PL_SCAN_ACTION = "scan.rcv.message";
    private static final String BARCODE_DATA_ACTION = "com.ehsy.warehouse.action.BARCODE_DATA";
    private static final String HONEYWELL_SCAN_ACTION = "com.honeywell.decode.intent.action.EDIT_DATA";


    private static final String MOBYDATA_SCAN_ACTION = "android.intent.action.SCANRESULT";

    private static final String MOBYDATA_SCAN_EXT_STRING = "com.android.decode.intentwedge.barcode_string";

    private String selectValue(Intent intent){

      if(intent.getStringExtra("value")!=null && !intent.getStringExtra("value").trim().isEmpty()){
        return intent.getStringExtra("value");
      }
      return intent.getStringExtra(MOBYDATA_SCAN_EXT_STRING);

    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        Log.i("PdaScannerPlugin",intent.getAction()+":"+intent.getStringExtra("value")+"/"+intent.getStringExtra(MOBYDATA_SCAN_EXT_STRING));

          try {
            WritableMap params = Arguments.createMap();
            String actionName = intent.getAction();
            if (XM_SCAN_ACTION.equals(actionName)) {
              params.putString("code", intent.getStringExtra("scannerdata"));
            } else if (IDATA_SCAN_ACTION.equals(actionName)) {


              params.putString("code", selectValue(intent));

              // params.putString("code", intent.getStringExtra(XM_SCAN_EXT_STRING));
              // params.putString("type", intent.getStringExtra(XM_SCAN_EXT_TYPE));
              // params.putString("data", new String(intent.getByteArrayExtra(XM_SCAN_EXT_DATA), StandardCharsets.UTF_8));


            } else if (YBX_SCAN_ACTION.equals(actionName)) {
              params.putString("code", intent.getStringExtra("barcode_string"));
            } else if (PL_SCAN_ACTION.equals(actionName)) {
              byte[] barcode = intent.getByteArrayExtra("barocode");
              int barcodelen = intent.getIntExtra("length", 0);
              String result = new String(barcode, 0, barcodelen);
              params.putString("code", result);
            } else if (HONEYWELL_SCAN_ACTION.equals(actionName) || BARCODE_DATA_ACTION.equals(actionName)) {
              params.putString("code", intent.getStringExtra("data"));
            } else {
              Log.i("PdaScannerPlugin", "NoSuchAction");
            }
            sendEvent(getReactApplicationContext(), "onEvent", params);
          } catch (Exception e) {
            WritableMap errorParams = Arguments.createMap();
            errorParams.putString("message", e.getMessage());
            sendEvent(getReactApplicationContext(), "onError", errorParams);
          }
      }
    };

    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
      getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName,
          params);
    }
    public PdaScanModule(ReactApplicationContext reactContext) {
      
        super(reactContext);
        Log.i("PdaScannerPlugin", "PdaScanModule start");
        
        this.reactContext = reactContext;
        IntentFilter xmIntentFilter = new IntentFilter();
        xmIntentFilter.addAction(XM_SCAN_ACTION);
        xmIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, xmIntentFilter);
        IntentFilter iDataIntentFilter = new IntentFilter();
        iDataIntentFilter.addAction(IDATA_SCAN_ACTION);
        iDataIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, iDataIntentFilter);
        IntentFilter yBoXunIntentFilter = new IntentFilter();
        yBoXunIntentFilter.addAction(YBX_SCAN_ACTION);
        yBoXunIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, yBoXunIntentFilter);
        IntentFilter pLIntentFilter = new IntentFilter();
        pLIntentFilter.addAction(PL_SCAN_ACTION);
        pLIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, pLIntentFilter);
        IntentFilter honeyIntentFilter = new IntentFilter();
        honeyIntentFilter.addAction(BARCODE_DATA_ACTION);
        honeyIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, honeyIntentFilter);
        IntentFilter honeywellIntentFilter = new IntentFilter();
        honeywellIntentFilter.addAction(HONEYWELL_SCAN_ACTION);
        honeywellIntentFilter.setPriority(Integer.MAX_VALUE);
        getReactApplicationContext().registerReceiver(scanReceiver, honeywellIntentFilter);
    }

    @Override
    public String getName() {
        return "PdaScan";
    }


}
