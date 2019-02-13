package com.example.administrator.phoneinfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import static com.example.administrator.phoneinfo.CreateCellInfoDBActivity.TABLENAME1;

/**
 * Created by Administrator on 2017/2/15.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {



    private Context mContext;
    private String mTableName;

    public MyDatabaseHelper(Context context, String dataBaseName1,String tablename, SQLiteDatabase.CursorFactory factory, int version){

        super(context,dataBaseName1,factory,version);
        mContext = context;
        mTableName=tablename;
    }
    public void onCreate(SQLiteDatabase db){
        System.out.println("ZGQ:database is onCreate");
        //Toast.makeText(mContext,"ZGQ:Create succeeded",Toast.LENGTH_LONG).show();

    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists "+mTableName);
        System.out.println("ZGQ:database is onUpgrade");
        //Toast.makeText(mContext,"ZGQ:Create succeeded",Toast.LENGTH_LONG).show();
        onCreate(db);
    }

}

