package net.smart.appstore.server.api.support;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.vstore.appserver.util.Util;

public class StoreClientLogFactory {
	@SuppressWarnings("unchecked")
	public static List parse(String logs) throws Throwable{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		LogParseHandler handler=new LogParseHandler();
		SAXParser parser = factory.newSAXParser();
		parser.parse(new ByteArrayInputStream(logs.getBytes("UTF8")), handler);
		return handler.getObjects();
	}
	static class LogParseHandler extends DefaultHandler{
	    private StringBuilder builder;
	    @SuppressWarnings("unchecked")
		private List rets=new ArrayList();
	    private Object current;
	    @SuppressWarnings("unchecked")
		public List getObjects(){
	    	return rets;
	    }
		@Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	        super.characters(ch, start, length);
	        builder.append(ch, start, length);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	        super.endElement(uri, localName, name);
	        if(name == null || name.trim().length() == 0)
	        	name =  localName;
	        if (this.current != null){
	        	if (name != null){		       
	         		String builderString = null;
	         		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        		if (builder != null){
	        			builderString = builder.toString().trim();
	        		}
	        		if (builderString != null){
	        			if(current instanceof AppInstallLog){
		        			if (name.equalsIgnoreCase("token")){
		  		            	((AppInstallLog)current).setToken(builderString);
		  		            }else if (name.equalsIgnoreCase("iccid")){
		  		            	((AppInstallLog)current).setIccid(builderString);
		  		            }else if (name.equalsIgnoreCase("pkgId")){
		  		            	((AppInstallLog)current).setPkgId(builderString);
		  		            }else if (name.equalsIgnoreCase("version")){
		  		            	int ret=0;
		  		            	try{
		  		            		ret=new Integer(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppInstallLog)current).setVersion(ret);
		  		            }else if (name.equalsIgnoreCase("userId")){
		  		            	((AppInstallLog)current).setUserId(builderString);
		  		            }else if (name.equalsIgnoreCase("actionTime")){
		  		            	Date systime=null;
		  		            	try{
		  		            		systime=dateFormat.parse(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppInstallLog)current).setActionTime(systime);
		  		            }else if (name.equalsIgnoreCase("downType")){
		  		            	((AppInstallLog)current).setDownType(builderString);
		  		            }else if (name.equalsIgnoreCase("installRet")){
		  		            	int ret=-1;
		  		            	try{
		  		            		ret=new Integer(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppInstallLog)current).setInstallRet(ret);
		  		            }else if (name.equalsIgnoreCase("phoneno")){
		  		            	((AppInstallLog)current).setPhoneno(builderString);
		  		            }else if (name.equalsIgnoreCase("failCode")){
		  		            	((AppInstallLog)current).setFailCode(Util.parseInt(builderString));
		  		            }else if (name.equalsIgnoreCase("downloadId")){
		  		            	BigDecimal downloadId=null;
		  		            	try{
		  		            		downloadId=new BigDecimal(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppInstallLog)current).setDownloadId(downloadId);
		  		            }else if (name.equalsIgnoreCase("reasonId")){
		  		            	int ret=0;
		  		            	try{
		  		            		ret=new Integer(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppInstallLog)current).setReasonId(ret);
		  		            }else if (name.equalsIgnoreCase("installApp")){
		  		            	rets.add(current);
		  		            }
	        			}else if(current instanceof AppBehaviorLog){
		        			if (name.equalsIgnoreCase("token")){
		  		            	((AppBehaviorLog)current).setToken(builderString);
		  		            }else if (name.equalsIgnoreCase("iccid")){
		  		            	((AppBehaviorLog)current).setIccid(builderString);
		  		            }else if (name.equalsIgnoreCase("pkgId")){
		  		            	((AppBehaviorLog)current).setPkgId(builderString);
		  		            }else if (name.equalsIgnoreCase("version")){
		  		            	int ret=0;
		  		            	try{
		  		            		ret=new Integer(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppBehaviorLog)current).setVersion(ret);
		  		            }else if (name.equalsIgnoreCase("userId")){
		  		            	((AppBehaviorLog)current).setUserId(builderString);
		  		            }else if (name.equalsIgnoreCase("openTime")){
		  		            	Date systime=null;
		  		            	try{
		  		            		systime=dateFormat.parse(builderString);
		  		            	}catch(Exception e){}
		  		            	((AppBehaviorLog)current).setActionTime(systime);
		  		            }else if (name.equalsIgnoreCase("behavior")){
		  		            	rets.add(current);
		  		            }
	        			}else if(current instanceof ClientBehaviorLog){
		        			if (name.equalsIgnoreCase("token")){
		  		            	((ClientBehaviorLog)current).setToken(builderString);
		  		            }else if (name.equalsIgnoreCase("iccid")){
		  		            	((ClientBehaviorLog)current).setIccid(builderString);
		  		            }else if (name.equalsIgnoreCase("pkgId")){
		  		            	((ClientBehaviorLog)current).setPkgId(builderString);
		  		            }else if (name.equalsIgnoreCase("version")){
		  		            	int ret=0;
		  		            	try{
		  		            		ret=new Integer(builderString);
		  		            	}catch(Exception e){}
		  		            	((ClientBehaviorLog)current).setVersion(ret);
		  		            }else if (name.equalsIgnoreCase("userId")){
		  		            	((ClientBehaviorLog)current).setUserId(builderString);
		  		            }else if (name.equalsIgnoreCase("openTime")){
		  		            	Date systime=null;
		  		            	try{
		  		            		systime=dateFormat.parse(builderString);
		  		            	}catch(Exception e){}
		  		            	((ClientBehaviorLog)current).setActionTime(systime);
		  		            }else if (name.equalsIgnoreCase("clientBehavior")){
		  		            	rets.add(current);
		  		            }
	        			}
	        		}
	        	}		          		            
	            builder.setLength(0);    
	        }
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void startDocument() throws SAXException {
	        super.startDocument();
	        rets = new ArrayList();
	        builder = new StringBuilder();
	    }

	    public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
	        super.startElement(uri, localName, name, attributes);
	        if(name == null || name.trim().length() == 0)
	        	name =  localName;
	        if (name!= null && name.equalsIgnoreCase("installApp")){
	            this.current = new AppInstallLog();
	        }
	        if (name!= null && name.equalsIgnoreCase("behavior")){
	            this.current = new AppBehaviorLog();
	        }
	        if (name!= null && name.equalsIgnoreCase("clientBehavior")){
	            this.current = new ClientBehaviorLog();
	        }
	    }		
	}
}
