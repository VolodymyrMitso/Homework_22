package mitso.v.homework_22.fragments.list_fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

            Log.i(LOG_TAG, "DATABASE DOESN'T EXIST.");

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

        if (mMainActivity.getSupportActionBar() != null) {

            mMainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mMainActivity.getSupportActionBar().setHomeButtonEnabled(false);
            mMainActivity.getSupportActionBar().setDisplayShowHomeEnabled(false);

            mMainActivity.getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_app_name) + "</font>"));
        }
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

        mBinding.setClickerAdd(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mActionMode != null)
                    mActionMode.finish();

                releaseHandler();

                mMainActivity.commitFragment(new CreateEditFragment());
            }
        });
    }

    private void addNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_OUT_KEY);
            if (mNote == null)
                throw new NullPointerException();

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
            if (mOldNote == null)
                throw new NullPointerException();

            if (mNoteList.contains(mOldNote)) {
                mNoteList.remove(mOldNote);
                Log.i(LOG_TAG, "OLD NOTE IS DELETED.");
            }

        } catch (NullPointerException _error) {

            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "OLD NOTE IS NULL.");
        }
    }

    private void initRecyclerView() {

        mNoteAdapter = new NoteAdapter(mMainActivity, mNoteList);
        final int spacingInPixels = mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_10dp);

        mBinding.rvNotes.setAdapter(mNoteAdapter);
        mBinding.rvNotes.setLayoutManager(new GridLayoutManager(mMainActivity, 1));
        mBinding.rvNotes.addItemDecoration(new SpacingDecoration(spacingInPixels));

        Log.i(LOG_TAG, "RECYCLER VIEW IS CREATED.");

        setHandler();
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
            if (mNoteAdapter != null)
                mNoteAdapter.setNoteHandler(this);
        } catch (Exception _error) {
            Log.e(LOG_TAG, _error.toString());
        }
    }

    private void releaseHandler() {
        try {
            if (mNoteAdapter != null)
                mNoteAdapter.releaseNoteHandler();
        } catch (Exception _error) {
            Log.e(LOG_TAG, _error.toString());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private ActionMode              mActionMode;
    private ActionMode.Callback     mActionModeCallBack;

    @Override
    public void onClick(Note _note, int _position) {

        if (mActionMode != null)

            toggleSelection(_position);

        else {

            releaseHandler();

            final CreateEditFragment createEditFragment = new CreateEditFragment();
            final Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.NOTE_BUNDLE_IN_KEY, _note);
            createEditFragment.setArguments(bundle);

            mMainActivity.commitFragment(createEditFragment);
        }
    }

    @Override
    public void onLongClick(Note _note, int _position) {

        if (mActionMode == null)
            initActionMode();

        toggleSelection(_position);
    }

    private void toggleSelection(int _position) {

        mNoteAdapter.toggleSelection(_position);
        int count = mNoteAdapter.getSelectedItemCount();

        if (count == 0)
            mActionMode.finish();
        else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private void initActionMode() {

        mActionModeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_selected, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mi_remove_sm:

                        mNoteAdapter.removeNotes(mNoteAdapter.getSelectedItems());
                        /** !!! */
                        mNoteList = mNoteAdapter.getNoteList();
                        setDatabaseData();
                        mode.finish();

                        return true;
                    case R.id.mi_share_sm:

                        final StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < mNoteAdapter.getSelectedItems().size(); i++) {

                            int index = mNoteAdapter.getSelectedItems().get(i);

                            stringBuilder.append(mNoteList.get(index).getFormattedDate());
                            stringBuilder.append(" - ");
                            stringBuilder.append(mNoteList.get(index).getFormattedTime());
                            stringBuilder.append("\n");
                            stringBuilder.append(mNoteList.get(index).getBody());

                            if (i != mNoteAdapter.getSelectedItems().size() - 1)
                                stringBuilder.append("\n\n**********\n\n");
                        }

                        final Intent shareNoteIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareNoteIntent.setType("text/plain");
                        shareNoteIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMainActivity.getResources().getString(R.string.s_share_notes));
                        shareNoteIntent.putExtra(android.content.Intent.EXTRA_TEXT, stringBuilder.toString());

                        if (shareNoteIntent.resolveActivity(mMainActivity.getPackageManager()) != null)
                            startActivity(Intent.createChooser(shareNoteIntent, mMainActivity.getResources().getString(R.string.s_share_notes)));
                        else
                            Toast.makeText(mMainActivity, mMainActivity.getResources().getString(R.string.s_no_program), Toast.LENGTH_SHORT).show();

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mNoteAdapter.clearSelection();
                mActionMode = null;
            }
        };

        mActionMode = mMainActivity.startSupportActionMode(mActionModeCallBack);
    }
}