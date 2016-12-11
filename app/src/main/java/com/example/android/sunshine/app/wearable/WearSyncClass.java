package com.example.android.sunshine.app.wearable;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sbjr on 12/5/16.
 */

public class WearSyncClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    GoogleApiClient mGoogleApiClient;
    Context context;

    double hTemp;
    double lowTemp;
    int weatherId;

    public WearSyncClass(Context context,double hTemp, double lowTemp,int weatherId) {
        this.context = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        this.hTemp = hTemp;
        this.lowTemp = lowTemp;
        this.weatherId = weatherId;
    }

    public void close(){
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("wear","receivig data");
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/weather");
        putDataMapRequest.getDataMap().putInt("weather",weatherId);
        putDataMapRequest.getDataMap().putString("max",Double.toString(hTemp));
        putDataMapRequest.getDataMap().putString("min",Double.toString(lowTemp));
        putDataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest);
        //Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                Log.d("wear",dataItemResult.getStatus().toString());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("wear","onConnectionSuspended called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("wear","onConnectionFailed called");
    }
}
