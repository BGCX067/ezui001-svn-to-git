package cn.vstore.appserver.api.payment.danglepay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JavaTest {

    /**
     * @param args
     * @throws Exception
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException, Exception {
        String charSet = "UTF-8"; // 数据编码方式
        String postUrl = "http://189hi.cn/189pay2/yeepaytelecomjava_feed.do";//
        String mid = "382";// 商户ID由当乐分配
        String gid = "1";// 游戏ID由当乐分配
        String sid = "1";// 服务器ID
        String uif = "B16911a2ca9dc89f4077b05c53df1a21cc9d7fd892";// 提交订单用户（用户在厂商系统中的唯一标识，在用户忘记卡号的情况下，可以通过此信息来查询用户的充值记录）
        String utp = "0";// 用户类型，固定值：189用户为 “1”，非189用户为 "0"
        String uip = "127.0.0.1";// 用户IP，必填参数
        String eif = "3821118092734";// 扩展信息，商户根据自己需求填写，支付完成后原样返回
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = "20121015160545";//sdf.format(new Date());
        String merchantkey = "sr8fnmXt";// 商户密钥，由当乐告知
        String desKey = "c3I4Zm5tWHQ=";
        String amount = "50";
        String cardno = "0101001207430162809"; // 卡号"12745150287347434"
        String cardpwd = "100704562511274917";// 密码"151681397403006325"
        cardno = DES.encode(cardno, desKey).replaceAll("\r\n", ""); // 对卡号进行 DES 加密
        cardpwd = DES.encode(cardpwd, desKey).replaceAll("\r\n", ""); // 对密码进行 DES 加密


        StringBuilder sb=new StringBuilder();
        // 拼接MD5加密数据
        sb.append("mid=").append(mid);
        sb.append("&gid=").append(gid);
        sb.append("&sid=").append(sid);
        sb.append("&uif=").append(uif);
        sb.append("&utp=").append(utp);
        sb.append("&uip=").append(uip);
        sb.append("&eif=").append(eif);
        sb.append("&cardno=").append(cardno);// 在MD5时是不对卡号和密码进行UTF-8编码的，
        sb.append("&cardpwd=").append(cardpwd);
        sb.append("&amount=").append(amount);
        sb.append("&timestamp=").append(timestamp);
        sb.append("&merchantkey=").append(merchantkey);
        System.out.println("需要加密的拼接字符串===="+sb.toString());
        String verstr = MD5.MD5Encode(sb.toString()).toLowerCase();
        System.out.println("拼接字符串加密后为===="+verstr);
        // 拼提交订单数据
        sb=new StringBuilder();
        sb.append("mid=").append(mid);
        sb.append("&gid=").append(gid);
        sb.append("&sid=").append(sid);
        sb.append("&uif=").append(URLEncoder.encode(uif, charSet));
        sb.append("&utp=").append(utp);
        sb.append("&uip=").append(uip);
        sb.append("&eif=").append(eif);
        sb.append("&cardno=").append(URLEncoder.encode(cardno, charSet));// 在编成URL时才要对卡号和密码进行UTF-8编码。
        sb.append("&cardpwd=").append(URLEncoder.encode(cardpwd, charSet));
        sb.append("&amount=").append(amount);
        sb.append("&timestamp=").append(timestamp);
        sb.append("&verstring=").append(verstr);
        String postData = sb.toString();
        System.out.println("需要提交的订单数据==="+postData);
        String res = httpPost(postUrl, postData, charSet);
        System.out.println("提交订单后返回===="+res);
        if(null==res){
            return;
        }
        // 解释提交订单结果
        String []tmp=res.trim().split("&");
        Map<String,String> resMap = new HashMap<String,String>();
        for(String t:tmp){
            //System.out.println(t);
            String [] tt =t.split("=");
            if(tt.length==2){
                resMap.put(tt[0], tt[1]);
            }
        }
        System.out.println("提交订单返回字符串解析后的result为==="+resMap.get("result")+"====非1即为失败"); // 当result=1时提交订单成功，否则是失败的
        if("1".equals(resMap.get("result"))){
            System.out.println("提交订单状态===="+resMap.get("msg")); // 提交订单状态说明
        }
    }

    public static String httpPost(String urlStr, String postData, String charSet) throws Exception {
        PrintWriter writer=null;
        HttpURLConnection httpConn=null;
        String res=null;
        try {
            byte[] data=postData.getBytes(charSet);
            URL url=new URL(urlStr);
            httpConn=(HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Content-Length", String.valueOf(data.length));
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//jdk1.4换成这个,连接超时
            // System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //jdk1.4换成这个,读操作超时
            httpConn.setConnectTimeout(30000);// jdk 1.5换成这个,连接超时
            httpConn.setReadTimeout(30000);// jdk 1.5换成这个,读操作超时
            httpConn.connect();
            OutputStream os=httpConn.getOutputStream();
            os.write(data);
            // 获得响应状态
            int responseCode=httpConn.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode) {
                byte[] buffer=new byte[1024];
                int len=-1;
                InputStream is=httpConn.getInputStream();
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                while((len=is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                res=bos.toString(charSet);
                is.close();
            } else {
                System.err.println(urlStr + " Response Code:" + responseCode);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.err.println(urlStr);
        } finally {
            if(null != writer) {
                writer.close();
            }
            if(null != httpConn) {
                httpConn.disconnect();
            }
        }
        return res;
    }
}
