package tw.com.sti.clientsdk;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class LicenseTool {

	private static final HashMap<String, String> WIDGET_BROADCAST = new HashMap<String, String>();

	public static final void addWidgetBroadcast(String pkgId, String broadcast) {
		if (broadcast != null && pkgId != null)
			WIDGET_BROADCAST.put(pkgId, broadcast);
	}

	public static final Uri insertLicense(Context context, String pkgId,
			long serverGenId, byte[] data, byte[] sign) {
		String authority=context.getPackageName()+".sdk.provider.StoreProvider";
		Uri puri=Uri.parse("content://" + authority+ "/app/license");
		ContentValues cv = new ContentValues();
		cv.put("packageName", pkgId);
		cv.put("serverGenId", serverGenId);
		cv.put("data", (byte[]) data);
		cv.put("sign", (byte[]) sign);
		Uri uri = context.getContentResolver().insert(
				puri, cv);
		String broadcast = WIDGET_BROADCAST.get(pkgId);
		if (broadcast != null) {
			Intent intent = new Intent(broadcast);
			context.sendBroadcast(intent);
			WIDGET_BROADCAST.remove(pkgId);
		}
		return uri;
	}

	public static final int deleteAllLicense(Context context, String pkgId) {
		String authority=context.getPackageName()+".sdk.provider.StoreProvider";
		Uri puri=Uri.parse("content://" + authority+ "/app/license");
		return context.getContentResolver().delete(puri,
				"packageName=?", new String[] { pkgId });
	}

	public static boolean hasLicense(Context context, String pkgId) {
		String authority=context.getPackageName()+".sdk.provider.StoreProvider";
		Uri puri=Uri.parse("content://" + authority+ "/app/license");
		Cursor cur = context.getContentResolver().query(puri, null,
				"packageName=?", new String[] { pkgId },
				null);
		try {
			if (cur.getCount() == 0) {
				return false;
			} else
				return true;
		} finally {
			cur.close();
		}
	}

}
