package takuseki2001.gmail.com.mywork;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

//コラムの名前、テーブル名以外コピペ
public class MyDatabaseHelper extends SQLiteOpenHelper {


    //データベースのバージョン
    public static final int DATABASE_VERSION = 1;

    //データベースの情報を格納
    public static final String DATABASE_NAME = "MyPassDB.db";
    public static final String TABLE_NAME = "myPasstb";
    public static final String _ID = "_id";
    public static final String COLUMN_NAME_WEB = "web";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_PASS = "pass";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_ID + " TEXT," +
                    COLUMN_NAME_WEB + " TEXT," +
                    COLUMN_NAME_PASS + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + TABLE_NAME;


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブルを作成する
        db.execSQL(
                SQL_CREATE_ENTRIES
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//アップデートの判断、古いバージョンを削除し新規作成
        onUpgrade(db, oldVersion, newVersion);
    }

}
