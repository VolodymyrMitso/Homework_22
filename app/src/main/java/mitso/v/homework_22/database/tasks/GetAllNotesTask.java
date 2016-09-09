package mitso.v.homework_22.database.tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class GetAllNotesTask extends AsyncTask<Void, Void, List<Note>> {

    public String               LOG_TAG = Constants.GET_ALL_NOTES_TASK_LOG_TAG;

    public interface Callback{

        void onSuccess(List<Note> _result);
        void onFailure(Throwable _error);
    }

    private DatabaseHelper      mDatabaseHelper;
    private List<Note>          mNoteList;
    private Callback            mCallback;
    private Exception           mException;
    private SQLiteDatabase      mSQLiteDatabase;
    private Cursor              mCursor;

    public GetAllNotesTask(Context _context) {

        this.mDatabaseHelper = DatabaseHelper.getDatabaseHelper(_context);
        this.mNoteList = new ArrayList<>();
    }

    public void setCallback(Callback _callback) {

        if (mCallback == null)
            mCallback = _callback;
    }

    public void releaseCallback() {

        if (mCallback != null)
            mCallback = null;
    }

    @Override
    protected List<Note> doInBackground(Void ... _voids) {

        try {

            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

            final String[] projection = {
                    DatabaseHelper.COLUMN_NOTE_ID, DatabaseHelper.COLUMN_NOTE_BODY
            };

            mCursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE, projection,
                    null, null, null, null, null);

            while (mCursor.moveToNext()) {

                final long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_ID));
                final String body = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_BODY));

                final Note note = new Note(id, body);

                mNoteList.add(note);
            }

        } catch (Exception _error) {

            _error.printStackTrace();
            mException = _error;

        } finally {

            if (mCursor != null && !mCursor.isClosed())
                mCursor.close();

            if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen())
                mSQLiteDatabase.close();

            if (mDatabaseHelper != null)
                mDatabaseHelper.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Note> _noteList) {
        super.onPostExecute(_noteList);

        if (mCallback != null) {

            if (mException == null)
                mCallback.onSuccess(mNoteList);
            else
                mCallback.onFailure(mException);
        }
    }
}