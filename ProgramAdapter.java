package com.songming.player;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgramAdapter extends BaseAdapter {

	private Context context;
	private List<VideoDemandModel> list;

	public ProgramAdapter(Context context, List<VideoDemandModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {

		HoldView holder;
		if (arg1 == null) {
			arg1 = LayoutInflater.from(context).inflate(R.layout.program_item,
					null);
			holder = new HoldView();
			holder.address = (TextView) arg1.findViewById(R.id.address);
			holder.icon = (ImageView) arg1.findViewById(R.id.icon);
			holder.title = (TextView) arg1.findViewById(R.id.title);
			arg1.setTag(holder);
		} else {
			holder = (HoldView) arg1.getTag();
		}

		holder.title.setText(list.get(arg0).getTitle());
		
		String path = list.get(arg0).getPath();
		holder.address.setText(path);

		holder.icon.setTag(path);
		// 显示视频缩略图
		MyBobAsynctack myBobAsynctack = new MyBobAsynctack(holder.icon, path);
		myBobAsynctack.execute("0");

		return arg1;
	}

	class HoldView {
		TextView title;
		TextView address;
		ImageView icon;
	}

	/**
	 * 异步加载缩略图片，解决listview卡顿
	 * @author Administrator
	 *
	 */
	class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
		private ImageView imgView;
		private String path;

		public MyBobAsynctack(ImageView imageView, String path) {
			this.imgView = imageView;
			this.path = path;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			//获取缩略图
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path,
					Thumbnails.MINI_KIND);
			
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			 if (imgView.getTag().equals(path)) {
				 // 通过 Tag可以绑定 图片地址和imageView，这是解决Listview加载图片错位的解决办法之一
				 imgView.setImageBitmap(bitmap);
			 }
		}
	}

}
