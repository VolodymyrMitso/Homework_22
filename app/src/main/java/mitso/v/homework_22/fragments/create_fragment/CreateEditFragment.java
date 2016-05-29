package mitso.v.homework_22.fragments.create_fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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

        addNote();

        initActionBar();

        initButtons();

        return rootView;
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null) {

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

    private void initButtons() {

        mBinding.setClickerBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, new ListFragment())
                        .commitAllowingStateLoss();
            }
        });

        mBinding.setClickerSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ListFragment listFragment = new ListFragment();

                if (!mBinding.etCreateEditNote.getText().toString().isEmpty()) {

                    final Note note = new Note(mBinding.etCreateEditNote.getText().toString(), new Date());
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.NOTE_BUNDLE_OUT_KEY, note);

                    Log.i(LOG_TAG, "NOTE IS SENT.");

                    if (isNoteNotNull) {
                        bundle.putSerializable(Constants.OLD_NOTE_BUNDLE_KEY, mNote);
                        Log.i(LOG_TAG, "OLD NOTE IS SENT.");
                    }

                    listFragment.setArguments(bundle);
                }

                hideKeyboard();

                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, listFragment)
                        .commitAllowingStateLoss();
            }
        });
    }

    private void hideKeyboard() {

        final View view = mMainActivity.getCurrentFocus();
        if (view != null) {
            final InputMethodManager inputMethodManager =
                    (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNote() {

        try {
            mNote = (Note) getArguments().getSerializable(Constants.NOTE_BUNDLE_IN_KEY);
            if (mNote != null)
                mBinding.etCreateEditNote.setText(mNote.getBody());

            isNoteNotNull = true;
            Log.i(LOG_TAG, "NOTE IS RECEIVED.");

        } catch (NullPointerException _error) {

            isNoteNotNull = false;
            Log.e(LOG_TAG, _error.toString());
            Log.i(LOG_TAG, "NOTE IS NULL.");
        }
    }
}