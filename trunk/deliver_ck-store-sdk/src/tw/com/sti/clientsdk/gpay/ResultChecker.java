package tw.com.sti.clientsdk.gpay;

import org.json.JSONObject;

public class ResultChecker
{
	String mContent;
	public ResultChecker(String content)
	{
		this.mContent = content;
	}
	
	String getSuccess()
	{
		String success = null;
		
		try
		{
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			success = objResult.getString("success");
			success = success.replace("\"", "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return success;
	}
	
	boolean checkSign()
	{
		boolean bChecked = false;
		
		try
		{
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			int iSignContentEnd = result.indexOf("&sign_type=");
			String signContent = result.substring(0, iSignContentEnd);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			String signType = objResult.getString("sign_type");
			signType = signType.replace("\"", "");
			
			String sign = objResult.getString("sign");
			sign = sign.replace("\"", "");
			
			if( signType.equalsIgnoreCase("RSA") )
			{
				bChecked = Rsa.doCheck(signContent, sign, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUXxtN4khfOVuaCJiCB02PoFj1UCWeH5iARypSfKAthR8ifYP19hwzwBxxN6dQmEnsNd8rxdLUKbu4G3yHP5GTrGt1xniP4OIFeYP2TuUBclv6DS+5 ycaccOiyZBvLyp6PS3pOmYYovez/RoMclSZ49RiJqckgm0vUpn+oJ0mlPQIDAQAB");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return bChecked;
	}
	
	public boolean isPayOk()
	{
		boolean isPayOk = false;
		
		String success = getSuccess();
		if( success.equalsIgnoreCase("true") && checkSign() )
			isPayOk = true;
		
		return isPayOk;
	}
}