package cn.com.vapk.vstore.client.util;

import java.util.concurrent.ConcurrentHashMap;

import cn.com.vapk.vstore.client.util.AsyncTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class ConcurrentIntentService extends Service {
	protected abstract void onHandleIntent(Intent intent);

	private boolean m_bRedelivery;
	private ConcurrentHashMap<Intent, MyMyAsyncTask> m_mapIntent2MyAsyncTask;

	public ConcurrentIntentService() {
		m_mapIntent2MyAsyncTask = new ConcurrentHashMap<Intent, MyMyAsyncTask>(32);
	}

	public void setIntentRedelivery(boolean enabled) {
		m_bRedelivery = enabled;
	}

	public void cancel() {
		for (MyMyAsyncTask task : m_mapIntent2MyAsyncTask.values()) {
			task.cancel(true);
		}
		m_mapIntent2MyAsyncTask.clear();
		stopSelf();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (m_mapIntent2MyAsyncTask.containsKey(intent)) {
			return;
		}

		MyMyAsyncTask task = new MyMyAsyncTask();
		m_mapIntent2MyAsyncTask.put(intent, task);
		task.execute(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		return m_bRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// ///////////////////////////////////////////////////////////
	private class MyMyAsyncTask extends AsyncTask<Intent, Void, Void> {
		protected Void doInBackground(Intent... its) {
			final int nCount = its.length;
			for (int i = 0; i < nCount; i++) {
				Intent it = its[i];

				m_mapIntent2MyAsyncTask.remove(it);
				onHandleIntent(it);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_mapIntent2MyAsyncTask.isEmpty())
				stopSelf();
		}

		@Override
		protected void onCancelled() {
			if (m_mapIntent2MyAsyncTask.isEmpty())
				stopSelf();
		}
	}
}