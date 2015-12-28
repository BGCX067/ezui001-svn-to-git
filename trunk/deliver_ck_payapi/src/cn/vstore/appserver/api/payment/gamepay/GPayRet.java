package cn.vstore.appserver.api.payment.gamepay;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GPayRet {

	String partner;
	String subject;
	String trade_no;
	String buyer_email;
	String gmt_create;
	String seller_id;
	String gmt_payment;
	String seller_email;
	String gmt_close;
	String buyer_id;
	String use_coupon;
	Double total_fee;
	String out_trade_no;
	char is_total_fee_adjust;
	float discount;
	float price;
	int pament_type;
	int quantity;
	int trade_status;

	public static GPayRet parseXml(String xml) {
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		return parseXml(is);
	}

	public static GPayRet parseXml(InputStream xml) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		GPayRetHandler handler = new GPayRetHandler();
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

	static class GPayRetHandler extends DefaultHandler {

		private StringBuilder builder;
		private List<GPayRet> rets = new ArrayList<GPayRet>();

		private GPayRet current = new GPayRet();

		public List<GPayRet> getObjects() {
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
						if (name.equalsIgnoreCase("partner")) {
							current.setPartner(builderString);
						} else if (name.equalsIgnoreCase("discount")) {

							try {
								current.setDiscount(Float.parseFloat(builderString));
							} catch (Exception e) {
								current.setDiscount(0);
							}
						} else if (name.equalsIgnoreCase("payment_type")) {
							try {
								current.setPament_type((Integer.parseInt(builderString)));
							} catch (Exception e) {
								current.setPament_type(0);
							}
						} else if (name.equalsIgnoreCase("subject")) {
							current.setSubject(builderString);
						} else if (name.equalsIgnoreCase("trade_no")) {
							current.setTrade_no(builderString);
						} else if (name.equalsIgnoreCase("buyer_email")) {
							current.setBuyer_email(builderString);
						} else if (name.equalsIgnoreCase("gmt_create")) {
							current.setGmt_create(builderString);
						} else if (name.equalsIgnoreCase("out_trade_no")) {
							if(builderString.indexOf("-") > 0) {
								builderString = builderString.substring(builderString.indexOf("-") + 1);
							}
							current.setOut_trade_no(builderString);
						} else if (name.equalsIgnoreCase("quantity")) {
							current.setQuantity(Integer.parseInt(builderString));
						} else if (name.equalsIgnoreCase("seller_id")) {
							current.setSeller_id(builderString);
						} else if (name.equalsIgnoreCase("trade_status")) {
							if (builderString.equals("WAIT_BUYER_PAY"))
								current.setTrade_status(1);
							else if (builderString.equals("TRADE_FINISHED"))
								current.setTrade_status(2);
							else if (builderString.equals("TRADE_SUCCESS"))
								current.setTrade_status(2);
							else
								current.setTrade_status(3);
						} else if (name.equalsIgnoreCase("is_total_fee_adjust")) {
							current.setIs_total_fee_adjust(builderString.charAt(0));
						} else if (name.equalsIgnoreCase("total_fee")) {
							current.setTotal_fee(Double.parseDouble(builderString));
						} else if (name.equalsIgnoreCase("gmt_payment")) {
							current.setGmt_payment(builderString);
						} else if (name.equalsIgnoreCase("seller_email")) {
							current.setSeller_email(builderString);
						} else if (name.equalsIgnoreCase("gmt_close")) {
							current.setGmt_close(builderString);
						} else if (name.equalsIgnoreCase("price")) {
							try {
								current.setPrice((Float.parseFloat(builderString)));
							} catch (Exception e) {
								current.setDiscount(0);
							}
						} else if (name.equalsIgnoreCase("buyer_id")) {
							current.setBuyer_id(builderString);

						} else if (name.equalsIgnoreCase("use_coupon")) {
							current.setUse_coupon(builderString);

						} else if (name.equalsIgnoreCase("notify")) {
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
			rets = new ArrayList<GPayRet>();
			builder = new StringBuilder();
		}

		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (name == null || name.trim().length() == 0)
				name = localName;
			if (name != null && name.equalsIgnoreCase("notify")) {
				this.current = new GPayRet();
			}
		}

	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public int getPament_type() {
		return pament_type;
	}

	public void setPament_type(int pament_type) {
		this.pament_type = pament_type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getBuyer_email() {
		return buyer_email;
	}

	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getGmt_create() {
		return gmt_create;
	}

	public void setGmt_create(String gmt_create) {
		this.gmt_create = gmt_create;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public int getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(int trade_status) {
		this.trade_status = trade_status;
	}

	public char getIs_total_fee_adjust() {
		return is_total_fee_adjust;
	}

	public void setIs_total_fee_adjust(char is_total_fee_adjust) {
		this.is_total_fee_adjust = is_total_fee_adjust;
	}

	public Double getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(Double total_fee) {
		this.total_fee = total_fee;
	}

	public String getGmt_payment() {
		return gmt_payment;
	}

	public void setGmt_payment(String gmt_payment) {
		this.gmt_payment = gmt_payment;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public String getGmt_close() {
		return gmt_close;
	}

	public void setGmt_close(String gmt_close) {
		this.gmt_close = gmt_close;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getUse_coupon() {
		return use_coupon;
	}

	public void setUse_coupon(String use_coupon) {
		this.use_coupon = use_coupon;
	}
}
