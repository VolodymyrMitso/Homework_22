package mitso.v.homework_22;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import mitso.v.homework_22.fragments.BaseFragment;
import mitso.v.homework_22.fragments.list_fragment.ListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        DataBindingUtil.setContentView(this, R.layout.activity_main);

        commitFragment(new ListFragment(), null);
    }

    public void commitFragment(BaseFragment _baseFragment, Bundle _bundle) {

        _baseFragment.setArguments(_bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fl_container, _baseFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        final BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fl_container);
        baseFragment.onBackPressed();
    }
}
