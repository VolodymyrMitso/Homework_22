package mitso.v.homework_22.database.tasks;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

    public String       LOG_TAG = Constants.DELETE_NOTE_TASK_LOG_TAG;

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

    public DeleteNoteTask(DatabaseHelper _databaseHelper, Note _note) {
        this.mDatabaseHelper = _databaseHelper;
        this.mNote = _note;
    }

    @Override
    protected Void doInBackground(Void... _voids) {

        try {
            final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            db.delete(DatabaseHelper.DATABASE_TABLE,
                    DatabaseHelper.COLUMN_NOTE_ID + " = " + mNote.getId(),
                    null);

            db.close();

            Log.i(LOG_TAG, "OLD NOTE IS DELETED FROM DATABASE.");

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
