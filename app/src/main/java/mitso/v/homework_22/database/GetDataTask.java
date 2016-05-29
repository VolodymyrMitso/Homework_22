package mitso.v.homework_22.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.models.Note;

public class GetDataTask extends AsyncTask<Void, Void, List<Note>> {

    public String           LOG_TAG = Constants.GET_DATA_TASK_LOG_TAG;

    public interface Callback{
        void onSuccess(List<Note> _result);
        void onFailure(Throwable _error);
    }

    private DatabaseHelper  mDatabaseHelper;
    private List<Note>      mNoteList;
    private Callback        mCallback;
    private Exception       mException;

    public GetDataTask(DatabaseHelper mDatabaseHelper) {
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

            SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            String[] projection = {
                    DatabaseHelper.COLUMN_NOTES
            };

            Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, projection,
                    null, null, null, null, null);

            while (cursor.moveToNext()) {

                String bankString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES));

                Note note = new Gson().fromJson(bankString, Note.class);

                mNoteList.add(note);
            }

            cursor.close();
            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            mException = e;
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