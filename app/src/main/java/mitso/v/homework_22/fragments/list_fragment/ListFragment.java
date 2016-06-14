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
    private boolean                 isRecyclerViewCreated;

    private Note                    mNote;
    private Note                    mOldNote;

    private DatabaseHelper          mDatabaseHelper;
//    private boolean                 isDatabaseSet;

    private boolean                 isNoteNotNull;

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
            deleteOldNote();
            if (isNoteNotNull)
                setDatabaseData();
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
                deleteOldNote();
                if (isNoteNotNull)
                    setDatabaseData();
                else
                    initRecyclerView();

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

//        isDatabaseSet = false;

        mDatabaseHelper = new DatabaseHelper(mMainActivity);

        final SetDataTask setDataTask = new SetDataTask(mMainActivity, mDatabaseHelper, mNoteList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void onSuccess() {

                Log.i(setDataTask.LOG_TAG, "ON SUCCESS.");
//                isDatabaseSet = true;

                if (!isRecyclerViewCreated)
                    initRecyclerView();

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

//                if (mMainActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {

//                    if (isDatabaseSet) {

                        if (mActionMode != null)
                            mActionMode.finish();

                        releaseHandler();
                        mMainActivity.commitFragment(new CreateEditFragment());

//                    } else
//                        Toast.makeText(mMainActivity, "wait few sec and try again", Toast.LENGTH_SHORT).show();

//                } else {
//
//                    releaseHandler();
//                    mMainActivity.commitFragment(new CreateEditFragment());
//                }
            }
        });
    }

    private void addNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_OUT_KEY);
            if (mNote == null)
                throw new NullPointerException();

            mNoteList.add(0, mNote);
            isNoteNotNull = true;
            Log.i(LOG_TAG, "NOTE IS ADDED.");

        } catch (NullPointerException _error) {

            isNoteNotNull = false;
//            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "NOTE IS NOT ADDED. NOTE IS NULL.");
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

//            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "OLD NOTE IS NOT DELETED. OLD NOTE IS NULL.");
        }
    }

    private void initRecyclerView() {

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

    @Override
    public void onClick(Note _note, int _position) {

//        if (isDatabaseSet) {

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

//        } else
//            Toast.makeText(mMainActivity, "wait few sec and try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(Note _note, int _position) {

//        if (isDatabaseSet) {

            if (mActionMode == null)
                initActionMode();

            toggleSelection(_position);

//        } else
//            Toast.makeText(mMainActivity, "wait few sec and try again", Toast.LENGTH_SHORT).show();
    }

    private void toggleSelection(int _position) {

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

                        mNoteAdapter.removeNotes(mNoteAdapter.getSelectedItems());

                        /** !!! */
                        mNoteList = mNoteAdapter.getNoteList();
                        if (!mNoteList.isEmpty())
                            Log.i(LOG_TAG, "LIST ISN'T EMPTY.");
                        else
                            Log.i(LOG_TAG, "LIST IS EMPTY.");

                        setDatabaseData();
                        _mode.finish();

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

                        return true;
                    case R.id.mi_select_all:

                        if (!isAllItemsSelected) {

                            mNoteAdapter.selectAllItems(mNoteList);
                            mActionMode.setTitle(String.valueOf(mNoteAdapter.getSelectedItemCount()));
                            mActionMode.invalidate();
                            mSelectedMenu.getItem(0).setIcon(mMainActivity.getResources().getDrawable(R.drawable.ic_select_all_filled));
                            isAllItemsSelected = true;

                        } else {

                            mNoteAdapter.deselectAllItems();
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
                mNoteAdapter.clearSelection();
                mActionMode = null;
            }
        };

        mActionMode = mMainActivity.startSupportActionMode(mActionModeCallBack);
    }
}