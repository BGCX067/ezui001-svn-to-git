package cn.com.vapk.vstore.client;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import cn.com.vapk.vstore.client.R;

public class CreateShortcutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Intent.ACTION_CREATE_SHORTCUT.equals(getIntent().getAction())) {
			finish();
			return;
		}

		Intent launcherIntent = new Intent(this, ASC.class);
		launcherIntent.setAction("cn.com.vapk.vstore.client.action.SHORTCUT");

		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
		ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));

		setResult(RESULT_OK, intent);
		finish();
	}

}