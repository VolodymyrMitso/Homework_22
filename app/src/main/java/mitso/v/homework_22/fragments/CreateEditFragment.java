package mitso.v.homework_22.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.databinding.FragmentCreateEditBinding;
import mitso.v.homework_22.models.Note;
import mitso.v.homework_22.support.Support;

public class CreateEditFragment extends BaseFragment {

    private String                      LOG_TAG = Constants.CREATE_EDIT_FRAGMENT_LOG_TAG;

    private Support                     mSupport;

    private FragmentCreateEditBinding   mBinding;

    private Note                        mNote;
    private boolean                     isNoteNull;

    private Parcelable                  mState;

    private ListFragment                mListFragment;
    private Bundle                      mBundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {

        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mMainActivity, R.style.FragmentCreateEditTheme);
        final LayoutInflater layoutInflater = _inflater.cloneInContext(contextThemeWrapper);

        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_create_edit, _container, false);
        final View rootView = mBinding.getRoot();

        mSupport = new Support();

        receiveNote();
        receiveState();

        setHasOptionsMenu(true);
        initActionBar();

        if (isNoteNull)
            showKeyboardAndSetFocus(mBinding.etCreateEditNote);

        initFragmentAndBundle();

        return rootView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");
        Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

        if (mState != null)
            mBundle.putParcelable(Constants.RV_STATE_BUNDLE_OUT_KEY, mState);
        mMainActivity.commitFragment(mListFragment, mBundle);
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null) {

            mMainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mMainActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mMainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (isNoteNull)
                mMainActivity.getSupportActionBar().setTitle(mMainActivity.getResources().getString(R.string.s_create_note));
            else
                mMainActivity.getSupportActionBar().setTitle(mMainActivity.getResources().getString(R.string.s_edit_note));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater) {

        _inflater.inflate(R.menu.menu_create_edit, _menu);
        super.onCreateOptionsMenu(_menu, _inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {

        switch (_item.getItemId()) {
            case R.id.mi_share:

                mSupport.shareNote(mMainActivity, mBinding.etCreateEditNote);

                return true;
            case R.id.mi_delete:

                if (!isNoteNull) {

                    Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");

                    mBundle.putSerializable(Constants.OLD_NOTE_BUNDLE_KEY, mNote);
                    Log.i(LOG_TAG, "OLD NOTE IS SENT.");

                    if (mState != null)
                        mBundle.putParcelable(Constants.RV_STATE_BUNDLE_OUT_KEY, mState);
                    mMainActivity.commitFragment(mListFragment, mBundle);

                } else
                    Toast.makeText(mMainActivity, mMainActivity.getResources().getString(R.string.s_not_in_list), Toast.LENGTH_LONG).show();

                return true;
            case R.id.mi_save:

                if (!mBinding.etCreateEditNote.getText().toString().isEmpty()) {

                    final Note note = new Note(mBinding.etCreateEditNote.getText().toString(), new Date());

                    mBundle.putSerializable(Constants.NOTE_BUNDLE_OUT_KEY, note);
                    Log.i(LOG_TAG, "NEW NOTE IS SENT.");

                } else
                    Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");

                if (!isNoteNull) {

                    mBundle.putSerializable(Constants.OLD_NOTE_BUNDLE_KEY, mNote);
                    Log.i(LOG_TAG, "OLD NOTE IS SENT.");

                } else
                    Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

                if (mState != null)
                    mBundle.putParcelable(Constants.RV_STATE_BUNDLE_OUT_KEY, mState);
                mMainActivity.commitFragment(mListFragment, mBundle);

                return true;
            case android.R.id.home:

                Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");
                Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

                if (mState != null)
                    mBundle.putParcelable(Constants.RV_STATE_BUNDLE_OUT_KEY, mState);
                mMainActivity.commitFragment(mListFragment, mBundle);

                return true;
            default:
                return super.onOptionsItemSelected(_item);
        }
    }

    private void hideKeyboard() {

        final View view = mMainActivity.getCurrentFocus();
        if (view != null) {
            final InputMethodManager inputMethodManager =
                    (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboardAndSetFocus(EditText _editText) {

        _editText.requestFocus();
        final InputMethodManager inputMethodManager =
                (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onPause() {
        super.onPause();

        hideKeyboard();
    }

    private void receiveNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_IN_KEY);
            if (mNote == null)
                throw new NullPointerException();

            mBinding.etCreateEditNote.setText(mNote.getBody());
            isNoteNull = false;
            Log.i(LOG_TAG, "NOTE IS RECEIVED.");

        } catch (NullPointerException _error) {

            isNoteNull = true;
            Log.i(LOG_TAG, "NOTE IS NOT RECEIVED. NOTE IS NULL.");
        }
    }

    private void receiveState() {

        try {
            mState = getArguments().getParcelable(Constants.RV_STATE_BUNDLE_IN_KEY);
            if (mState == null)
                throw new NullPointerException();

            Log.i(LOG_TAG, "STATE IS RECEIVED.");

        } catch (NullPointerException _error) {

            Log.i(LOG_TAG, "STATE IS NOT RECEIVED. STATE IS NULL.");
        }
    }

    private void initFragmentAndBundle() {

        mListFragment = new ListFragment();
        mBundle = new Bundle();
    }
}