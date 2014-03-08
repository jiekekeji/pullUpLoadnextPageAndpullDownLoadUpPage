package com.example.mylistview;

import java.util.LinkedList;

import com.example.mylistview.PullUpAndDownListView.RefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements RefreshListener {

	private LinkedList< String> mData;
	private PullUpAndDownListView mListView;
	private MyAdapter mAdapter;

	static int b=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mData = new LinkedList<>();
		
		for (int i = 0; i <50; i++) {
			mData.add(""+i);
		}
		
		mListView = (PullUpAndDownListView) findViewById(R.id.myListView);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(1);
		
		mListView.setRefreshListener(this);
		
	}
	
	
	
	class MyAdapter extends BaseAdapter{

		private Context context;
		
		public MyAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context=context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView mTextView=new TextView(context);
			mTextView.setText(mData.get(position));
			
			return mTextView;
		}
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void pullUp() {
		// TODO Auto-generated method stub
		Log.i("xxxxxxxx", "pullUp");
		new MyAsyncTask().execute();
	}

	@Override
	public void pullDown() {
		// TODO Auto-generated method stub
		Log.i("xxxxxxxx", "pullDown");
		new MyAsyncTask().execute();
	}
	
	class MyAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getData();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAdapter.notifyDataSetChanged();
			mListView.setRefreshComplete();
			mListView.setSelection(1);
		}
		
	}
	
	private synchronized void getData(){
		mData.clear();
		for (int i = 0; i < 50; i++) {
			b=b+i;
			mData.add(b+"");
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
