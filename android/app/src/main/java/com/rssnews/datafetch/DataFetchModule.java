package com.rssnews.datafetch;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

public class DataFetchModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private String URL = "content://com.rssfetcher.RssContentProvider/rss";

    DataFetchModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "DataFetch";
    }

    @ReactMethod
    public void fetchRssNews(Promise promise) {
        try {
            Uri rss = Uri.parse(URL);
            try {
                ContentProviderClient client = reactContext.getContentResolver().acquireContentProviderClient(rss);
                Cursor c = client.query(rss, null, null, null, null);


                if (null == c) {

                } else {
                    if (c.moveToFirst()) {
                        do {

                        } while (c.moveToNext());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            promise.resolve("");
        } catch (IllegalViewOperationException e) {
            promise.reject("E_LAYOUT_ERROR", e);
        }
    }

}
