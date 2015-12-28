package cn.com.vapk.vstore.client.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import tw.com.sti.store.api.android.util.Logger;

import cn.com.vapk.vstore.client.util.AsyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.widget.ImageView;

public final class BitmapUtils {

	private static final Logger L = Logger.getLogger(BitmapUtils.class);
	private static final int TIMEOUT_TIME = 10 * 1000; // millisecond
	private static final boolean TIMEOUT = true;

	public final static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		if (TIMEOUT) {
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_TIME);
			HttpConnectionParams.setSoTimeout(params, TIMEOUT_TIME);
		}
		final HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				L.w("ImageDownloader Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new FlushedInputStream(entity.getContent());
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			if (Logger.ERROR)
				L.e("ImageDownloader Error while retrieving bitmap from " + url,
						e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	public final static class BitmapDownloaderTask extends
			AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	/**
	 * A bug in the previous versions of BitmapFactory.decodeStream may prevent
	 * this code from working over a slow connection. Decode a new
	 * FlushedInputStream(inputStream) instead to fix the problem. Here is the
	 * implementation of this helper class:
	 * 
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	final public static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

}
