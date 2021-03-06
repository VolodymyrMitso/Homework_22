package mitso.v.homework_22.database.tasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class DeleteSelectedNotesTask extends AsyncTask<Void, Void, Void> {

    public String               LOG_TAG = Constants.DELETE_SELECTED_NOTES_TASK_LOG_TAG;

    public interface Callback{

        void onSuccess();
        void onFailure(Throwable _error);
    }

    private DatabaseHelper      mDatabaseHelper;
    private List<Note>          mNoteList;
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

    public DeleteSelectedNotesTask(Context _context, List<Note> _noteList) {

        this.mDatabaseHelper = DatabaseHelper.getDatabaseHelper(_context);
        this.mNoteList = _noteList;
    }

    @Override
    protected Void doInBackground(Void ... _voids) {

        try {
            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

            for (int i = 0; i < mNoteList.size(); i++) {

                final Note note = mNoteList.get(i);

                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE,
                        DatabaseHelper.COLUMN_NOTE_ID + " = " + note.getId(),
                        null);
            }

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
