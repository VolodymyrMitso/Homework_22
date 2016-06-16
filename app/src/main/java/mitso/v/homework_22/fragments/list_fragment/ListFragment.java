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
import java.util.Collections;
import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.database.tasks.AddNewNoteTask;
import mitso.v.homework_22.database.tasks.DeleteOldNoteTask;
import mitso.v.homework_22.database.tasks.DeleteSelectedNotesTask;
import mitso.v.homework_22.database.tasks.GetAllNotesTask;
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
    private boolean                 isRecyclerViewCreated;

    private Note                    mNewNote;
    private Note                    mOldNote;
    private boolean                 isNewNoteNotNull;
    private boolean                 isOldNoteNotNull;

    private DatabaseHelper          mDatabaseHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        final View rootView = mBinding.getRoot();

        initActionBar();

        if (mMainActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

            Log.i(LOG_TAG, "DATABASE EXISTS.");

            getAllNotesFromDatabase();

        } else {

            Log.i(LOG_TAG, "DATABASE DOESN'T EXIST.");

            mNoteList = new ArrayList<>();
            addNoteToList();
            if (isNewNoteNotNull)
                addNoteToDatabase();
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

    private void getAllNotesFromDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final GetAllNotesTask getAllNotesTask = new GetAllNotesTask(mDatabaseHelper);
        getAllNotesTask.setCallback(new GetAllNotesTask.Callback() {
            @Override
            public void onSuccess(List<Note> _result) {

                Log.i(getAllNotesTask.LOG_TAG, "ON SUCCESS.");

                mNoteList = _result;

                addNoteToList();
                if (isNewNoteNotNull)
                    addNoteToDatabase();
                else
                    initRecyclerView();

                mDatabaseHelper.close();
                getAllNotesTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(getAllNotesTask.LOG_TAG, "ON FAILURE.");
                Log.e(getAllNotesTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                getAllNotesTask.releaseCallback();
            }
        });
        getAllNotesTask.execute();
    }

    private void addNoteToDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final AddNewNoteTask addNewNoteTask = new AddNewNoteTask(mDatabaseHelper, mNewNote);
        addNewNoteTask.setCallback(new AddNewNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(addNewNoteTask.LOG_TAG, "ON SUCCESS.");

                deleteNoteFromList();
                if (isOldNoteNotNull)
                    deleteNoteFromDatabase();
                else
                    initRecyclerView();

                mDatabaseHelper.close();
                addNewNoteTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(addNewNoteTask.LOG_TAG, "ON FAILURE.");
                Log.e(addNewNoteTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                addNewNoteTask.releaseCallback();
            }
        });
        addNewNoteTask.execute();
    }

    private void deleteNoteFromDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final DeleteOldNoteTask deleteOldNoteTask = new DeleteOldNoteTask(mDatabaseHelper, mOldNote);
        deleteOldNoteTask.setCallback(new DeleteOldNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(deleteOldNoteTask.LOG_TAG, "ON SUCCESS.");

                initRecyclerView();

                mDatabaseHelper.close();
                deleteOldNoteTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(deleteOldNoteTask.LOG_TAG, "ON FAILURE.");
                Log.e(deleteOldNoteTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                deleteOldNoteTask.releaseCallback();
            }
        });
        deleteOldNoteTask.execute();
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

    private void addNoteToList() {

        try {
            mNewNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_OUT_KEY);
            if (mNewNote == null)
                throw new NullPointerException();

            mNoteList.add(mNewNote);
            isNewNoteNotNull = true;
            Log.i(LOG_TAG, "NEW NOTE IS ADDED TO LIST.");

        } catch (NullPointerException _error) {

            isNewNoteNotNull = false;
            Log.i(LOG_TAG, "NEW NOTE IS NOT ADDED TO LIST. NEW NOTE IS NULL.");
        }
    }

    private void deleteNoteFromList() {

        try {
            mOldNote = (Note) getArguments().getSerializable(Constants.OLD_NOTE_BUNDLE_KEY);
            if (mOldNote == null)
                throw new NullPointerException();

            if (mNoteList.contains(mOldNote)) {
                mNoteList.remove(mOldNote);
                isOldNoteNotNull = true;
                Log.i(LOG_TAG, "OLD NOTE IS DELETED FROM LIST.");
            }

        } catch (NullPointerException _error) {

            isOldNoteNotNull = false;
            Log.i(LOG_TAG, "OLD NOTE IS NOT DELETED FROM LIST. OLD NOTE IS NULL.");
        }
    }

    private void initRecyclerView() {

        Collections.sort(mNoteList);

        mNoteAdapter = new NoteAdapter(mMainActivity, mNoteList);
        final int spacingInPixels = mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_10dp);

        mBinding.rvNotes.setAdapter(mNoteAdapter);
        mBinding.rvNotes.setLayoutManager(new GridLayoutManager(mMainActivity, 1));
        mBinding.rvNotes.addItemDecoration(new SpacingDecoration(spacingInPixels));

        isRecyclerViewCreated = true;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionMode              mActionMode;
    private ActionMode.Callback     mActionModeCallBack;

    private boolean                 isAllItemsSelected;
    private Menu                    mSelectedMenu;

    private List<Note>              mSelectedNotes;
    private List<Note>              mTempSelectedNotes;

    private void deleteSelectedNotesFromDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final DeleteSelectedNotesTask deleteSelectedNotesTask = new DeleteSelectedNotesTask(mDatabaseHelper, mSelectedNotes);
        deleteSelectedNotesTask.setCallback(new DeleteSelectedNotesTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(deleteSelectedNotesTask.LOG_TAG, "ON SUCCESS.");

                mNoteAdapter.removeNotes(mNoteAdapter.getSelectedItems());
                mActionMode.finish();

                mDatabaseHelper.close();
                deleteSelectedNotesTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(deleteSelectedNotesTask.LOG_TAG, "ON FAILURE.");
                Log.e(deleteSelectedNotesTask.LOG_TAG, _error.toString());

                mDatabaseHelper.close();
                deleteSelectedNotesTask.releaseCallback();
            }
        });
        deleteSelectedNotesTask.execute();
    }

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

        if (mSelectedNotes.contains(mNoteList.get(_position)))
            mSelectedNotes.remove(mNoteList.get(_position));
        else
            mSelectedNotes.add(mNoteList.get(_position));

        mNoteAdapter.toggleSelection(_position);
        final int count = mNoteAdapter.getSelectedItemCount();

        if (count == 0)
            mActionMode.finish();
        else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private void initActionMode() {

        mBinding.floatingActionButton.hide();

        mSelectedNotes = new ArrayList<>();
        mTempSelectedNotes = new ArrayList<>();

        mActionModeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode _mode, Menu _menu) {
                _mode.getMenuInflater().inflate(R.menu.menu_selected, _menu);
                mSelectedMenu = _menu;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode _mode, Menu _menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode _mode, MenuItem _item) {
                switch (_item.getItemId()) {
                    case R.id.mi_remove:

                        deleteSelectedNotesFromDatabase();

                        return true;
                    case R.id.mi_share:

                        final StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < mNoteAdapter.getSelectedItems().size(); i++) {

                            final int index = mNoteAdapter.getSelectedItems().get(i);

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
                            Toast.makeText(mMainActivity, mMainActivity.getResources().getString(R.string.s_no_program), Toast.LENGTH_LONG).show();

                        _mode.finish();

                        return true;
                    case R.id.mi_select_all:

                        if (!isAllItemsSelected) {

                            mNoteAdapter.selectAllItems(mNoteList);

                            mTempSelectedNotes = mSelectedNotes;
                            mSelectedNotes = new ArrayList<>(mNoteList);

                            mActionMode.setTitle(String.valueOf(mNoteAdapter.getSelectedItemCount()));
                            mActionMode.invalidate();
                            mSelectedMenu.getItem(0).setIcon(mMainActivity.getResources().getDrawable(R.drawable.ic_select_all_filled));
                            isAllItemsSelected = true;

                        } else {

                            mNoteAdapter.deselectAllItems();

                            mSelectedNotes = mTempSelectedNotes;

                            mActionMode.setTitle(String.valueOf(mNoteAdapter.getSelectedItemCount()));
                            mActionMode.invalidate();
                            mSelectedMenu.getItem(0).setIcon(mMainActivity.getResources().getDrawable(R.drawable.ic_select_all_empty));
                            isAllItemsSelected = false;
                        }

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode _mode) {
                mSelectedNotes.clear();
                mTempSelectedNotes.clear();
                mNoteAdapter.clearSelection();
                mActionMode = null;
                mBinding.floatingActionButton.show();
            }
        };

        mActionMode = mMainActivity.startSupportActionMode(mActionModeCallBack);
    }
}