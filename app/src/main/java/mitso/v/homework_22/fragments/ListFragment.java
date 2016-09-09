package mitso.v.homework_22.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.animator.FlipInBottomXAnimator;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.database.DatabaseHelper;
import mitso.v.homework_22.database.tasks.AddNewNoteTask;
import mitso.v.homework_22.database.tasks.DeleteOldNoteTask;
import mitso.v.homework_22.database.tasks.DeleteSelectedNotesTask;
import mitso.v.homework_22.database.tasks.GetAllNotesTask;
import mitso.v.homework_22.databinding.FragmentListBinding;
import mitso.v.homework_22.models.Note;
import mitso.v.homework_22.recycler_view.INoteHandler;
import mitso.v.homework_22.recycler_view.NoteAdapter;
import mitso.v.homework_22.support.Support;

public class ListFragment extends BaseFragment implements INoteHandler {

    private String                  LOG_TAG = Constants.LIST_FRAGMENT_LOG_TAG;

    private Support                 mSupport;

    private FragmentListBinding     mBinding;

    private NoteAdapter             mNoteAdapter;
    private List<Note>              mNoteList;

    private Note                    mNewNote;
    private Note                    mOldNote;
    private boolean                 isNewNoteNull;
    private boolean                 isOldNoteNull;

    private Parcelable              mState;
    private boolean                 isRecyclerViewReady;

    private CreateEditFragment      mCreateEditFragment;
    private Bundle                  mBundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {

        mBinding = DataBindingUtil.inflate(_inflater, R.layout.fragment_list, _container, false);
        final View rootView = mBinding.getRoot();

        mSupport = new Support();

        initActionBar();
        setHasOptionsMenu(true);

        if (mMainActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

            Log.i(LOG_TAG, "DATABASE EXISTS.");

            getAllNotesFromDatabase();

        } else {

            Log.i(LOG_TAG, "DATABASE DOESN'T EXIST.");

            mNoteList = new ArrayList<>();

            receiveNewNote();
            receiveOldNote();

            if (!isNewNoteNull && isOldNoteNull)        // new + null
                addNewNoteToDatabase();
            else                                        // null + null
                initRecyclerView();
        }

        initButtons();
        initFragmentAndBundle();

        return rootView;
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null) {

            mMainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mMainActivity.getSupportActionBar().setHomeButtonEnabled(false);
            mMainActivity.getSupportActionBar().setDisplayShowHomeEnabled(false);

            mMainActivity.getSupportActionBar().setTitle(mMainActivity.getResources().getString(R.string.s_app_name));
        }
    }

    private void getAllNotesFromDatabase() {

        final GetAllNotesTask getAllNotesTask = new GetAllNotesTask(mMainActivity);
        getAllNotesTask.setCallback(new GetAllNotesTask.Callback() {
            @Override
            public void onSuccess(List<Note> _result) {

                Log.i(getAllNotesTask.LOG_TAG, "DONE.");

                mNoteList = _result;

                if (mNoteList.isEmpty())
                    Log.i(LOG_TAG, "DATABASE IS EMPTY.");

                receiveNewNote();
                receiveOldNote();

                if (!isNewNoteNull && isOldNoteNull)        // new + null
                    addNewNoteToDatabase();
                else if (isNewNoteNull && !isOldNoteNull)   // null + old
                    deleteOldNoteFromDatabase();
                else if (!isNewNoteNull)                    // new + old
                    addNewNoteToDatabase();
                else                                        // null + null
                    initRecyclerView();

                getAllNotesTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(getAllNotesTask.LOG_TAG, "ERROR!");
                Log.e(getAllNotesTask.LOG_TAG, _error.toString());

                getAllNotesTask.releaseCallback();
            }
        });
        getAllNotesTask.execute();
    }

    private void addNewNoteToDatabase() {

        final AddNewNoteTask addNewNoteTask = new AddNewNoteTask(mMainActivity, mNewNote);
        addNewNoteTask.setCallback(new AddNewNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(addNewNoteTask.LOG_TAG, "NEW NOTE IS ADDED TO DATABASE.");

                if (!isNewNoteNull && isOldNoteNull)        // new + null
                    initRecyclerView();
                else if (!isNewNoteNull)                    // new + old
                    deleteOldNoteFromDatabase();

                addNewNoteTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(addNewNoteTask.LOG_TAG, "ERROR!");
                Log.e(addNewNoteTask.LOG_TAG, _error.toString());

                addNewNoteTask.releaseCallback();
            }
        });
        addNewNoteTask.execute();
    }

    private void deleteOldNoteFromDatabase() {

        final DeleteOldNoteTask deleteOldNoteTask = new DeleteOldNoteTask(mMainActivity, mOldNote);
        deleteOldNoteTask.setCallback(new DeleteOldNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(deleteOldNoteTask.LOG_TAG, "OLD NOTE IS DELETED FROM DATABASE.");

                initRecyclerView();

                deleteOldNoteTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(deleteOldNoteTask.LOG_TAG, "ERROR!");
                Log.e(deleteOldNoteTask.LOG_TAG, _error.toString());

                deleteOldNoteTask.releaseCallback();
            }
        });
        deleteOldNoteTask.execute();
    }

    private void receiveNewNote() {

        try {
            mNewNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_OUT_KEY);
            if (mNewNote == null)
                throw new NullPointerException();

            isNewNoteNull = false;
            Log.i(LOG_TAG, "NEW NOTE IS RECEIVED.");

        } catch (NullPointerException _error) {

            isNewNoteNull = true;
            Log.i(LOG_TAG, "NEW NOTE IS NOT RECEIVED. NEW NOTE IS NULL.");
        }
    }

    private void receiveOldNote() {

        try {
            mOldNote = (Note) getArguments().getSerializable(Constants.OLD_NOTE_BUNDLE_KEY);
            if (mOldNote == null)
                throw new NullPointerException();

            isOldNoteNull = false;
            Log.i(LOG_TAG, "OLD NOTE IS RECEIVED.");

        } catch (NullPointerException _error) {

            isOldNoteNull = true;
            Log.i(LOG_TAG, "OLD NOTE IS NOT RECEIVED. OLD NOTE IS NULL.");
        }
    }

    private void receiveState() {

        try {
            mState = getArguments().getParcelable(Constants.RV_STATE_BUNDLE_OUT_KEY);
            if (mState == null)
                throw new NullPointerException();

            Log.i(LOG_TAG, "STATE IS RECEIVED.");

        } catch (NullPointerException _error) {

            Log.i(LOG_TAG, "STATE IS NOT RECEIVED. STATE IS NULL.");
        }
    }

    private void initRecyclerView() {

        final Handler handler = new Handler();

        if (mNoteList.isEmpty()) {

            if (!isNewNoteNull) {

                createRecyclerView();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        addNote();
                    }
                }, 200);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recyclerViewIsReady();
                    }
                }, 600);

            } else
                Log.i(LOG_TAG, "LIST IS EMPTY.");

            mBinding.floatingActionButton.show();

        } else {

            Collections.sort(mNoteList);

            createRecyclerView();

            receiveState();

            if (!isNewNoteNull && isOldNoteNull) { ////////////////////////////////////// new + null

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        addNote();
                    }
                }, 200);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recyclerViewIsReady();
                    }
                }, 600);

            } else if (isNewNoteNull && !isOldNoteNull) { /////////////////////////////// null + old

                if (mState != null)
                    mBinding.rvNotes.getLayoutManager().onRestoreInstanceState(mState);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        deleteNote();
                    }
                }, 200);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recyclerViewIsReady();
                    }
                }, 600);

            } else if (!isNewNoteNull) { ///////////////////////////////////////////////// new + old

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        addNote();
                    }
                }, 200);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        deleteNote();
                    }
                }, 500);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recyclerViewIsReady();
                    }
                }, 900);

            } else { /////////////////////////////////////////////////////////////////// null + null

                if (mState != null)
                    mBinding.rvNotes.getLayoutManager().onRestoreInstanceState(mState);

                recyclerViewIsReady();
            }

            mBinding.floatingActionButton.show();
        }
    }

    private void createRecyclerView() {

        mNoteAdapter = new NoteAdapter(mMainActivity, mNoteList);

        mBinding.rvNotes.setAdapter(mNoteAdapter);
        mBinding.rvNotes.setLayoutManager(new LinearLayoutManager(mMainActivity));
        mBinding.rvNotes.setItemAnimator(new FlipInBottomXAnimator());

        mBinding.rvNotes.getItemAnimator().setAddDuration(300);
        mBinding.rvNotes.getItemAnimator().setRemoveDuration(300);
        mBinding.rvNotes.getItemAnimator().setMoveDuration(300);

        Log.i(LOG_TAG, "RECYCLER VIEW IS CREATED.");

        setHandler();
    }

    private void addNote() {

        mBinding.rvNotes.getLayoutManager().scrollToPosition(0);

        mNoteAdapter.addNote(0, mNewNote);

        mNoteList.add(0, mNewNote);
        Log.i(LOG_TAG, "NEW NOTE IS ADDED TO LIST.");
    }

    private void deleteNote() {

        if (mNoteList.contains(mOldNote)) {

            mNoteAdapter.removeNote(mNoteList.indexOf(mOldNote));

            mNoteList.remove(mOldNote);
            Log.i(LOG_TAG, "OLD NOTE IS DELETED FROM LIST.");
        }
    }

    private void recyclerViewIsReady() {

        isRecyclerViewReady = true;
        Log.i(LOG_TAG, "RECYCLER VIEW IS READY.");
    }

    private void setHandler() {

        if (mNoteAdapter != null)
            mNoteAdapter.setNoteHandler(this);
    }

    private void releaseHandler() {

        if (mNoteAdapter != null)
            mNoteAdapter.releaseNoteHandler();
    }

    private void initButtons() {

        mBinding.setClickerAdd(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                releaseHandler();

                if (isRecyclerViewReady && !mNoteList.isEmpty())
                    mBundle.putParcelable(Constants.RV_STATE_BUNDLE_IN_KEY, mBinding.rvNotes.getLayoutManager().onSaveInstanceState());
                mMainActivity.commitFragment(mCreateEditFragment, mBundle);
            }
        });
    }

    private void initFragmentAndBundle() {

        mCreateEditFragment = new CreateEditFragment();
        mBundle = new Bundle();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isSearchViewOpened && !mSearchView.getQuery().toString().isEmpty())
            mSearchView.setQuery("", true);
        else
            mMainActivity.finish();
    }

    @Override
    public void onClick(Note _note, int _position) {

        if (isRecyclerViewReady) {

            if (mActionMode != null)
                makeSelection(_position);

            else {

                releaseHandler();

                mBundle.putSerializable(Constants.NOTE_BUNDLE_IN_KEY, _note);
                if (isRecyclerViewReady && !mNoteList.isEmpty())
                    mBundle.putParcelable(Constants.RV_STATE_BUNDLE_IN_KEY, mBinding.rvNotes.getLayoutManager().onSaveInstanceState());
                mMainActivity.commitFragment(mCreateEditFragment, mBundle);
            }
        }
    }

    @Override
    public void onLongClick(Note _note, int _position) {

        if (isRecyclerViewReady) {

            if (mActionMode == null)
                initActionMode();

            makeSelection(_position);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionMode              mActionMode;

    private List<Note>              mSelectedNotes;

    private void makeSelection(int _position) {

        if (isSearchViewOpened)
            selectFromList(mFilteredList, _position);
        else
           selectFromList(mNoteList, _position);

        mNoteAdapter.makeSelection(_position);
        final int count = mNoteAdapter.getSelectedItemCount();

        if (count == 0) {

            mActionMode.finish();

        } else {

            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private void selectFromList(List<Note> _noteList, int _position) {

        if (mSelectedNotes.contains(_noteList.get(_position)))
            mSelectedNotes.remove(_noteList.get(_position));
        else
            mSelectedNotes.add(_noteList.get(_position));
    }

    private void initActionMode() {

        final ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode _mode, Menu _menu) {

                if (!isSearchViewOpened) {

                    mBinding.floatingActionButton.hide();
                    mBinding.rvNotes.setPadding(0, 0, 0, 0);
                }

                mSelectedNotes = new ArrayList<>();

                _mode.getMenuInflater().inflate(R.menu.menu_selected, _menu);

                Log.i(LOG_TAG, "ACTION MODE IS CREATED.");

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

                        if (isSearchViewOpened)
                            mSupport.shareNotes(mMainActivity, mNoteAdapter, mFilteredList);
                        else
                            mSupport.shareNotes(mMainActivity, mNoteAdapter, mNoteList);

                        Log.i(LOG_TAG, "SELECTED NOTES ARE SHARED.");

                        _mode.finish();

                        return true;
                    case R.id.mi_select_all:

                        if (isSearchViewOpened) {

                            mNoteAdapter.selectAllItems(mFilteredList);
                            mSelectedNotes = new ArrayList<>(mFilteredList);

                        } else {

                            mNoteAdapter.selectAllItems(mNoteList);
                            mSelectedNotes = new ArrayList<>(mNoteList);
                        }

                        mActionMode.setTitle(String.valueOf(mNoteAdapter.getSelectedItemCount()));
                        mActionMode.invalidate();

                        Log.i(LOG_TAG, "ALL NOTES ARE SELECTED.");

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode _mode) {

                mSelectedNotes.clear();
                mNoteAdapter.clearSelection();
                mActionMode = null;

                if (isSearchViewOpened) {

                    if (mFilteredList.isEmpty())
                        mSearchView.setQuery("", true);

                } else {

                    mBinding.rvNotes.setPadding(0, 0, 0, mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_88dp));
                    mBinding.floatingActionButton.show();
                }

                Log.i(LOG_TAG, "ACTION MODE IS DESTROYED.");
            }
        };

        mActionMode = mMainActivity.startSupportActionMode(actionModeCallBack);
    }

    private void deleteSelectedNotesFromDatabase() {

        final DeleteSelectedNotesTask deleteSelectedNotesTask = new DeleteSelectedNotesTask(mMainActivity, mSelectedNotes);
        deleteSelectedNotesTask.setCallback(new DeleteSelectedNotesTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(deleteSelectedNotesTask.LOG_TAG, "SELECTED NOTES ARE DELETED FROM DATABASE.");

                mNoteAdapter.removeNotes(mNoteAdapter.getSelectedItems());

                mNoteList.removeAll(mSelectedNotes);
                if (isSearchViewOpened)
                    mFilteredList.removeAll(mSelectedNotes);
                Log.i(LOG_TAG, "SELECTED NOTES ARE DELETED FROM LIST.");

                mActionMode.finish();

                deleteSelectedNotesTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.i(deleteSelectedNotesTask.LOG_TAG, "ERROR!");
                Log.e(deleteSelectedNotesTask.LOG_TAG, _error.toString());

                deleteSelectedNotesTask.releaseCallback();
            }
        });
        deleteSelectedNotesTask.execute();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private SearchView      mSearchView;
    private boolean         isSearchViewOpened;

    private List<Note>      mFilteredList;


    @Override
    public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater) {

        _inflater.inflate(R.menu.menu_list, _menu);

        final MenuItem menuItem = _menu.findItem(R.id.mi_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String _query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String _query) {

                if (isRecyclerViewReady) {

                    mSupport.filterList(mNoteList, _query, mFilteredList);
                    mNoteAdapter.animateTo(mFilteredList);
                    mBinding.rvNotes.scrollToPosition(0);

                    Log.i(LOG_TAG, "SEARCH VIEW IS SEARCHING.");
                }

                return true;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSearchViewOpened = true;

                mBinding.floatingActionButton.hide();
                mBinding.rvNotes.setPadding(0, 0, 0, 0);

                mFilteredList = new ArrayList<>(mNoteList);

                Log.i(LOG_TAG, "SEARCH VIEW IS OPENED.");
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                mFilteredList.clear();

                mBinding.rvNotes.setPadding(0, 0, 0, mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_88dp));
                mBinding.floatingActionButton.show();

                mSearchView = null;

                isSearchViewOpened = false;

                Log.i(LOG_TAG, "SEARCH VIEW IS CLOSED.");

                return false;
            }
        });

        super.onCreateOptionsMenu(_menu, _inflater);
    }
}