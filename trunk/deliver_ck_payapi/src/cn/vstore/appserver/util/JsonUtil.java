package cn.vstore.appserver.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
public class JsonUtil {
	final ClassLoader classLoader = getClass().getClassLoader();
	/**
	 * 从一个JSON 对象字符格式中得到一个java对象 　
	 * 
	 * @param jsonString
	 * @param pojoCalss
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getObject4JsonString(String jsonString, Class pojoCalss) {

		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		pojo = JSONObject.toBean(jsonObject, pojoCalss);
		return pojo;
	}

	/**
	 * 从json HASH表达式中获取一个map，改map支持嵌套功能
	 * 
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getMap4Json(String jsonString) {

		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator keyIter = jsonObject.keys();
		String key;
		Object value;
		Map valueMap = new HashMap();

		while (keyIter.hasNext()){
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;
	}

	/**
	 * 
	 * 从json数组中得到相应java数组
	 * 
	 * @param jsonString
	 * 
	 * @return
	 */

	public static Object[] getObjectArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();
	}

	/**
	 * 
	 * 从json对象集合表达式中得到一个java对象列表
	 * 
	 * @param jsonString
	 * 
	 * @param pojoClass
	 * 
	 * @return
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getList4Json(String jsonString, Class pojoClass) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List list = new ArrayList();

		for (int i = 0; i < jsonArray.size(); i++) {
			
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject, pojoClass);
			list.add(pojoValue);
		}
		return list;
	}

	/**
	 * 
	 * 从json数组中解析出java字符串数组
	 * 
	 * @param jsonString
	 * 
	 * @return
	 */

	public static String[] getStringArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/**
	 * 
	 * 从json数组中解析出javaLong型对象数组
	 * 
	 * @param jsonString
	 * 
	 * @return
	 */

	public static Long[] getLongArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] longArray = new Long[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			longArray[i] = jsonArray.getLong(i);
		}
		return longArray;
	}

	/**
	 * 
	 * 从json数组中解析出java Integer型对象数组
	 * 
	 * @param jsonString
	 * 
	 * @return
	 */

	public static Integer[] getIntegerArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] integerArray = new Integer[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			integerArray[i] = jsonArray.getInt(i);
		}
		return integerArray;
	}


	/**
	 * 
	 * 从json数组中解析出java Integer型对象数组
	 * 
	 * @param jsonString
	 * 
	 * @return
	 */

	public static Double[] getDoubleArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] doubleArray = new Double[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {

			doubleArray[i] = jsonArray.getDouble(i);
		}
		return doubleArray;
	}

	/**
	 * 
	 * 将java对象转换成json字符串
	 * 
	 * @param javaObj
	 * 
	 * @return
	 */

	public static String getJsonString4JavaPOJO(Object javaObj) {

		JSONObject json;
		json = JSONObject.fromObject(javaObj);
		return json.toString();

	}

	/**
	 * 将JSON字符串转换成JSONObject
	 * @param jsonStr
	 * @return
	 */
	public static JSONObject fromJsonStr2JsonObj(String jsonStr){
		
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		return jsonObject;
	}

	/**
	 * 创建JSONObject对象
	 * @return
	 */
	private static JSONObject createJSONObject() {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "kevin");
		jsonObject.put("desc", new Integer(100));
		return jsonObject;
	}
	
	/**
	 * 将JSONObject对象转换成以"[]"包围的字符串以便做后期处理
	 * @param jsonObject
	 * @return
	 */
	public static String JsonObj2JsonStr(JSONObject jsonObject){
		
		StringBuffer stringBuffer = new StringBuffer();
		String jsonStr=jsonObject.toString();
		stringBuffer.append("[").append(jsonStr).append("]");
		return stringBuffer.toString();
	}

	/**
	 * 将JSONObject字符串转换成以"[]"包围的字符串以便做后期处理
	 * @param jsonObject
	 * @return
	 */
	public static String JsonStr2JsonStr(String jsonStr){
		
		StringBuffer stringBuffer = new StringBuffer();
		//String jsonStr=jsonObject.toString();
		stringBuffer.append("[").append(jsonStr).append("]");
		return stringBuffer.toString();
	}
	/**
	 * Json字符串转换为List<?>
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<?> JsonStr2List(String jsonStr, Class clazz) {
		
		List<?> list = null; 
		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		list = (List<?>) JSONArray.toCollection(jsonArray, clazz);
		return list;
	}
	
	/**
	 * Json字符串转换为List<?>
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<?> JsonStr2List(String jsonStr, Class clazz, int start, int end) {
		
		List<?> list = null; 
		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		list = (List<?>) JSONArray.toCollection(jsonArray, clazz);
		return list;
	}
	
	
	 /**   
     *   将json 数组转换为Map 对象   
     * @param jsonString   
     * @return   
     */   
    public static Map<String, Object> getMap(String jsonString)    
    {    
      JSONObject jsonObject;    
      try   
      {    
       jsonObject =  JSONObject.fromObject(jsonString);
       @SuppressWarnings("unchecked")    
       Iterator<String> keyIter = jsonObject.keys();    
       String key;    
       Object value;    
       Map<String, Object> valueMap = new HashMap<String, Object>();    
       while (keyIter.hasNext())    
       {    
        key = (String) keyIter.next();    
        value = jsonObject.get(key);    
        valueMap.put(key, value);    
       }    
       return valueMap;    
      }    
      catch (JSONException e)    
      {    
       e.printStackTrace();    
      }    
      return null;    
    }    
   
    /**   
     * 把json 转换为 ArrayList 形式   
     * @return   
     */   
    public static List<Map<String, Object>> getList(String jsonString)    
    {    
      List<Map<String, Object>> list = null;    
      try   
      {    
       JSONArray jsonArray = JSONArray.fromObject(jsonString);    
       JSONObject jsonObject;    
        list = new ArrayList<Map<String, Object>>();    
       for (int i = 0; i < jsonArray.size(); i++)    
       {    
        jsonObject = jsonArray.getJSONObject(i);    
        list.add(getMap(jsonObject.toString()));    
       }    
      }    
      catch (Exception e)    
      {    
       e.printStackTrace();    
      }    
      return list;
    }
	public static void main(String[] args) throws IOException {
		//String str = "[{\"code\":\"0001\",\"desc\":\"miaoshu\"}]";
		
//		String ss=JsonObj2JsonStr(JsonUtil.createJSONObject());
//		System.out.println("JsonUtil.createJSONObject()====>"+JsonUtil.createJSONObject());
//		System.out.println("s====>"+ss);
//		boolean b=JsonUtil.createJSONObject().isEmpty();
//		boolean c=JsonUtil.createJSONObject().isNullObject();
//		System.out.println("b======>"+b+" ||||||||| c===>"+c);
//		
//				JSONArray jsonArr = new JSONArray();   
//		        JSONObject obj = new JSONObject();   
//		        obj.put("a", "a") ;
//		        jsonArr.add(obj) ;
		        //jsonArr.add(new JSONObject().put("b", "b")) ;   
		        //jsonArr.add(new JSONObject().put("c", "c")) ;   
		        //jsonArr.add(0, new JSONObject().put("d", "d")) ;   
//		        System.out.println(jsonArr.toString() );  
		
//		String temp = "[{\"aid\":\"1\",\"times\":\"2\"},{\"aid\":\"3\",\"times\":\"4\"},{\"aid\":\"5\",\"times\":\"6\"}]";    
//        List<Map<String, Object>> lm = JsonUtil.getList(temp);    
//        for(int i=0;i<lm.size();i++){
//            System.out.print(lm.get(i).get("aid"));
//            System.out.print(lm.get(i).get("times"));
//        }    
			URL url = Thread.currentThread().getContextClassLoader().getResource("100016_SignKey.pub");
			String fullFileName = url.toString();
			System.out.println(System.getProperty("user.dir"));
			
			File file= new File(".\\src");
			String srcPath = file.getCanonicalPath();
			System.out.println(srcPath);
			
		//System.out.println("JSONObject=====>"+JsonUtil.createJSONObject());
		//System.out.println("JSONObject to jsonStr=====>"+JsonUtil.JsonObj2JsonStr(JsonUtil.createJSONObject()));
	}
}
