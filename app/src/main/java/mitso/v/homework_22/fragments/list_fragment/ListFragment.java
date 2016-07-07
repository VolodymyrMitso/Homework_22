package mitso.v.homework_22.fragments.list_fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private Note                    mNewNote;
    private Note                    mOldNote;
    private boolean                 isNewNoteNotNull;
    private boolean                 isOldNoteNotNull;

    private DatabaseHelper          mDatabaseHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {

        mBinding = DataBindingUtil.inflate(_inflater, R.layout.fragment_list, _container, false);
        final View rootView = mBinding.getRoot();

        initActionBar();
        setHasOptionsMenu(true);

        if (mMainActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

            Log.i(LOG_TAG, "DATABASE EXISTS.");

            getAllNotesFromDatabase();

        } else {

            Log.i(LOG_TAG, "DATABASE DOESN'T EXIST.");

            mNoteList = new ArrayList<>();
            addNoteToList();
            if (isNewNoteNotNull)
                addNewNoteToDatabase();
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
                    Integer.toHexString(mMainActivity.getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + mMainActivity.getResources().getString(R.string.s_app_name) + "</font>"));
        }
    }

    private void getAllNotesFromDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final GetAllNotesTask getAllNotesTask = new GetAllNotesTask(mDatabaseHelper);
        getAllNotesTask.setCallback(new GetAllNotesTask.Callback() {
            @Override
            public void onSuccess(List<Note> _result) {

                Log.i(getAllNotesTask.LOG_TAG, "ON SUCCESS.");
                mDatabaseHelper.close();

                mNoteList = _result;

                addNoteToList();
                if (isNewNoteNotNull)
                    addNewNoteToDatabase();
                else
                    initRecyclerView();

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

    private void addNewNoteToDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final AddNewNoteTask addNewNoteTask = new AddNewNoteTask(mDatabaseHelper, mNewNote);
        addNewNoteTask.setCallback(new AddNewNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(addNewNoteTask.LOG_TAG, "ON SUCCESS.");
                mDatabaseHelper.close();

                deleteNoteFromList();
                if (isOldNoteNotNull)
                    deleteOldNoteFromDatabase();
                else
                    initRecyclerView();

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

    private void deleteOldNoteFromDatabase() {

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final DeleteOldNoteTask deleteOldNoteTask = new DeleteOldNoteTask(mDatabaseHelper, mOldNote);
        deleteOldNoteTask.setCallback(new DeleteOldNoteTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(deleteOldNoteTask.LOG_TAG, "ON SUCCESS.");
                mDatabaseHelper.close();

                initRecyclerView();

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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionMode              mActionMode;

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
                mDatabaseHelper.close();

                mNoteAdapter.removeNotes(mNoteAdapter.getSelectedItems());
                mNoteList.removeAll(mSelectedNotes);
                if (isSearchViewOpened)
                    mFilteredList.removeAll(mSelectedNotes);
                mActionMode.finish();

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

        if (isSearchViewOpened) {

            if (mSelectedNotes.contains(mFilteredList.get(_position)))
                mSelectedNotes.remove(mFilteredList.get(_position));
            else
                mSelectedNotes.add(mFilteredList.get(_position));

        } else {

            if (mSelectedNotes.contains(mNoteList.get(_position)))
                mSelectedNotes.remove(mNoteList.get(_position));
            else
                mSelectedNotes.add(mNoteList.get(_position));
        }

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

        final ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode _mode, Menu _menu) {

                if (!isSearchViewOpened) {
                    mBinding.floatingActionButton.hide();
                    mBinding.rvNotes.setPadding(0,0,0,0);
                }

                mSelectedNotes = new ArrayList<>();
                mTempSelectedNotes = new ArrayList<>();

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

                        if (isSearchViewOpened)
                            shareNotes(mNoteAdapter, mFilteredList);
                        else
                            shareNotes(mNoteAdapter, mNoteList);

                        _mode.finish();

                        return true;
                    case R.id.mi_select_all:

                        if (!isAllItemsSelected) {

                            mTempSelectedNotes = mSelectedNotes;

                            if (isSearchViewOpened) {

                                mNoteAdapter.selectAllItems(mFilteredList);
                                mSelectedNotes = new ArrayList<>(mFilteredList);

                            } else {

                                mNoteAdapter.selectAllItems(mNoteList);
                                mSelectedNotes = new ArrayList<>(mNoteList);
                            }

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

                if (!isSearchViewOpened) {
                    mBinding.rvNotes.setPadding(0,0,0,mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_78dp));
                    mBinding.floatingActionButton.show();
                }
            }
        };

        mActionMode = mMainActivity.startSupportActionMode(actionModeCallBack);
    }

    private void shareNotes(NoteAdapter _noteAdapter, List<Note> _noteList) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < _noteAdapter.getSelectedItems().size(); i++) {

            final int index = _noteAdapter.getSelectedItems().get(i);

            stringBuilder.append(_noteList.get(index).getFormattedDate());
            stringBuilder.append(" - ");
            stringBuilder.append(_noteList.get(index).getFormattedTime());
            stringBuilder.append("\n");
            stringBuilder.append(_noteList.get(index).getBody());

            if (i != _noteAdapter.getSelectedItems().size() - 1)
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
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean         isSearchViewOpened;
    private List<Note>      mFilteredList;

    @Override
    public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater) {
        _inflater.inflate(R.menu.menu_list, _menu);

        final MenuItem menuItem = _menu.findItem(R.id.mi_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        final int id = android.support.v7.appcompat.R.id.search_button;
        final ImageView imageView = (ImageView) searchView.findViewById(id);
        imageView.setImageResource(R.drawable.ic_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String _query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String _query) {

                filterList(mNoteList, _query, mFilteredList);
                mNoteAdapter.animateTo(mFilteredList);
                mBinding.rvNotes.scrollToPosition(0);

                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSearchViewOpened = true;

                mBinding.floatingActionButton.hide();
                mBinding.rvNotes.setPadding(0,0,0,0);

                mFilteredList = new ArrayList<>();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                mFilteredList.clear();

                mBinding.rvNotes.setPadding(0,0,0,mMainActivity.getResources().getDimensionPixelSize(R.dimen.d_size_78dp));
                mBinding.floatingActionButton.show();

                isSearchViewOpened = false;

                return false;
            }
        });

        super.onCreateOptionsMenu(_menu, _inflater);
    }

    private void filterList(List<Note> _noteList, String _query, List<Note> _filteredList) {
        _query = _query.toLowerCase();

        _filteredList.clear();
        for (Note note : _noteList) {
            final String noteBody = note.getBody().toLowerCase();
            final String noteDate = note.getFormattedDate().toLowerCase();
            if (noteBody.contains(_query) || noteDate.contains(_query))
                _filteredList.add(note);
        }
    }
}