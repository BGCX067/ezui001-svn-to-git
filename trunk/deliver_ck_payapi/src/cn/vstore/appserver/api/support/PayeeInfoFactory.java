package cn.vstore.appserver.api.support;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.vstore.appserver.api.support.paytype.Gpay;
import cn.vstore.appserver.api.support.paytype.PePay;
import cn.vstore.appserver.api.support.paytype.UnionPay;

public class PayeeInfoFactory {
	public static int getPaymentType(Object payinfo){
		int p=0;
		if(payinfo instanceof Gpay){
			p=1;
		}else if(payinfo instanceof UnionPay){
			p=2;
		}else if(payinfo instanceof PePay) {
			p=3;
		}
		return p;
	}
	
	public static Object parse(String payinfo) throws Throwable{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		ParseHandler handler=new ParseHandler();
		SAXParser parser = factory.newSAXParser();
		parser.parse(new ByteArrayInputStream(payinfo.getBytes("UTF8")), handler);
		return handler.getObject();
	}
	static class ParseHandler extends DefaultHandler{
	    private StringBuilder builder;
	    private Object current;
		public Object getObject(){
	    	return current;
	    }
		@Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	        super.characters(ch, start, length);
	        builder.append(ch, start, length);
	    }

		@Override
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	        super.endElement(uri, localName, name);
	        if(name == null || name.trim().length() == 0)
	        	name =  localName;
	        if (this.current != null){
	        	if (name != null){		       
	         		String builderString = null;
	        		if (builder != null){
	        			builderString = builder.toString().trim();
	        		}
	        		if (builderString != null){
	        			if(current instanceof Gpay){
		        			if (name.equalsIgnoreCase("partner")){
		  		            	((Gpay)current).setPartner(builderString);
		  		            }else if (name.equalsIgnoreCase("seller")){
		  		            	((Gpay)current).setSeller(builderString);
		  		            }
	        			}else if(current instanceof UnionPay){
		        			if (name.equalsIgnoreCase("merchantId")){
		  		            	((UnionPay)current).setMerchantId(builderString);
		  		            }
	        			}
	        		}
	        	}		          		            
	            builder.setLength(0);    
	        }
	    }

		@Override
	    public void startDocument() throws SAXException {
	        super.startDocument();
	        builder = new StringBuilder();
	    }

	    public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
	        super.startElement(uri, localName, name, attributes);
	        if(name == null || name.trim().length() == 0)
	        	name =  localName;
	        if (name!= null && name.equalsIgnoreCase("gpay")){
	            this.current = new Gpay();
	        }
	        if (name!= null && name.equalsIgnoreCase("unionpay")){
	            this.current = new UnionPay();
	        }
	    }		
	}

}
