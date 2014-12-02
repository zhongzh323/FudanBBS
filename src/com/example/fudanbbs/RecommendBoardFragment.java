package com.example.fudanbbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.Top10Fragment.top10AsycTask;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecommendBoardFragment extends Fragment {
	private ArrayList<HashMap<String, ArrayList<String[]>>> recommendboardlist;
	private ProgressDialog progressdialog;
	private View rootView;
	private int textsize;
	static class ViewHolder{
		TextView boardname;
		TextView boardnamefake;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		if(null == rootView){
    		rootView = inflater.inflate(R.layout.recommendboardfragment, container,false);
    		generateView();
    		ExpandableListView listview = (ExpandableListView)rootView.findViewById(R.id.recommendBoardexpandableListView);
    		listview.setAdapter(new expandableAdapter());
    		listview.setOnItemClickListener(new onItemClickListener());
    		listview.setOnChildClickListener(new OnChildClickListener(){

				@Override
				public boolean onChildClick(
						ExpandableListView parent, View v,
						int groupPosition,
						int childPosition, long id) {
					// TODO Auto-generated method stub

					TextView boardnamefake = (TextView) v.findViewById(R.id.boardnamefake);
					openBoard(boardnamefake.getText().toString().trim());		
					
					return false;
				}
    			
    		});
		}
		textsize = 20;
		return rootView;
	}
	public void generateView(){
		progressdialog = new ProgressDialog(getActivity());
		progressdialog.setMessage(getString(R.string.loading));
		progressdialog.setCancelable(false);
		progressdialog.setCanceledOnTouchOutside(false);
		progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
		progressdialog.show();		
		recommendBoardAsyncTask task = new recommendBoardAsyncTask();
    	task.execute();
    	try {
			task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressdialog.dismiss();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	public void openBoard(String boardtitle){
		Intent intent = new Intent();
		intent.setClassName(getActivity(), "com.example.fudanbbs.BoardActivity");
		Bundle bundle = new Bundle();
		String boardURL = "http://bbs.fudan.edu.cn/bbs/doc?board="+boardtitle;
		bundle.putString("boardURL", boardURL);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	public class expandableAdapter extends BaseExpandableListAdapter{    
		ViewHolder holder;
    	@Override
    	public int getGroupCount() {
    		// TODO Auto-generated method stub
    		return recommendboardlist.size();
    	}
    
    	@Override
    	public int getChildrenCount(int groupPosition) {
    		// TODO Auto-generated method stub
    		return recommendboardlist.get(groupPosition).get("boards").size();
    	}
    
    	@Override
    	public Object getGroup(int groupPosition) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    
    	@Override
    	public Object getChild(int groupPosition,
    			int childPosition) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    
    	@Override
    	public long getGroupId(int groupPosition) {
    		// TODO Auto-generated method stub
    		return groupPosition;
    	}
    
    	@Override
    	public long getChildId(int groupPosition,
    			int childPosition) {
    		// TODO Auto-generated method stub
    		return childPosition;
    	}
    
    	@Override
    	public boolean hasStableIds() {
    		// TODO Auto-generated method stub
    		return false;
    	}
    
    	@Override
    	public View getGroupView(int groupPosition,
    			boolean isExpanded, View convertView,
    			ViewGroup parent) {
    		// TODO Auto-generated method stub
    		TextView textview = new TextView(parent.getContext());
    		ArrayList arraylist = recommendboardlist.get(groupPosition).get("secinfo");
    		String[] string = (String[]) arraylist.get(0);
    		textview.setText("       "+string[0]+" "+string[1]);
    		textview.setTextSize(textsize);
    		textview.setPadding(0, 6, 0, 6);
    		return textview;    		


    	}
    
    	@Override
    	public View getChildView(int groupPosition,
    			int childPosition, boolean isLastChild,
    			View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub
	
    		if(null == convertView){
        		LayoutInflater inflater = LayoutInflater.from(getActivity());
        		convertView = inflater.inflate(R.layout.recommendboard, null);
    			holder = new ViewHolder();
    			holder.boardname = (TextView) convertView.findViewById(R.id.boardname);
    			holder.boardnamefake = (TextView) convertView.findViewById(R.id.boardnamefake);
    			convertView.setTag(holder);
    		}else{
    			holder = (ViewHolder) convertView.getTag();
    		}
    		ArrayList arraylist = recommendboardlist.get(groupPosition).get("boards");
    		String[] string = (String[]) arraylist.get(childPosition);
    		holder.boardname.setText(" ["+string[0]+"] "+string[1]);
    		holder.boardname.setPadding(0, 6, 0, 6);
    		holder.boardname.setTextSize(textsize);
    		holder.boardnamefake.setText(string[0]);
    		return convertView;
    	}
    
    	@Override
    	public boolean isChildSelectable(int groupPosition,
    			int childPosition) {
    		// TODO Auto-generated method stub
    		return true;
    	}
	}
	
	public class recommendBoardAsyncTask extends AsyncTask{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			recommendboardlist = new ArrayList<HashMap<String, ArrayList<String[]>>>();	
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url = "http://bbs.fudan.edu.cn/bbs/sec";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements sections = doc.getElementsByTag("sec");
				for(Element section: sections){
					HashMap<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
					
					//add section info to overrall arraylist
					ArrayList<String[]> sectionarray = new ArrayList<String[]>();
					String[] sectionstring = new String[2];
					sectionstring[0] = section.attr("id");
					sectionstring[1] = section.attr("desc");
					sectionarray.add(sectionstring);
					map.put("secinfo", sectionarray);			
					
					// add each board info to overrall arraylist
					Elements boards = section.getElementsByAttribute("name");
					ArrayList<String[]> boardarray = new ArrayList<String[]>();
					for(Element board: boards){
						String[] boardstring = new String[2];
						boardstring[0] = board.attr("name");
						boardstring[1] = board.attr("desc");
						boardarray.add(boardstring);
					}
					map.put("boards", boardarray);
					recommendboardlist.add(map);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
		
	public class onItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent,
				View view, int position, long id) {
			// TODO Auto-generated method stub
			
		}
	}

}
