package com.rssfetcher;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssFeedHelper extends AsyncTask<String, Void, List<RssFeedEntry>> {
    private List<RssFeedEntry> feedEntries = new ArrayList<>();
    private RssFeedEntry feedEntry;
    private String text;
    public RssContentProvider delegate;

    @Override
    protected List<RssFeedEntry> doInBackground(String... strings) {
        try {
            String rssUrl = strings[0];
            URL url = new URL(rssUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("entry")) {
                            // create a new instance of employee
                            feedEntry = new RssFeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("entry")) {
                            feedEntries.add(feedEntry);
                            delegate.insertRssFeedEntryToDB(feedEntry);
                        }
                        else {
                            if(feedEntry != null) {
                                feedEntry.setAttributeValue(tagName, text);
                            }
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        return feedEntries;
    }
}
