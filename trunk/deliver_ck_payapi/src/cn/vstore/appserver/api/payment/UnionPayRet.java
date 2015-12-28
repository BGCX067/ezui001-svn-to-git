package cn.vstore.appserver.api.payment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class UnionPayRet {
	private String transType;
	private String merchantId;
	private String merchantOrderId;
	private String merchantOrderAmt;
	private String settleDate;
	private String setlAmt;
	private String setlCurrency;
	private String converRate;
	private String cupsQid;
	private String cupsTraceNum;
	private String cupsTraceTime;
	private String cupsRespCode;
	private String cupsRespDesc;
	private String sign;
	
	private Double amount;
	private int tradeStatus;
	private int paymentType = 2;

	public static UnionPayRet parseXml(String xml) {
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		return parseXml(is);
	}

	public static UnionPayRet parseXml(InputStream xml) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		UnionPayRetHandler handler = new UnionPayRetHandler();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(xml, handler);
		} catch (Throwable e) {
			e.printStackTrace();

		}
		if (handler.getObjects().size() > 0) {
			return handler.getObjects().get(0);
		} else {
			return null;
		}
	}
	
	static class UnionPayRetHandler extends DefaultHandler {

		private StringBuilder builder;
		private List<UnionPayRet> rets = new ArrayList<UnionPayRet>();

		private UnionPayRet current = new UnionPayRet();

		public List<UnionPayRet> getObjects() {
			return rets;
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
			if (name == null || name.trim().length() == 0)
				name = localName;

			if (this.current != null) {
				if (name != null) {
					String builderString = null;
					if (builder != null) {
						builderString = builder.toString().trim();
					}
					if (builderString != null) {
						if (name.equalsIgnoreCase("transType")) {
							current.setTransType(builderString);
						} else if (name.equalsIgnoreCase("merchantId")) {
							current.setMerchantId(builderString);
						} else if (name.equalsIgnoreCase("merchantOrderId")) {
							current.setMerchantOrderId(builderString);
						} else if (name.equalsIgnoreCase("merchantOrderAmt")) {
							current.setMerchantOrderAmt(builderString);
							try {
								current.setAmount(Double.parseDouble(builderString)/100);
							} catch (Exception e) {
								current.setAmount(0D);
							}
						} else if (name.equalsIgnoreCase("settleDate")) {
							current.setSettleDate(builderString);
						} else if (name.equalsIgnoreCase("setlAmt")) {
							current.setSetlAmt(builderString);
						} else if (name.equalsIgnoreCase("setlCurrency")) {
							current.setSetlCurrency(builderString);
						} else if (name.equalsIgnoreCase("converRate")) {
							current.setConverRate(builderString);
						} else if (name.equalsIgnoreCase("cupsQid")) {
							current.setCupsQid(builderString);
						} else if (name.equalsIgnoreCase("cupsTraceNum")) {
							current.setCupsTraceNum(builderString);
						} else if (name.equalsIgnoreCase("cupsTraceTime")) {
							current.setCupsTraceTime(builderString);
						} else if (name.equalsIgnoreCase("cupsRespCode")) {
							current.setCupsRespCode(builderString);
							if (builderString.equals("00"))
								current.setTradeStatus(2);
//							if (builderString.equals("WAIT_BUYER_PAY"))
//								current.setTrade_status(1);
//							else if (builderString.equals("TRADE_FINISHED"))
//								current.setTrade_status(2);
//							else if (builderString.equals("TRADE_SUCCESS"))
//								current.setTrade_status(2);
							else
								current.setTradeStatus(3);
						} else if (name.equalsIgnoreCase("cupsRespDesc")) {
							current.setCupsRespDesc(builderString);
						} else if (name.equalsIgnoreCase("sign")) {
							current.setSign(builderString.replaceAll("\\s", "+"));
						} else if (name.equalsIgnoreCase("upomp")) {
							rets.add(current);
						}
					}
				}
				builder.setLength(0);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			rets = new ArrayList<UnionPayRet>();
			builder = new StringBuilder();
		}

		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (name == null || name.trim().length() == 0)
				name = localName;
			if (name != null && name.equalsIgnoreCase("upomp")) {
				this.current = new UnionPayRet();
			}
		}

	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public String getMerchantOrderAmt() {
		return merchantOrderAmt;
	}

	public void setMerchantOrderAmt(String merchantOrderAmt) {
		this.merchantOrderAmt = merchantOrderAmt;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getSetlAmt() {
		return setlAmt;
	}

	public void setSetlAmt(String setlAmt) {
		this.setlAmt = setlAmt;
	}

	public String getSetlCurrency() {
		return setlCurrency;
	}

	public void setSetlCurrency(String setlCurrency) {
		this.setlCurrency = setlCurrency;
	}

	public String getConverRate() {
		return converRate;
	}

	public void setConverRate(String converRate) {
		this.converRate = converRate;
	}

	public String getCupsQid() {
		return cupsQid;
	}

	public void setCupsQid(String cupsQid) {
		this.cupsQid = cupsQid;
	}

	public String getCupsTraceNum() {
		return cupsTraceNum;
	}

	public void setCupsTraceNum(String cupsTraceNum) {
		this.cupsTraceNum = cupsTraceNum;
	}

	public String getCupsTraceTime() {
		return cupsTraceTime;
	}

	public void setCupsTraceTime(String cupsTraceTime) {
		this.cupsTraceTime = cupsTraceTime;
	}

	public String getCupsRespCode() {
		return cupsRespCode;
	}

	public void setCupsRespCode(String cupsRespCode) {
		this.cupsRespCode = cupsRespCode;
	}

	public String getCupsRespDesc() {
		return cupsRespDesc;
	}

	public void setCupsRespDesc(String cupsRespDesc) {
		this.cupsRespDesc = cupsRespDesc;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
}
