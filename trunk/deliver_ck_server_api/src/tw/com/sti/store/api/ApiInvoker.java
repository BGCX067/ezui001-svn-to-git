package tw.com.sti.store.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.EasySSLSocketFactory;

/**
 * @version $Id: ApiInvoker.java 7417 2011-03-02 04:05:34Z yhwang $
 */
public class ApiInvoker<T> {
    private static final int STATUS_SUCCESS = 0x01;
    private static final int STATUS_INVOKE_FAIL = 0x02;
    private static final int STATUS_HTTP_RESPONSE_STATUS_CODE_NOT_200 = 0x04;
    private static final int STATUS_RET_DATA_INVALID = 0x08;
    private static final int STATUS_INVOKE_STOP = 0x10;

    private int status;
    private boolean invoking;
    private boolean connecting;
    private boolean stop;

    private ApiDataParseHandler<T> parser;
    private T t;
    private String url;
    private List<NameValuePair> nvps;

    private HttpPost post;
    private Configuration config;
    private Logger L =Logger.getLogger(ApiInvoker.class);

    public ApiInvoker(Configuration config,ApiDataParseHandler<T> parser, String url,
            List<NameValuePair> nvps) {
    	this.config=config;
        if (parser == null) {
            throw new NullPointerException("parser is null");
        }
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        if (nvps == null) {
            throw new NullPointerException("List<NameValuePair> is null");
        }
        this.parser = parser;
        this.url = url;
        this.nvps = nvps;
    }

    public boolean isFail() {
        if (Logger.DEBUG && invoking)
            throw new RuntimeException("invokeing now.");

        // STATUS_SUCCESS會發生 t == null的問題，bug 待修復
        if (t == null) {
            return true;
        }
        return status == STATUS_INVOKE_FAIL
                || status == STATUS_RET_DATA_INVALID
                || status == STATUS_HTTP_RESPONSE_STATUS_CODE_NOT_200;
    }

    public boolean isSuccess() {
        if (Logger.DEBUG && invoking)
            throw new RuntimeException("invokeing now.");

        return status == STATUS_SUCCESS;
    }

    public void invoke() {
        if (nvps == null || invoking) {
            if (Logger.DEBUG)
                throw new RuntimeException("can't invoke twice.");
            return;
        }

        try {
            invoking = true;

            post = new HttpPost(url);
            try {
                post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                nvps = null;
            } catch (UnsupportedEncodingException e) {
                status = STATUS_INVOKE_FAIL;
                return;
            }

            HttpResponse resp = null;
            HttpClient http = createHttpClient();
            stop = false;
            long startTime = System.currentTimeMillis();
            connecting = true;
            try {
                resp = http.execute(post);
            } catch (ClientProtocolException e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() ClientProtocolException, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            } catch (IOException e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() IOException, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            } catch (Exception e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() Exception, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            }

            final int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                status = STATUS_HTTP_RESPONSE_STATUS_CODE_NOT_200;
                if (Logger.WARN)
                    L.w("HttpResponse statusCode: " + statusCode);
                if (Logger.DEBUG) {
                    float responseTime = (System.currentTimeMillis() - startTime) / 1000.0f;
                    L.d("Response Time: " + responseTime + " sec");
                }
                return;
            }

            String respData = null;
            HttpEntity entity = resp.getEntity();
            try {
                if (entity == null) {
                    status = STATUS_INVOKE_FAIL;
                    return;
                }
                respData = EntityUtils.toString(entity);
                connecting = false;
            } catch (ParseException e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() ParseException, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            } catch (IOException e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() IOException, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            } catch (Exception e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() Exception, abort: "
                            + post.isAborted(), e);
                if (post.isAborted()) {
                    status = STATUS_INVOKE_STOP;
                    return;
                }
                status = STATUS_INVOKE_FAIL;
                post.abort();
                return;
            } finally {
                if (entity != null)
                    entity.consumeContent();
            }

            if (Logger.DEBUG) {
                float responseTime = (System.currentTimeMillis() - startTime) / 1000.0f;
                L.d("Response Time: " + responseTime + " sec");
                L.d(url + " response:\n" + respData);
            }

            try {
                t = parser.parseRet(respData);
                if (t == null) {
                    status = STATUS_RET_DATA_INVALID;
                    return;
                }
            } catch (ApiDataParseException e) {
                if (Logger.ERROR)
                    L.e("ApiInvoker invoke() ApiDataParseException", e);
                status = STATUS_RET_DATA_INVALID;
                return;
            }
            status = STATUS_SUCCESS;
        } catch (Throwable e) {
            if (Logger.ERROR)
                L.e("ApiInvoker invoke() Throwable", e);
            status = STATUS_INVOKE_FAIL;
        } finally {
            if (Logger.DEBUG)
                L.d("status: " + status);
            connecting = false;
            invoking = false;
            parser = null;
            post = null;
            nvps = null;
        }
    }

    public boolean stop() {
        if (connecting && post != null) {
            post.abort();
            stop = true;
            status = STATUS_INVOKE_STOP;
            if (Logger.DEBUG) {
                L.d(url + " doStop");
            }
            return true;
        }
        return false;
    }

    public boolean isStop() {
        return stop;
    }

    public T getRet() {
        return t;
    }

    private HttpClient createHttpClient() {
        if (url.indexOf("https://") == 0) {
            SchemeRegistry schemeRegistry = new SchemeRegistry();// http scheme
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), config.getApiHttpPort()));
            schemeRegistry.register(new Scheme("https",
                    new EasySSLSocketFactory(), config.getApiHttpsPort()));

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, config.getApiTimeout());
            HttpConnectionParams.setSoTimeout(params, config.getApiTimeout());
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            ClientConnectionManager cm = new ThreadSafeClientConnManager(
                    params, schemeRegistry);

            HttpClient httpClient = new DefaultHttpClient(cm, params);
            return httpClient;
        }

        DefaultHttpClient http = new DefaultHttpClient();
        HttpParams my_httpParams = http.getParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, config.getApiTimeout());
        HttpConnectionParams.setSoTimeout(my_httpParams, config.getApiTimeout());
        return http;
    }

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

}
