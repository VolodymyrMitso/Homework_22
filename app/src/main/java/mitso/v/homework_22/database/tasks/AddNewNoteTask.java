package mitso.v.homework_22.database.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class AddNewNoteTask extends AsyncTask<Void, Void, Void> {

    public String               LOG_TAG = Constants.ADD_NEW_NOTE_TASK_LOG_TAG;

    public interface Callback{

        void onSuccess();
        void onFailure(Throwable _error);
    }

    private DatabaseHelper      mDatabaseHelper;
    private Note                mNote;
    private Callback            mCallback;
    private Exception           mException;
    private SQLiteDatabase      mSQLiteDatabase;

    public void setCallback(Callback _callback) {

        if (mCallback == null)
            mCallback = _callback;
    }

    public void releaseCallback() {

        if (mCallback != null)
            mCallback = null;
    }

    public AddNewNoteTask(Context _context, Note _note) {

        this.mDatabaseHelper = DatabaseHelper.getDatabaseHelper(_context);
        this.mNote = _note;
    }

    @Override
    protected Void doInBackground(Void ... _voids) {

        try {
            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

            final ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOTE_ID, mNote.getId());
            values.put(DatabaseHelper.COLUMN_NOTE_BODY, mNote.getBody());

            mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE, null, values);

        } catch (Exception _error) {

            _error.printStackTrace();
            mException = _error;

        } finally {

            if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen())
                mSQLiteDatabase.close();

            if (mDatabaseHelper != null)
                mDatabaseHelper.close();
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
