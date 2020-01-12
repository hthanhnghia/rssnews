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

import org.json.JSONArray;
import org.json.JSONObject;

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
        JSONObject rssDataJson = new JSONObject();
        JSONArray mainRssDataJson = new JSONArray();
        JSONArray recommendedRssDataJson = new JSONArray();

        try {
            Uri rss = Uri.parse(URL);
            try {
                ContentProviderClient client = reactContext.getContentResolver().acquireContentProviderClient(rss);
                Cursor c = client.query(rss, null, null, null, null);

                if (null == c) {

                } else {
                    if (c.moveToFirst()) {
                        do {
                            JSONObject feedEntryJson = new JSONObject();
                            feedEntryJson.put("title", c.getString(c.getColumnIndex("title")));
                            feedEntryJson.put("content", c.getString(c.getColumnIndex("content")));
                            feedEntryJson.put("published", c.getString(c.getColumnIndex("published")));
                            feedEntryJson.put("updated", c.getString(c.getColumnIndex("updated")));
                            feedEntryJson.put("author", c.getString(c.getColumnIndex("author")));

                            String feedType = c.getString(c.getColumnIndex("feed_type"));
                            if (feedType.equalsIgnoreCase("main")) {
                                mainRssDataJson.put(feedEntryJson);
                            }
                            else {
                                recommendedRssDataJson.put(feedEntryJson);
                            }
                        } while (c.moveToNext());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            rssDataJson.put("main", mainRssDataJson);
            rssDataJson.put("recommended", recommendedRssDataJson);
            promise.resolve(rssDataJson.toString());
        } catch (IllegalViewOperationException e) {
            promise.reject("E_LAYOUT_ERROR", e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
