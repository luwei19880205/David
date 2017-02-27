package com.songming.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 视频播放页面
 * 
 * @author lw 2016-07-08
 */
public class MainActivity extends Activity implements OnClickListener {

	private List<VideoDemandModel> list = new ArrayList<VideoDemandModel>();
	private ListView listView;
	private ProgramAdapter adapter;
	private MediaPlayer player;
	private SurfaceView mSurfaceView;
	private ImageView btn_play;
	private ImageView btn_move;
	private SeekBar seekbar;
	private boolean isFullScreen = false;
	private ProgressBar progressBar;
	private LinearLayout ll_play;
	private Timer timer;
	private VideoDemandModel video;
	private int curPosition;

	private Callback callback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			// 停止进度条timer
			if (timer != null) {
				timer.cancel();
			}

			if (player != null && player.isPlaying()) {
				// 保存播放进度
				curPosition = player.getCurrentPosition();
				// 停止播放器，释放资源
				player.stop();
				player.release();
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			player = new MediaPlayer();
			// 设置播放场景
			player.setDisplay(holder);
			// 设置声音流
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);

			// 有效解决因home键或锁屏导致surfaceView销毁从而引起播放中断问题
			if (curPosition > 0) {
				// 如果存在上次播放进度，则从上次位置开始播放
				play();
			}

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// 更新播放场景
			player.setDisplay(holder);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViews();

		initViews();

	}

	private void findViews() {

		btn_move = (ImageView) findViewById(R.id.btn_move);
		btn_play = (ImageView) findViewById(R.id.btn_play);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		ll_play = (LinearLayout) findViewById(R.id.ll_play);
		listView = (ListView) findViewById(R.id.listview);
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
		mSurfaceView.getHolder().addCallback(callback);
	}

	private void initViews() {

		list = getVideoInfo();
		adapter = new ProgramAdapter(this, list);
		listView.setAdapter(adapter);

		btn_move.setOnClickListener(this);
		btn_play.setOnClickListener(this);
		mSurfaceView.setOnClickListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				video = list.get(arg2);
				handler.sendEmptyMessageDelayed(1, 500);// 延迟500ms等待播放器装载完毕后开始播放
			}
		});

		// 播放进度条监听
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// 定位到拖动进度位置
				player.seekTo(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// 播放
				play();
				break;

			case 2:
				// 使用定时器来刷新播放进度条
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						seekbar.setProgress(player.getCurrentPosition());

					}
				}, 0, 50);

				break;

			case 3:
				// 隐藏播放栏
				ll_play.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 播放视频文件
	 */
	private void play() {
		try {
			player.reset();
			// 加载视频资源
			player.setDataSource("http://218.207.213.107/PLTV/88888888/224/3221225812/index.m3u8?rrsip=218.207.213.107&icpid=&accounttype=1&limitflux=-1&limitdur=-1&accountinfo=:20170217094330,zhiboapk,113.246.106.195,20170217094330,10000100000000050000000000923037,CDCD49FACC13AABFCF7E39A6AB9ACEA1,-1,0,3,-1,,2,,,,2,END&servicetype=1");
//			player.setDataSource("http://112.5.183.248:1948/cctv1.m3u8?channel-id=wasusyt&Contentid=5068253659616166748");
//			player.setDataSource(video.getPath());
			// 缓冲资源
			player.prepareAsync();

			// 首次加载资源显示缓冲进度条
			ll_play.setVisibility(View.VISIBLE);
			btn_play.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);

			// 播放器资源缓冲监听
			player.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					// 设置进度条上限
					seekbar.setMax(player.getDuration());
					seekbar.setProgress(0);

					btn_play.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					btn_play.setImageResource(R.drawable.play_off);

					if (handler.hasMessages(3)) {
						handler.removeMessages(3);
					}
					handler.sendEmptyMessageDelayed(3, 5000);
					// 播放
					mp.start();
					// 从指定位置开始播放
					player.seekTo(curPosition);
					curPosition = 0;

					// 启动进度条同步线程
					handler.sendEmptyMessage(2);
				}
			});

			// 播放完毕监听
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					btn_play.setImageResource(R.drawable.play_on);
					// 播放完毕，退出全屏
					exitFullScreen();
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard视频资源
	 * 
	 * @return
	 */
	private List<VideoDemandModel> getVideoInfo() {

		List<VideoDemandModel> videoList = new ArrayList<VideoDemandModel>();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ContentResolver resolver = this.getContentResolver();
			Cursor cursor = resolver.query(Video.Media.EXTERNAL_CONTENT_URI,
					null, null, null, null);
			while (cursor.moveToNext()) {
				String title = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
				String path = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
				long time = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
				VideoDemandModel videoDemandModel = new VideoDemandModel(title,
						path, size, time);
				videoList.add(videoDemandModel);
			}
		}

		return videoList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_move:

			if (!isFullScreen) {
				// 切换到全屏
				ChangeToFullScreen();
				isFullScreen = true;
			} else {
				// 退出全屏
				exitFullScreen();
				isFullScreen = false;
			}

			break;

		case R.id.btn_play:

			if (player.isPlaying()) {
				btn_play.setImageResource(R.drawable.play_on);
				player.pause();
			} else {
				btn_play.setImageResource(R.drawable.play_off);
				player.start();
			}

			break;

		case R.id.mSurfaceView:
			// 触碰播放界面，应当弹出播放菜单栏
			if (ll_play.getVisibility() != View.VISIBLE) {
				ll_play.setVisibility(View.VISIBLE);

				if (handler.hasMessages(3)) {
					handler.removeMessages(3);
				}
				// 设置播放菜单栏5秒后自动消失
				handler.sendEmptyMessageDelayed(3, 5000);
			}

			break;
		}
	}

	/**
	 * 退出全屏
	 */
	private void exitFullScreen() {
		// 调整页面为竖屏显示
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		int width = getWindowManager().getDefaultDisplay().getWidth();
		// 获取屏幕密度像素
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int height = (int) (250 * density);
		LayoutParams params = new LayoutParams(width, height);
		mSurfaceView.setLayoutParams(params);
	}

	/**
	 * 切换到全屏
	 */
	private void ChangeToFullScreen() {

		int wid = player.getVideoWidth();
		int hig = player.getVideoHeight();
		// 根据视频的属性调整其显示的模式，以确实是横屏还是竖屏
		if (wid > hig) {
			// 横屏显示
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else {
			// 竖屏显示
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		
		// 方式一 ：
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mSurfaceViewWidth = dm.widthPixels;
		int mSurfaceViewHeight = dm.heightPixels;
		if (wid > hig) {
			// 竖屏录制的视频，调节其上下的空余
			int w = mSurfaceViewHeight * wid / hig;
			int margin = (mSurfaceViewWidth - w) / 2;
			Log.d("aa", "margin:" + margin);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(margin, 0, margin, 0);
			mSurfaceView.setLayoutParams(lp);
		} else {
			// 横屏录制的视频，调节其左右的空余
			int h = mSurfaceViewWidth * hig / wid;
			int margin = (mSurfaceViewHeight - h) / 2;
			Log.d("aa", "margin:" + margin);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(0, margin, 0, margin);
			mSurfaceView.setLayoutParams(lp);
		}

		// 方式二：
		// int width = getWindowManager().getDefaultDisplay().getWidth();
		// int height = getWindowManager().getDefaultDisplay().getHeight();
		// LayoutParams params = new LayoutParams(width, height);
		// params.addRule(RelativeLayout.CENTER_IN_PARENT);
		// mSurfaceView.setLayoutParams(params);
	}

}
