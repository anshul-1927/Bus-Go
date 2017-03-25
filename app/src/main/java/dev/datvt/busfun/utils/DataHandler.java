package dev.datvt.busfun.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by datvt on 5/2/2016.
 */
public class DataHandler extends SQLiteOpenHelper {

    private static final String DATABASE_PATH = "data/data/dev.datvt.busfun/";
    private static final String DATABASE_NAME = "db_bus.sqlite";
    private static final int SCHEMA_VERSION = 1;

    private Context context;
    private SQLiteDatabase myDatabase;

    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        this.context = context;
        boolean dbexist = checkdatabase();

        if (dbexist) {
        } else {
            Log.i("ERROR", "Database doesn't exist!");
            createDatabse();
        }
    }

    public DataHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void createDatabse() {
        this.getReadableDatabase();
        copyDatabase();
    }

    public SQLiteDatabase getMyDatabase() {
        return myDatabase;
    }

    public void open() {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        myDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor selectSQL(String sql) {
        return myDatabase.rawQuery(sql, null);
    }

    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Databse doesn't exist!");
        }
        return checkdb;
    }

    private void copyDatabase() {
        try {
            AssetManager dirPath = context.getAssets();
            InputStream myinput = context.getAssets().open(DATABASE_NAME);
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myinput.close();
        } catch (IOException e) {
            Log.e("ERROR", "Không mở được database");
        }
    }
}
