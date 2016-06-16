package mitso.v.homework_22.fragments.create_fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Date;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.databinding.FragmentCreateEditBinding;
import mitso.v.homework_22.fragments.BaseFragment;
import mitso.v.homework_22.fragments.list_fragment.ListFragment;
import mitso.v.homework_22.models.Note;

public class CreateEditFragment extends BaseFragment {

    private String                      LOG_TAG = Constants.CREATE_EDIT_FRAGMENT_LOG_TAG;

    private FragmentCreateEditBinding   mBinding;

    private Note                        mNote;
    private boolean                     isNoteNotNull;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_edit, container, false);
        final View rootView = mBinding.getRoot();

        receiveNote();

        setHasOptionsMenu(true);
        initActionBar();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

         initBackButton();
    }

    private void initBackButton() {

        mBinding.etCreateEditNote.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    mBinding.etCreateEditNote.clearFocus();

                return false;
            }
        });

        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                        Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");
                        Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

                        mMainActivity.commitFragment(new ListFragment());

                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null) {

            mMainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mMainActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mMainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (isNoteNotNull)
                mMainActivity.getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                        Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                        "'>" + getResources().getString(R.string.s_edit_note) + "</font>"));
            else
                mMainActivity.getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                        Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                        "'>" + getResources().getString(R.string.s_create_note) + "</font>"));
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
            case android.R.id.home:

                Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");
                Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

                hideKeyboard();

                mMainActivity.commitFragment(new ListFragment());

                return true;
            case R.id.mi_share:

                if (!mBinding.etCreateEditNote.getText().toString().isEmpty()) {

                    final Intent shareNoteIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareNoteIntent.setType("text/plain");
                    shareNoteIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMainActivity.getResources().getString(R.string.s_share_note));
                    shareNoteIntent.putExtra(android.content.Intent.EXTRA_TEXT, mBinding.etCreateEditNote.getText().toString());

                    if (shareNoteIntent.resolveActivity(mMainActivity.getPackageManager()) != null)
                        startActivity(Intent.createChooser(shareNoteIntent, mMainActivity.getResources().getString(R.string.s_share_note)));
                    else
                        Toast.makeText(mMainActivity, mMainActivity.getResources().getString(R.string.s_no_program), Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.mi_save:

                final ListFragment listFragment = new ListFragment();

                if (!mBinding.etCreateEditNote.getText().toString().isEmpty()) {

                    final Note note = new Note(mBinding.etCreateEditNote.getText().toString(), new Date());
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.NOTE_BUNDLE_OUT_KEY, note);

                    Log.i(LOG_TAG, "NEW NOTE IS SENT.");

                    if (isNoteNotNull) {
                        bundle.putSerializable(Constants.OLD_NOTE_BUNDLE_KEY, mNote);
                        Log.i(LOG_TAG, "OLD NOTE IS SENT.");
                    } else
                        Log.i(LOG_TAG, "OLD NOTE IS NOT SENT. OLD NOTE IS NULL.");

                    listFragment.setArguments(bundle);
                } else
                    Log.i(LOG_TAG, "NEW NOTE IS NOT SENT. NEW NOTE IS NULL.");

                hideKeyboard();

                mMainActivity.commitFragment(listFragment);

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

    private void receiveNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_IN_KEY);
            if (mNote == null)
                throw new NullPointerException();

            mBinding.etCreateEditNote.setText(mNote.getBody());
            isNoteNotNull = true;
            Log.i(LOG_TAG, "NOTE IS RECEIVED.");

        } catch (NullPointerException _error) {

            isNoteNotNull = false;
            Log.i(LOG_TAG, "NOTE IS NOT RECEIVED. NOTE IS NULL.");
        }
    }
}