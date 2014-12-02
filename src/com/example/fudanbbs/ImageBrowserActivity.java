/**
 * 
 */
package com.example.fudanbbs;

import java.util.ArrayList;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * @author Joseph.Zhong
 *
 */
public class ImageBrowserActivity extends Activity {
	private ViewPager viewpager;
	private ViewPagerAdapter viewpageradapter;
	private ImageView imageview;
	private ArrayList<View> imageviewlist;
	private String currentimage;
	private ArrayList<String> imagearray;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ImageLoaderConfiguration config;
	private int index;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		currentimage = bundle.getString("currentimage");
		imagearray = bundle.getStringArrayList("imagearray");
		setContentView(R.layout.imageviewpager);
		Log.v("imagebrowser", currentimage);
		Log.v("tag", imagearray.get(0));
		// image loader configuration		
		options  = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true)
				.showImageOnLoading(R.drawable.fudanbbsimageloading).resetViewBeforeLoading(true).build();
		config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(options)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).threadPriority(Thread.NORM_PRIORITY-2)
				.writeDebugLogs().build();	
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);		
		
//		imageviewlist = new ArrayList<View>();
		
		for(int i=0; i<imagearray.size(); i++){

			if(currentimage == imagearray.get(i)){
				index = i;
			}
		}
		
		viewpager = (ViewPager)findViewById(R.id.imageviewpager);
		viewpageradapter = new ViewPagerAdapter(imagearray);
		viewpager.setAdapter(viewpageradapter);
		viewpager.setCurrentItem(index);

	}
	public class ViewPagerAdapter extends PagerAdapter{
		@Override
		public void destroyItem(ViewGroup container,
				int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((View) object);
		}

		@Override
		public void setPrimaryItem(ViewGroup container,
				int position, Object object) {
			// TODO Auto-generated method stub
			super.setPrimaryItem(container, position, object);
		}

		private ArrayList<String> imagelist;
		@Override
		public Object instantiateItem(ViewGroup container,
				int position) {
			ViewPager viewpager = (ViewPager)container;
			// TODO Auto-generated method stub
//			String url = "http://clubfiles.liba.com/2014/09/19/12/40403557.jpg";
			imageview = new ImageView(ImageBrowserActivity.this);
			imageLoader.displayImage(currentimage, imageview);			
			imageview.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
				
			});
//			imageLoader.displayImage(imagelist.get(position), imageview);
			viewpager.addView(imageview);
			return imageview;
		}

		public ViewPagerAdapter(
				ArrayList<String> imagearray) {
			// TODO Auto-generated constructor stub
			this.imagelist = imagearray;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagearray.size();
//			return imagelist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0,
				Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
	}
}
