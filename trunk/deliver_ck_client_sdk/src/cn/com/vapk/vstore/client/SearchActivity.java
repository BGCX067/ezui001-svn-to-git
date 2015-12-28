package cn.com.vapk.vstore.client;

import java.util.ArrayList;
import java.util.List;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.search.SearchSuggestionProvider;

/*
 * 1. extend the old version provider
 * 2. using our search activity in all this application when using search button
 * 3. close auto complete text and limit enter button
 */
public class SearchActivity extends ListActivity implements OnClickListener,
		TextWatcher {
	private static final Logger L = Logger.getLogger(SearchActivity.class);

	private EditText searchText;

	private String searchStr;
	private SearchRecentSuggestions suggestions;
	private Uri uri;
	private Cursor resultCursor;
	private SimpleCursorAdapter adapter;
	private CloseClientReceiver closeClientReceiver;
	private final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		searchText = (EditText) findViewById(R.id.search_text);
		ImageButton searchBtn = (ImageButton) findViewById(R.id.search_btn);

		TextWatcher textWatcher = this;
		searchText.addTextChangedListener(textWatcher);
		searchBtn.setOnClickListener(this);
		ImageButton speakButton = (ImageButton) findViewById(R.id.voice_search_btn);

		UI.bindTabOnClickListener(this, this);

		// 因為Acer輸入法的Bug，所以原本關掉AUTO_COMPLETE，現在要打開
		// searchText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

		suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionProvider.AUTHORITY,
				SearchSuggestionProvider.MODE);

		uri = SearchSuggestionProvider.CONTENT_URI;
		if (Logger.DEBUG)
			L.d("uri: " + uri);

		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			speakButton.setOnClickListener(this);
		} else {
			speakButton.setVisibility(View.GONE);
			if (Logger.DEBUG)
				L.d("Recognizer not present");
		}

		doSuggestion();
		clearData(false);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	public void onClick(View v) {
		if (UI.handleTabOnClickEvent(this, v))
			return;

		if (v.getId() == R.id.search_btn) {
			storeData(); // Search button action
		} else if (v.getId() == R.id.voice_search_btn) {
			startVoiceRecognitionActivity();
		}
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			searchText.setText(matches.get(0));

			if (Logger.DEBUG) {
				for (int i = 0; i < matches.size(); i++) {
					L.d("Speech recognition result " + i + " : "
							+ matches.get(i));
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			storeData();
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			ActionController.search(this);
			return false;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		searchStr = searchText.getText().toString();
		if (Logger.DEBUG)
			L.d("searchStr changed to: " + searchStr);

		doSuggestion();
	}

	private void storeData() {
		if (LangUtils.isBlank(searchStr)) {
			DialogUtils.createFinishActivityAlertDialog(this,
					getString(R.string.search_alert_message), false).show();
			return;
		}
		suggestions.saveRecentQuery(searchStr, null);
		ActionController.searchResult(this, searchStr);

		if (Logger.DEBUG)
			L.d("Store data: " + searchStr);
		searchText.setText("");
	}

	private void clearData(boolean isClear) {
		if (Logger.DEBUG)
			L.d("Ready to clear search key words. isClear: " + isClear);

		if (isClear) {
			suggestions.clearHistory();
		}
	}

	private void doSuggestion() {
		resultCursor = managedQuery(uri, null, null,
				new String[] { searchStr }, null);

		if (Logger.DEBUG)
			L.d("Get: " + resultCursor.getCount() + " items");

		if (adapter == null) {
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_1, resultCursor,
					new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
					new int[] { android.R.id.text1 });
			this.setListAdapter(adapter);
		} else {
			adapter.changeCursor(resultCursor);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (resultCursor.moveToPosition(position))
			searchText.setText(resultCursor.getString(1));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);
		if(!ActionController.loginFlag){
			menu.removeItem(R.id.menu_logout);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (!UI.handleMenuOnSelectEvent(this, item)) {
			if (Logger.DEBUG)
				throw new RuntimeException("handleMenuOnSelectEvent false.");
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}
}