package god.bhagwan.bhajans.mantra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    static String dbName="god.bhagwan.bhajans.mantra";
    static String tableName="allSongs";

    public DatabaseHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    Cursor myRawQuery(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor myCursor=db.rawQuery("select * from "+tableName,null);
        return myCursor;
    }

    boolean insertData(String name,String lyrics, String groupName,  String location, String path, String imageId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("lyrics",lyrics);
        contentValues.put("location",location);
        contentValues.put("groupName",groupName);
        contentValues.put("path",path);
        contentValues.put("imageId",imageId);
        if(db.insert(tableName,null,contentValues)!=-1){
            return true;
        }
        else return false;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+tableName+" (name TEXT,lyrics TEXT,groupName TEXT,location TEXT,path TEXT,imageId TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
