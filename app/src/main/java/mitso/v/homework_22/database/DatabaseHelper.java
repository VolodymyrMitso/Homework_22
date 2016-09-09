package mitso.v.homework_22.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String      DATABASE_NAME = "notes_database.db";
    public static final int         DATABASE_VERSION = 1;
    public static final String      DATABASE_TABLE = "notes_table";

    public static final String      COLUMN_DATABASE_ID = "_id";
    public static final String      COLUMN_NOTE_ID = "note_id";
    public static final String      COLUMN_NOTE_BODY = "note_body";

    public static final String      CREATE_TABLE = "create table";
    public static final String      INTEGER_PRIMARY_KEY = "integer primary key autoincrement";
    public static final String      INTEGER = "integer";
    public static final String      TEXT = "text not null";

    public static final String      COMMA = ",";
    public static final String      SPACE = " ";
    public static final String      PARENTHESES_IN = "(";
    public static final String      PARENTHESES_OUT = ")";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static DatabaseHelper instance;

    private DatabaseHelper(Context _context) {

        super(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getDatabaseHelper(Context _context) {

        if (instance == null)
            instance = new DatabaseHelper(_context);

        return instance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(SQLiteDatabase _db) {

        _db.execSQL(
                CREATE_TABLE + SPACE + DATABASE_TABLE + SPACE +
                PARENTHESES_IN +
                COLUMN_DATABASE_ID + SPACE + INTEGER_PRIMARY_KEY + COMMA + SPACE +
                COLUMN_NOTE_ID + SPACE + INTEGER + COMMA + SPACE +
                COLUMN_NOTE_BODY + SPACE + TEXT +
                PARENTHESES_OUT
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {

    }
}