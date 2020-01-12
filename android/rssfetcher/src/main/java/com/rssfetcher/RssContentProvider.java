package com.rssfetcher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rssfetcher.models.RssFeedEntry;

import java.util.HashMap;

public class RssContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.rssfetcher.RssContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/rss";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String MAIN_ARTICLE_URL = "https://www.polygon.com/rss/index.xml";
    static final String RECOMMENDED_ARTICLE_URL = "https://storage.googleapis.com/singtel-test/polygon.xml";

    private static HashMap<String, String> RSS_PROJECTION_MAP;

    static final int RSS_ALL = 1;
    static final int RSS_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "rss", RSS_ALL);
        uriMatcher.addURI(PROVIDER_NAME, "rss/#", RSS_ID);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "News";
    static final String RSS_TABLE_NAME = "rss";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + RSS_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " feed_type TEXT NOT NULL, " +
                    " title TEXT NOT NULL, " +
                    " content TEXT NOT NULL, " +
                    " author TEXT NOT NULL, " +
                    " hash TEXT NOT NULL, " +
                    " published TEXT NOT NULL, " +
                    " updated TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  RSS_TABLE_NAME);
            onCreate(db);
        }
    }

    public void insertRssFeedEntryToDB(RssFeedEntry rssFeedEntry, String feedType) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(RSS_TABLE_NAME);
        qb.appendWhere(  "hash = ");
        qb.appendWhereEscapeString(rssFeedEntry.getHash());
        Cursor c = qb.query(db,	null,	null,
                null,null, null, null);
        /**
         * Only insert if the feed with the same hash not found in the database
         */
        if (c.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("feed_type", feedType);
            values.put("title", rssFeedEntry.getTitle());
            values.put("content", rssFeedEntry.getContent());
            values.put("author", rssFeedEntry.getAuthor().getName());
            values.put("hash", rssFeedEntry.getHash());
            values.put("published", rssFeedEntry.getPublished());
            values.put("updated", rssFeedEntry.getUpdated());
            db.insert(RSS_TABLE_NAME, null, values);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();

        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(RSS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case RSS_ALL:
                if (db != null) {
                    new RssFeedHelper(this).execute(MAIN_ARTICLE_URL, "main");
                    new RssFeedHelper(this).execute(RECOMMENDED_ARTICLE_URL, "recommended");
                }
                qb.setProjectionMap(RSS_PROJECTION_MAP);
                break;

            case RSS_ID:
                qb.appendWhere(  "id=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all rss records
             */
            case RSS_ALL:
                return "vnd.android.cursor.dir/vnd.rss";
            /**
             * Get a particular rss record
             */
            case RSS_ID:
                return "vnd.android.cursor.item/vnd.rss";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}