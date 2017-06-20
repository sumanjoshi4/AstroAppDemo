package com.app.astro.astroassignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by B0096643 on 6/16/2017.
 * Database helper class it saves channel info in local database
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME     = "AMDB.sqlite";
    private static final int    VERSION     = 1;
    private String              dbPath      = "/databases/";
    private SQLiteDatabase      mDataBase;
    private final Context       mContext;
    private static final String CHANNEL_ID = "CHANNEL_ID" ;
    private static final String CHANNEL_TITLE = "CHANNEL_TITLE" ;
    private static final String CHANNEL_NUMBER = "CHANNEL_NUMBER" ;
    private static final String THUMBNAIL_URL = "THUMBNAIL_URL" ;
    private static final String IS_FAVOURITE = "IS_FAVOURITE" ;
    private static final String TABLE_NAME = "CHANNELINFO" ;

    static String CREATE_CHANNEL_TABLE = "CREATE TABLE IF NOT EXISTS CHANNELINFO (CHANNEL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "CHANNEL_TITLE text,THUMBNAIL_URL text, CHANNEL_NUMBER INTEGER, IS_FAVOURITE INTEGER DEFAULT 0);";

    public DataBaseHelper(Context context){
       super(context, DB_NAME, null, VERSION);
        mContext = context;
        // Use internal directory so that if app is uninstalled db is also
        // uninstalled
        dbPath = mContext.getApplicationInfo().dataDir + dbPath;
        File dbDirectory = new File(dbPath);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdir();
        }
   }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CHANNEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public synchronized void close() {
        if(mDataBase!=null)
            mDataBase.close();
        super.close();
    }

    public boolean dbExists() {
        if (SharedPrefsUtil.doesDbExists()) {
            return true;
        }
        File dbFile = new File(dbPath + DB_NAME);
        boolean exists = dbFile.exists();
        SharedPrefsUtil.setDbExists(exists);
        return exists;
    }

    public boolean openDataBase() {
        String mPath = dbPath + DB_NAME;
        File dbFile = new File(mPath);
        if(dbFile.exists()){
            mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return mDataBase != null;
    }


    public static void insertChannelDataIntoDB(ChannelModel model){
        SQLiteDatabase db = AppController.getDatabase();
        db.execSQL(CREATE_CHANNEL_TABLE);
            ContentValues cv = new ContentValues();
            cv.put(CHANNEL_NUMBER,model.getChannelNumber());
            cv.put(CHANNEL_ID,model.getChannelId());
            cv.put(THUMBNAIL_URL,model.getmThumbUrl());
            cv.put(CHANNEL_TITLE,model.getChannelTitle());
            cv.put(IS_FAVOURITE,1);
            db.insert(TABLE_NAME,null,cv);
        AppController.closeDatabase();

    }

    public static void removeDataRowFromTable(int channelId){
        SQLiteDatabase db = AppController.getDatabase();
        db.delete(TABLE_NAME,CHANNEL_ID+" = ?",new String[]{Integer.toString(channelId)});
    }

    public static ArrayList<ChannelModel> getFavouriteList(){
        ArrayList<ChannelModel> arr = new ArrayList<>();
        try {
            SQLiteDatabase mSQLiteDatabase = AppController.getDatabase();
            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    ChannelModel obj = new ChannelModel();
                    int index = cursor.getColumnIndex(CHANNEL_NUMBER);
                    String str = cursor.getString(index);
                    obj.setChannelNumber(str);
                    index = cursor.getColumnIndex(CHANNEL_TITLE);
                    str = cursor.getString(index);
                    obj.setChannelTitle(str);
                    index = cursor.getColumnIndex(THUMBNAIL_URL);
                    str = cursor.getString(index);
                    obj.setmThumbUrl(str);
                    index = cursor.getColumnIndex(CHANNEL_ID);
                    int channeId = cursor.getInt(index);
                    obj.setChannelId(channeId);
                    obj.setFavourite(true);
                    arr.add(obj);

                } while (cursor.moveToNext());

            }
        }catch (Exception e){
            AppController.closeDatabase();
            return arr;
        }
        AppController.closeDatabase();
        return arr;

    }

    public static void deleteTable(){
        SQLiteDatabase mSQLiteDatabase = AppController.getDatabase();
        mSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

}
