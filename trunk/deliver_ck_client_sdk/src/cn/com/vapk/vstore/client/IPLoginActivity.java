package cn.com.vapk.vstore.client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.Credential;
import tw.com.sti.store.api.vo.IPLoginRet;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class IPLoginActivity extends Activity {

    private static final Logger L = Logger.getLogger(IPLoginActivity.class);

    private ApiInvoker<IPLoginRet> apiInvoker;
    private String ip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ip = getIP();

        if (Logger.DEBUG)
            L.d("IP: " + ip);

        if (LangUtils.isBlank(ip)) {
            if (Logger.WARN)
                L.w("ipLoginAble IP is blank!");
            ActionController.ipLoginFail(this);
            finish();
            return;
        }

        setContentView(R.layout.ip_login);
        requestIPLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiInvoker != null)
            apiInvoker.stop();
    }

    private String getIP() {
        Enumeration<NetworkInterface> nis = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            if (Logger.ERROR)
                L.e("IPLoginActivity.getIP()", e);
            return null;
        }

        if (nis == null) {
            if (Logger.WARN)
                L.w("NetworkInterface.getNetworkInterfaces() return null");
            return null;
        }

        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            if ("rmnet0".equals(ni.getName()) || "rmnet0".equals(ni.getDisplayName())) {
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                if (ias != null && ias.hasMoreElements()) {
                    InetAddress ia = ias.nextElement();
                    return ia.getHostAddress();
                }
            }

            // shit Samsung Tablet
            if ("pdp0".equals(ni.getName()) || "pdp0".equals(ni.getDisplayName())) {
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                if (ias != null && ias.hasMoreElements()) {
                    InetAddress ia = ias.nextElement();
                    return ia.getHostAddress();
                }
            }
        }

        return null;
    }

    private void requestIPLogin() {
        if (!NetworkUtils.isNetworkOpen(this)) {
            showDialog(DialogUtils.DLG_NO_NETWORK);
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                AndroidApiService apiService = AndroidApiService.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
                apiInvoker = apiService.IPLogin(ip);
                apiInvoker.invoke();
                return null;
            }

            protected void onPostExecute(Void result) {
                responseIPLogin();
            };

        }.execute();
    }

    private void responseIPLogin() {
        UI.stopProgressing(this);

        if (apiInvoker.isStop()) {
            if (Logger.DEBUG)
                L.d("apiInvoker.isStop()");
            return;
        }

        if (apiInvoker.isFail()) {
            if (Logger.DEBUG)
                L.d("apiInvoker.isFail()");
            showDialog(DialogUtils.DLG_CONN_TO_SERVER_FAIL);
            return;
        }

        IPLoginRet loginRet = apiInvoker.getRet();
        if (loginRet.isSuccess()) {
            boolean autoLogin = true;
            Credential credential = loginRet.getCredential();
            if (credential == null) {
                if (Logger.DEBUG)
                    throw new RuntimeException("IPLogin's Credential is null.");
                ActionController.ipLoginFail(this);
                finish();
                return;
            }
            AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
            apiService.saveCredential(this, credential);
            boolean afse = loginRet.isAppFilterSettingEnable();
            ActionController.loginSuccess(this, autoLogin, afse);
            finish();
            return;
        }

        if (loginRet.isFail()) {
            ActionController.ipLoginFail(this);
            finish();
            return;
        }

        if (loginRet.isHasNewClient()) {
            if (Logger.DEBUG)
                L.d("appsRet.isHasNewClient()");
            showDialog(DialogUtils.DLG_API_RET_NEW_CLIENT);
            return;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DialogUtils.DLG_NO_NETWORK: {
            return DialogUtils.createNoNetworkAlertDialog(this);
        }
        case DialogUtils.DLG_CONN_TO_SERVER_FAIL: {
            return DialogUtils.createConnectionToServerFailAlertDialog(this);
        }
        case DialogUtils.DLG_API_RET_NEW_CLIENT: {
            return DialogUtils.createNewClientAlertDialog(this, apiInvoker.getRet().getNewClientInfo());
        }
        }
        return null;
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }
}