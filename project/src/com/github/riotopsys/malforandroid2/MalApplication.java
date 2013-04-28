/**
 * Copyright 2013 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.riotopsys.malforandroid2;

import java.io.File;

import roboguice.RoboGuice;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MalApplication extends android.app.Application {

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
