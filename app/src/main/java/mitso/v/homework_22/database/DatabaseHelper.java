package mitso.v.homework_22.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String      DATABASE_NAME = "notes_database";
    public static final int         DATABASE_VERSION = 1;

    public static final String      DATABASE_TABLE = "notes_table";

    public static final String      KEY_ID  = "_id";
    public static final String      COLUMN_ID = "note_id";
    public static final String      COLUMN_BODY = "note_body";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase _sqLiteDatabase) {
        _sqLiteDatabase.execSQL("create table " + DATABASE_TABLE + " " +
                "(" +
                KEY_ID + " integer primary key autoincrement, " +
                COLUMN_ID + " integer, " +
                COLUMN_BODY + " text not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}