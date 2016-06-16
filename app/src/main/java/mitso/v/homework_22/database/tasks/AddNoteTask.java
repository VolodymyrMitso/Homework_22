package mitso.v.homework_22.database.tasks;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class AddNoteTask extends AsyncTask<Void, Void, Void> {

    public String       LOG_TAG = Constants.ADD_NOTE_TASK_LOG_TAG;

    public interface Callback{
        void onSuccess();
        void onFailure(Throwable _error);
    }

    private DatabaseHelper      mDatabaseHelper;
    private Note                mNote;
    private Callback            mCallback;
    private Exception           mException;

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    public AddNoteTask(DatabaseHelper _databaseHelper, Note _note) {
        this.mDatabaseHelper = _databaseHelper;
        this.mNote = _note;
    }

    @Override
    protected Void doInBackground(Void... _voids) {

        try {
            final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            final ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOTE_ID, mNote.getId());
            values.put(DatabaseHelper.COLUMN_NOTE_BODY, mNote.getBody());

            db.insert(DatabaseHelper.DATABASE_TABLE, null, values);

            db.close();

            Log.i(LOG_TAG, "NEW NOTE IS ADDED TO DATABASE.");

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
