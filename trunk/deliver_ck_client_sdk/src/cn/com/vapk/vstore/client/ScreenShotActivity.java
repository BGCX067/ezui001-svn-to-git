package cn.com.vapk.vstore.client;

import java.util.HashMap;

import tw.com.sti.store.api.android.util.Logger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils;

/*
 * Finish activity for:
 * without screen shot
 */
public class ScreenShotActivity extends Activity implements OnClickListener {

	private static final Logger L = Logger.getLogger(ScreenShotActivity.class);

	private ImageView pictureView, yesDot, previousBtn, nextBtn;
	private LinearLayout dotLayout;
	private View buttonsView;

	private int picSize = 0;
	private int pageId;
	private HashMap<Integer, Bitmap> pictures;
	private String[] imageURL;
	private boolean displayNow = true;
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_shot);

		Intent intent = getIntent();
		imageURL = intent
				.getStringArrayExtra(ActionController.EXTRA_SCREEN_SHOTS);
		pageId = intent.getIntExtra(
				ActionController.EXTRA_SCREEN_SHOT_POSITION, 0);
		picSize = imageURL.length;
		pictures = new HashMap<Integer, Bitmap>();

		if (Logger.DEBUG) {
			L.d("Init pageId: " + pageId);
			L.d("Picture size: " + picSize);
		}

		loadPicture();

		pictureView = (ImageView) findViewById(R.id.dataImage);
		buttonsView = findViewById(R.id.screen_buttons_layout);
		previousBtn = (ImageView) findViewById(R.id.preview_previous);
		nextBtn = (ImageView) findViewById(R.id.preview_next);
		yesDot = (ImageView) findViewById(R.id.preview_dot_y);
		dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
//		Button backBtn = (Button) findViewById(R.id.backBtn);

		yesDot.setVisibility(View.VISIBLE);
		previousBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
//		backBtn.setOnClickListener(this);

		setBtnVisibilty();
		drawDot();

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	/*
	 * Load pictures
	 */
	private void loadPicture() {
		if (picSize < 0)
			finish();

		new AsyncTask<Void, Void, Void>() {
			Bitmap icon;
			int loadPage = 0;

			@Override
			protected Void doInBackground(Void... params) {
				for (int i = 0; i < picSize; i++) {
					if (pictures.get(pageId) == null && displayNow) {
						loadPage = pageId;
						displayNow = false;
						icon = BitmapUtils.downloadBitmap(imageURL[loadPage]);
						pictures.put(loadPage, icon);

						if (Logger.DEBUG)
							L.d("loading loadPage: " + loadPage
									+ ", loading pageId: " + pageId);
						return null;
					}
					if (pictures.get(i) == null) {
						icon = BitmapUtils.downloadBitmap(imageURL[i]);
						pictures.put(i, icon);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				displayImage();
				if (pictures.size() != (picSize))
					loadPicture();
			}
		}.execute();
	}

	private void displayImage() {
		if (pictures.get(pageId) == null) {
			displayNow = true;
			pictureView.setVisibility(View.GONE);
			UI.startProgressing(this);
		} else {
			UI.stopProgressing(this);
			pictureView.setVisibility(View.VISIBLE);
			pictureView.setImageBitmap(pictures.get(pageId));
		}
	}

	/*
	 * Dynamic drawing dot image
	 */
	private void drawDot() {
		dotLayout.removeAllViews();

		if (picSize == 1) {
			return;
		}

		for (int i = 0; i < picSize; i++) {
			if (i == pageId) {
				dotLayout.addView(yesDot);
			} else {
				ImageView noDot = new ImageView(this);
				noDot.setImageResource(R.drawable.screen_shot_dot_normal);
				dotLayout.addView(noDot);
			}
		}
	}

	/*
	 * forward, back and picture behavior
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.preview_previous:
			if (pageId != 0) {
				pageId--;
			}
			drawDot();
			break;
		case R.id.preview_next:
			if (pageId != picSize - 1) {
				pageId++;
			}
			drawDot();
			break;
//		case R.id.backBtn:
//			finish();
		}
		setBtnVisibilty();
		displayImage();

		if (Logger.DEBUG)
			L.d("My pageId in onClick(): " + pageId);
	}

	private void setBtnVisibilty() {
		if (picSize == 1) {
			previousBtn.setVisibility(View.INVISIBLE);
			nextBtn.setVisibility(View.INVISIBLE);
		} else if (pageId == 0) {
			previousBtn.setVisibility(View.INVISIBLE);
			nextBtn.setVisibility(View.VISIBLE);
		} else if (pageId == picSize - 1) {
			nextBtn.setVisibility(View.INVISIBLE);
			previousBtn.setVisibility(View.VISIBLE);
		} else {
			nextBtn.setVisibility(View.VISIBLE);
			previousBtn.setVisibility(View.VISIBLE);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (buttonsView.getVisibility() == 8) {
				buttonsView.setVisibility(View.VISIBLE);
			} else {
				buttonsView.setVisibility(View.GONE);
			}
		}
		return true;
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		finish();
		return false;
	}
}