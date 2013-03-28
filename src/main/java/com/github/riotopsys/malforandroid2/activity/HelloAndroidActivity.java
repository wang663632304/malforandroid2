package com.github.riotopsys.malforandroid2.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.fragment.ItemListFragment;
import com.github.riotopsys.malforandroid2.server.ServerInterface;

public class HelloAndroidActivity extends BaseActivity {

    private static String TAG = HelloAndroidActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
        
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.list_frame, new ItemListFragment());
        transaction.commit();
        
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	if ( item.getItemId() == R.id.refresh_menu_item){
    		ServerInterface.getAnimeList(this);
    	}
    	return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
}

