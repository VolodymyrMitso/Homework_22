package mitso.v.homework_22.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.models.Note;

public class SetDataTask extends AsyncTask<Void, Void, Void> {

    public String           LOG_TAG = Constants.SET_DATA_TASK_LOG_TAG;

    public interface Callback{
        void onSuccess();
        void onFailure(Throwable _error);
    }

    private Context         mContext;
    private DatabaseHelper  mDatabaseHelper;
    private List<Note>      mNoteList;
    private Callback        mCallback;
    private Exception       mException;

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    public SetDataTask(Context _context, DatabaseHelper _databaseHelper, List<Note> _noteList) {
        this.mContext = _context;
        this.mDatabaseHelper = _databaseHelper;
        this.mNoteList = _noteList;
    }

    @Override
    protected Void doInBackground(Void... _voids) {

        try {
            if (mContext.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

                for (int i = 0; i < mNoteList.size(); i++) {

                    Note note = mNoteList.get(i);
                    String apiBankString = new Gson().toJson(note);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_NOTES, apiBankString);

                    db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                }

                db.close();

                Log.i(LOG_TAG, "DATABASE IS REWRITTEN.");

            } else {

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                for (int i = 0; i < mNoteList.size(); i++) {

                    Note note = mNoteList.get(i);
                    String apiBankString = new Gson().toJson(note);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_NOTES, apiBankString);

                    db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                }

                db.close();

                Log.i(LOG_TAG, "DATABASE IS CREATED FIRST TIME.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void _aVoid) {
        super.onPostExecute(_aVoid);

        if (mCallback != null) {
            if (mException == null)
                mCallback.onSuccess();
            else
                mCallback.onFailure(mException);
        }
    }
}
