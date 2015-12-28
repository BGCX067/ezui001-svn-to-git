package tw.com.sti.store.api.vo;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import tw.com.sti.security.util.Base64Coder;

public class LicenseRet {

	private String pkgId;
	private long id;
	private byte[] data;
	private byte[] sign;
	private int licenseType;
	private Date durationEnd;
	private Date durationStart;
	private int priceType;

	public int getPriceType() {
		return priceType;
	}

	public void setPriceType(int priceType) {
		this.priceType = priceType;
	}

	public String getPkgId() {
		return pkgId;
	}

	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}

	public int getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(int licenseType) {
		this.licenseType = licenseType;
	}

	public Date getDurationEnd() {
		return durationEnd;
	}

	public void setDurationEnd(Date durationEnd) {
		this.durationEnd = durationEnd;
	}

	public Date getDurationStart() {
		return durationStart;
	}

	public void setDurationStart(Date durationStart) {
		this.durationStart = durationStart;
	}

	public static LicenseRet parseSingleLicenseXml(InputStream xml,boolean checkOldLicense) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SingleLicenseRetHandler handler = new SingleLicenseRetHandler();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(xml, handler);
		} catch (Exception e) {
		}
		return handler.getObjects();

	}

	public static List<LicenseRet> parseXml(InputStream xml,boolean checkOldLicense) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		LicenseRetHandler handler = new LicenseRetHandler(checkOldLicense);
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(xml, handler);
		} catch (Exception e) {
		}
		return handler.getObjects();

	}

	static class LicenseRetHandler extends DefaultHandler {
		private LicenseRetHandler(boolean checkOldLicense){
			oldLicense = checkOldLicense;
		}
		private StringBuilder builder;
		private List<LicenseRet> rets = new ArrayList<LicenseRet>();
		private LicenseRet current = new LicenseRet();
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private boolean oldLicense;
		public List<LicenseRet> getObjects() {
			return rets;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
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
						if (name.equalsIgnoreCase("pkgid")) {
							current.setPkgId(builderString);
						} else if (name.equalsIgnoreCase("id")) {
							long ret = -1;
							try {
								ret = new Long(builderString);
							} catch (Exception e) {
							}
							current.setId(ret);
						} else if (name.equalsIgnoreCase("data")) {
							current.setData(Base64Coder.decode(builderString.toCharArray()));
						} else if (name.equalsIgnoreCase("sign")) {
							current.setSign(Base64Coder.decode(builderString.toCharArray()));
						} else if (name.equalsIgnoreCase("licensetype")) {
							int ret = -1;
							try {
								ret = new Integer(builderString);
							} catch (Exception e) {
							}
							current.setLicenseType(ret);
						} else if (name.equalsIgnoreCase("durationEnd")) {
							Date d = null;
							try {
								d = dateFormat.parse(builderString);
							} catch (Exception e) {
							}
							current.setDurationEnd(d);
						} else if (name.equalsIgnoreCase("durationStart")) {
							Date d = null;
							try {
								d = dateFormat.parse(builderString);
							} catch (Exception e) {
							}
							current.setDurationStart(d);
						} else if (name.equalsIgnoreCase("license")) {
							rets.add(current);
						} else if (name.equalsIgnoreCase("oldlicense")) {
							rets.add(current);
						}else if (name.equalsIgnoreCase("pricetype")) {
							int ret = -1;
							try {
								ret = new Integer(builderString);
							} catch (Exception e) {
							}
							current.setPriceType(ret);
						} else if (name.equalsIgnoreCase("available")) {
//							int ret = -1;
//							try {
//								ret = new Integer(builderString);
//							} catch (Exception e) {
//							}
//							current.setAvailable(ret);
						}

					}
				}
				builder.setLength(0);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			rets = new ArrayList<LicenseRet>();
			builder = new StringBuilder();
		}

		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (name == null || name.trim().length() == 0)
				name = localName;
			if (name != null && name.equalsIgnoreCase("license")) {
				this.current = new LicenseRet();
			}else if (oldLicense && name != null && name.equalsIgnoreCase("oldlicense")){
				this.current = new LicenseRet();
			}
		}
	}

	static class SingleLicenseRetHandler extends DefaultHandler {
		private StringBuilder builder;
		private LicenseRet rets = null;
		private LicenseRet current = new LicenseRet();

		public LicenseRet getObjects() {
			return rets;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
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
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					if (builder != null) {
						builderString = builder.toString().trim();
					}
					if (builderString != null) {
						if (name.equalsIgnoreCase("pkgid")) {
							current.setPkgId(builderString);
						} else if (name.equalsIgnoreCase("id")) {
							long ret = -1;
							try {
								ret = new Long(builderString);
							} catch (Exception e) {
							}
							current.setId(ret);
						} else if (name.equalsIgnoreCase("data")) {
							current.setData(Base64Coder.decode(builderString.toCharArray()));
						} else if (name.equalsIgnoreCase("sign")) {
							current.setSign(Base64Coder.decode(builderString.toCharArray()));
						} else if (name.equalsIgnoreCase("licensetype")) {
							int ret = -1;
							try {
								ret = new Integer(builderString);
							} catch (Exception e) {
							}
							current.setLicenseType(ret);
						} else if (name.equalsIgnoreCase("durationEnd")) {
							Date d = null;
							try {
								d = dateFormat.parse(builderString);
							} catch (Exception e) {
							}
							current.setDurationEnd(d);
						} else if (name.equalsIgnoreCase("durationStart")) {
							Date d = null;
							try {
								d = dateFormat.parse(builderString);
							} catch (Exception e) {
							}
							current.setDurationStart(d);
						}else if (name.equalsIgnoreCase("pricetype")) {
							int ret = -1;
							try {
								ret = new Integer(builderString);
							} catch (Exception e) {
							}
							current.setPriceType(ret);
						} else if (name.equalsIgnoreCase("license")) {
							rets = current;
							return;
						}

					}
				}
				builder.setLength(0);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			rets = null;
			builder = new StringBuilder();
		}

		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (name == null || name.trim().length() == 0)
				name = localName;
			if (name != null && name.equalsIgnoreCase("license")) {
				this.current = new LicenseRet();
			}
		}
	}

}
