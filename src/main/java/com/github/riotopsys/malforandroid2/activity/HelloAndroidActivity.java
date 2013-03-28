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

