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
    protected Void doInBackground(Void ... _voids) {

        try {
            if (mContext.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

                mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);

                Log.i(LOG_TAG, "DATABASE IS DELETED.");

                if (!mNoteList.isEmpty()) {

                    final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                    for (int i = 0; i < mNoteList.size(); i++) {

                        final Note note = mNoteList.get(i);
                        final String noteString = new Gson().toJson(note);

                        final ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_NOTES, noteString);

                        db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                    }

                    db.close();

                    Log.i(LOG_TAG, "DATABASE IS REWRITTEN.");
                }

            } else {

                final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                for (int i = 0; i < mNoteList.size(); i++) {

                    final Note note = mNoteList.get(i);
                    final String noteString = new Gson().toJson(note);

                    final ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_NOTES, noteString);

                    db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                }

                db.close();

                Log.i(LOG_TAG, "DATABASE IS CREATED FIRST TIME.");
            }

        } catch (Exception _error) {
            _error.printStackTrace();
            mException = _error;
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
