package mitso.v.homework_22.fragments.create_fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.databinding.FragmentCreateBinding;
import mitso.v.homework_22.fragments.BaseFragment;
import mitso.v.homework_22.fragments.list_fragment.ListFragment;
import mitso.v.homework_22.models.Note;

public class CreateFragment extends BaseFragment {

    private FragmentCreateBinding       mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create, container, false);
        final View rootView = mBinding.getRoot();

        initActionBar();
        initButtons();

        return rootView;
    }

    private void initActionBar() {

        if (mMainActivity.getSupportActionBar() != null)
            mMainActivity.getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_create_note) + "</font>"));
    }

    private void initButtons() {

        mBinding.setClickerBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, new ListFragment())
                        .commitAllowingStateLoss();
            }
        });

        mBinding.setClickerSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListFragment listFragment = new ListFragment();

                if (!mBinding.etCreateNote.getText().toString().isEmpty()) {

                    Note note = new Note(mBinding.etCreateNote.getText().toString(), new Date());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.NOTE_BUNDLE_KEY, note);
                    listFragment.setArguments(bundle);
                }

                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, listFragment)
                        .commitAllowingStateLoss();
            }
        });
    }
}