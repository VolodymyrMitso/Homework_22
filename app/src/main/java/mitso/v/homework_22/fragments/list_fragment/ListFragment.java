package mitso.v.homework_22.fragments.list_fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.database.GetDataTask;
import mitso.v.homework_22.database.SetDataTask;
import mitso.v.homework_22.databinding.FragmentListBinding;
import mitso.v.homework_22.fragments.BaseFragment;
import mitso.v.homework_22.fragments.create_fragment.CreateEditFragment;
import mitso.v.homework_22.fragments.list_fragment.recycler_view.INoteHandler;
import mitso.v.homework_22.fragments.list_fragment.recycler_view.NoteAdapter;
import mitso.v.homework_22.fragments.list_fragment.recycler_view.SpacingDecoration;
import mitso.v.homework_22.models.Note;

public class ListFragment extends BaseFragment implements INoteHandler {

    private String                  LOG_TAG = Constants.LIST_FRAGMENT_LOG_TAG;

    private FragmentListBinding     mBinding;

    private NoteAdapter             mNoteAdapter;
    private List<Note>              mNoteList;

    private Note                    mNote;
    private Note                    mOldNote;

    private DatabaseHelper          mDatabaseHelper;

    private boolean                 isHandlerSet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        final View rootView = mBinding.getRoot();

        initActionBar();

        if (mMainActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

            Log.i(LOG_TAG, "DATABASE EXISTS.");

            getDatabaseData();

        } else {

            Log.e(LOG_TAG, "DATABASE DOESN'T EXIST.");

            mNoteList = new ArrayList<>();
            addNote();

            if (!mNoteList.isEmpty()) {
                Log.i(LOG_TAG, "LIST ISN'T EMPTY.");

                deleteOldNote();
                initRecyclerView();
                setDatabaseData();

            } else
                Log.i(LOG_TAG, "LIST IS EMPTY.");
        }

        initButtons();

        return rootView;
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null)
            mMainActivity.getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_app_name) + "</font>"));
    }

    private void getDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void onSuccess(List<Note> _result) {

                Log.i(getDataTask.LOG_TAG, "ON SUCCESS.");

                mNoteList = _result;
                addNote();

                if (!mNoteList.isEmpty()) {
                    Log.i(LOG_TAG, "LIST ISN'T EMPTY.");

                    deleteOldNote();
                    initRecyclerView();
                    setDatabaseData();

                } else
                    Log.i(LOG_TAG, "LIST IS EMPTY.");

                mDatabaseHelper.close();
                getDataTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(getDataTask.LOG_TAG, "ON FAILURE.");
                Log.e(getDataTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                getDataTask.releaseCallback();
            }
        });
        getDataTask.execute();
    }

    private void setDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final SetDataTask setDataTask = new SetDataTask(mMainActivity, mDatabaseHelper, mNoteList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(setDataTask.LOG_TAG, "ON SUCCESS.");

                mDatabaseHelper.close();
                setDataTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(setDataTask.LOG_TAG, "ON FAILURE.");
                Log.e(setDataTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                setDataTask.releaseCallback();
            }
        });
        setDataTask.execute();
    }

    private void initButtons() {

        mBinding.setClickerBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.finish();
            }
        });

        mBinding.setClickerAdd(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, new CreateEditFragment())
                        .commitAllowingStateLoss();
            }
        });
    }

    private void addNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_OUT_KEY);
            mNoteList.add(0, mNote);

            Log.i(LOG_TAG, "NOTE IS ADDED.");

        } catch (NullPointerException _error) {

            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "NOTE IS NULL.");
        }
    }

    private void deleteOldNote() {

        try {
            mOldNote = (Note) getArguments().getSerializable(Constants.OLD_NOTE_BUNDLE_KEY);
            if (mNoteList.contains(mOldNote))
                mNoteList.remove(mOldNote);

            Log.i(LOG_TAG, "OLD NOTE IS DELETED.");

        } catch (NullPointerException _error) {

            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "OLD NOTE IS NULL.");
        }
    }

    private void initRecyclerView() {

        mNoteAdapter = new NoteAdapter(mNoteList);
        mBinding.rvNotes.setAdapter(mNoteAdapter);
        mBinding.rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        final int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_10dp);
        mBinding.rvNotes.addItemDecoration(new SpacingDecoration(spacingInPixels));

        Log.i(LOG_TAG, "RECYCLER VIEW IS CREATED.");

        setHandler();
    }

    @Override
    public void openNote(Note _note) {

        final CreateEditFragment createEditFragment = new CreateEditFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.NOTE_BUNDLE_IN_KEY, _note);
        createEditFragment.setArguments(bundle);

        Toast.makeText(mMainActivity, String.valueOf(_note.getId()), Toast.LENGTH_SHORT).show();

        mMainActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, createEditFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void selectNote(Note _note) {

        Toast.makeText(mMainActivity, _note.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        setHandler();
    }

    @Override
    public void onPause() {
        super.onPause();

        releaseHandler();
    }

    private void setHandler() {

        try {
            if (!isHandlerSet) {
                mNoteAdapter.setNoteHandler(this);
                isHandlerSet = true;
                Log.i(LOG_TAG, "HANDLER IS SET ON.");
            }
        } catch (Exception _error) {
            Log.e(LOG_TAG, _error.toString());
        }
    }

    private void releaseHandler() {

        try {
            if (isHandlerSet) {
                mNoteAdapter.releaseNoteHandler();
                isHandlerSet = false;
                Log.i(LOG_TAG, "HANDLER IS SET OFF.");
            }
        } catch (Exception _error) {
            Log.e(LOG_TAG, _error.toString());
        }
    }
}