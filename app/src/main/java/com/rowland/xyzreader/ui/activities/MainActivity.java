package com.rowland.xyzreader.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.rowland.xyzreader.R;
import com.rowland.xyzreader.ui.fragments.MainFragment;

public class MainActivity extends BaseToolBarActivity {

    // Logging Identifier for class
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout
        setContentView(R.layout.activity_main);
        // However, if we're being restored from a previous state, then we don't need to do anything
        // and should return or else we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        else {
            // Pass bundle to the fragment
            showMainFragment(null);
        }
    }

    // Insert the MainFragment
    private void showMainFragment(Bundle args) {
        // Acquire the Fragment manger
        FragmentManager fm = getSupportFragmentManager();
        // Begin the transaction
        FragmentTransaction ft = fm.beginTransaction();
        // Create new fragment
        MainFragment mainFragment = MainFragment.newInstance(args);
        // Prefer replace() over add() see <a>https://github.com/RowlandOti/PopularMovies/issues/1</a>
        ft.replace(R.id.fragment_container, mainFragment);
        ft.commit();
    }
}
