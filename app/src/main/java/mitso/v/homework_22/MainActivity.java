package mitso.v.homework_22;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mitso.v.homework_22.databinding.ActivityMainBinding;
import mitso.v.homework_22.fragments.list_fragment.ListFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding     mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, new ListFragment())
                .commitAllowingStateLoss();
    }
}
