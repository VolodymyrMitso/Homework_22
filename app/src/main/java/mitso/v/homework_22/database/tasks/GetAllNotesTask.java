package mitso.v.homework_22.database.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.models.Note;

public class GetAllNotesTask extends AsyncTask<Void, Void, List<Note>> {

    public String       LOG_TAG = Constants.GET_ALL_NOTES_TASK_LOG_TAG;

    public interface Callback{
        void onSuccess(List<Note> _result);
        void onFailure(Throwable _error);
    }

    private DatabaseHelper mDatabaseHelper;
    private List<Note>          mNoteList;
    private Callback            mCallback;
    private Exception           mException;

    public GetAllNotesTask(DatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
        this.mNoteList = new ArrayList<>();
    }

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    @Override
    protected List<Note> doInBackground(Void... _voids) {

        try {

            final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            final String[] projection = {
                    DatabaseHelper.COLUMN_NOTE_ID, DatabaseHelper.COLUMN_NOTE_BODY
            };

            final Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, projection,
                    null, null, null, null, null);

            while (cursor.moveToNext()) {

                final long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_ID));
                final String body = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_BODY));

                final Note note = new Note(id, body);

                mNoteList.add(note);
            }

            cursor.close();
            db.close();

        } catch (Exception _error) {

            _error.printStackTrace();
            mException = _error;
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