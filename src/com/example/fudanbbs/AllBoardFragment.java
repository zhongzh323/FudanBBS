/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.RecommendBoardFragment.recommendBoardAsyncTask;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author Joseph.Zhong
 *
 */
public class AllBoardFragment extends Fragment {
	private ArrayList<HashMap<String, String>> allboardlist;
	private ArrayList<String[]> autohintlist, lastusedlist;
	private View rootview;
	private SearchView searchview;
	private TextView textview;
	private ListView autohintlistview, allboardslistview;
	private ProgressDialog progressdialog;
	private SimpleAdapter allboardadapter;
	private String TAG = "##################"+this.getClass().getName();
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(null == rootview){
			rootview = inflater.inflate(R.layout.allboardsfragement, container, false);
		}
		searchview = (SearchView) rootview.findViewById(R.id.searchView);
		textview = (TextView) rootview.findViewById(R.id.lastusedhint);
		autohintlistview = (ListView) rootview.findViewById(R.id.autohintlistView);
		allboardslistview = (ListView) rootview.findViewById(R.id.allboardsListView);
		progressdialog = new ProgressDialog(getActivity());
		progressdialog.setMessage(getString(R.string.loadingboardlist));
		progressdialog.setCancelable(false);
		progressdialog.setCanceledOnTouchOutside(false);
		progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		

		AllBoardsAsyncTask task = new AllBoardsAsyncTask();
    	task.execute();
		return rootview;
	}
	public class AllBoardsAsyncTask extends AsyncTask{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog.show();	
			if(null == allboardlist){
				allboardlist = new ArrayList<HashMap<String, String>>();
			}else{
				allboardlist.clear();
			}
			Log.v(TAG, "onPreExecute");
		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
	    	allboardadapter = new SimpleAdapter(rootview.getContext(), allboardlist, 
	    			R.layout.allboard, new String[]{"boardtitle", "boarddesc"}, new int[]{R.id.boardtitle, R.id.boarddesc});
	    	allboardslistview.setAdapter(allboardadapter);
	    	allboardslistview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub
					TextView boardtitle = (TextView) view.findViewById(R.id.boardtitle);
					String boardtitlestring = boardtitle.getText().toString().trim();
					TextView boarddesc = (TextView) view.findViewById(R.id.boarddesc);
					String boarddescstring = boardtitle.getText().toString().trim();
		
					Intent intent = new Intent();
					intent.setClassName(getActivity(), "com.example.fudanbbs.BoardActivity");
					Bundle bundle = new Bundle();
					String boardURL = "http://bbs.fudan.edu.cn/bbs/doc?board="+boardtitlestring.substring(1, boardtitlestring.length()-1);
					bundle.putString("boardURL", boardURL);
					bundle.putString("boardname", boarddescstring.substring(1, boarddescstring.length()-1));
					intent.putExtras(bundle);
					startActivity(intent);

				}});
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			Log.v(TAG, "onPostExecute");
		}



		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url = "http://bbs.fudan.edu.cn/bbs/all";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements boards = doc.getElementsByTag("brd ");
				for(Element board: boards){

					String[] string = new String[2];
					string[0] = board.attr("title");
					string[1] = board.attr("desc");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("boardtitle", "["+string[0]+"] ");
					map.put("boarddesc", string[1]);
					allboardlist.add(map);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
