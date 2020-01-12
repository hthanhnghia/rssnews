package com.rssfetcher;

import android.os.AsyncTask;

import com.rssfetcher.models.RssFeedAuthor;
import com.rssfetcher.models.RssFeedEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class RssFeedHelper extends AsyncTask<String, Void, List<RssFeedEntry>> {
    private List<RssFeedEntry> feedEntries = new ArrayList<>();
    private RssFeedEntry feedEntry;
    private RssFeedAuthor feedAuthor;
    private String text;
    public RssContentProvider delegate;

    private List<String> rssFeedModelTags = Arrays.asList("entry", "author");
    private Stack<String> modelTagStack = new Stack<>();

    public RssFeedHelper(RssContentProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<RssFeedEntry> doInBackground(String... strings) {
        try {
            String rssUrl = strings[0];
            String feedType = strings[1];
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
                        if (rssFeedModelTags.contains(tagName)) {
                            if (tagName.equalsIgnoreCase("entry")) {
                                // create a new instance of feed entry
                                feedEntry = new RssFeedEntry();
                            } else if (tagName.equalsIgnoreCase("author")) {
                                // create a new instance of feed author
                                feedAuthor = new RssFeedAuthor();
                            }
                            modelTagStack.push(tagName);
                        }

                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("entry")) {
                            feedEntries.add(feedEntry);
                            delegate.insertRssFeedEntryToDB(feedEntry, feedType);
                            modelTagStack.pop();
                        } else if (tagName.equalsIgnoreCase("author")) {
                            feedEntry.setAuthor(feedAuthor);
                            modelTagStack.pop();
                        }

                        else {
                            if (modelTagStack.size() > 0) {
                                String currentModelTag = modelTagStack.peek();

                                if (currentModelTag.equalsIgnoreCase("entry") && feedEntry != null) {
                                    feedEntry.setAttributeValue(tagName, text);
                                } else if (currentModelTag.equalsIgnoreCase("author") && feedEntry != null) {
                                    feedAuthor.setAttributeValue(tagName, text);
                                }
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
