package mitso.v.homework_22.database.tasks;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class DeleteSelectedNotesTask extends AsyncTask<Void, Void, Void> {

    public String       LOG_TAG = Constants.DELETE_OLD_NOTE_TASK_LOG_TAG;

    public interface Callback{
        void onSuccess();
        void onFailure(Throwable _error);
    }

    private DatabaseHelper      mDatabaseHelper;
    private List<Note>          mNoteList;
    private Callback            mCallback;
    private Exception           mException;

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    public DeleteSelectedNotesTask(DatabaseHelper _databaseHelper, List<Note> _noteList) {
        this.mDatabaseHelper = _databaseHelper;
        this.mNoteList = _noteList;
    }

    @Override
    protected Void doInBackground(Void... _voids) {

        try {
            final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            for (int i = 0; i < mNoteList.size(); i++) {

                final Note note = mNoteList.get(i);

                db.delete(DatabaseHelper.DATABASE_TABLE,
                        DatabaseHelper.COLUMN_NOTE_ID + " = " + note.getId(),
                        null);
            }

            db.close();

            Log.i(LOG_TAG, "SELECTED NOTES ARE DELETED FROM DATABASE.");

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
