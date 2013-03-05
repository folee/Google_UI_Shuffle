package it.vibin.ui.shuffle;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VerticalShuffleAdapter extends BaseAdapter {
	private final String	TAG	= VerticalShuffleAdapter.class.getName();
	private LayoutInflater	inflater;
	private List<String>	list;
	private Context			context;

	public VerticalShuffleAdapter(Context context, List<String> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.episode_item, null);
			holder = new ViewHolder();
			holder.tv_episode = (TextView) convertView.findViewById(R.id.tv_episode);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		final String value = list.get(position);
		holder.tv_episode.setText(value);

		holder.tv_episode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView	tv_episode;
	}

}
