package com.github.riotopsys.malforandroid2;

import java.io.File;

import roboguice.RoboGuice;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		
		File cacheDir = new File(getCacheDir(),"imageCache");
		if ( !cacheDir.exists() ){
			cacheDir.mkdirs();
		}
		
		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultDisplayImageOptions )  
		.discCache(new TotalSizeLimitedDiscCache(cacheDir, 10*1024*1024))
		.build();
		
		ImageLoader.getInstance().init(config);
		
		RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, 
				new CustomModule(),
				RoboGuice.newDefaultRoboModule(this));
	}

}
