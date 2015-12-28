package cn.vstore.appserver.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class LicneseHandler extends DefaultHandler {

	private List<License> licenses = new ArrayList<License>();
	private License currentLicense = new License();
	private StringBuilder builder;

	public License getCurrentLicense() {
		return currentLicense;
	}

	public List<License> getLicenses() {
		return this.licenses;
	}

	public void setLicenses(List<License> licenses) {
		this.licenses = licenses;
	}

	public void setCurrentLicense(License currentLicense) {
		this.currentLicense = currentLicense;
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
		if (this.currentLicense != null) {
			if (name != null) {
				String builderString = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				if (builder != null) {
					builderString = builder.toString().trim();
				}
				if (builderString != null) {
					if (name.equalsIgnoreCase("appPackageId")) {
						currentLicense.setAppPackageId(builder.toString()
								.trim());
					} else if (name.equalsIgnoreCase("durationEnd")) {
						try {
							currentLicense.setDurationEnd(dateFormat
									.parse(builderString));
						} catch (ParseException e) {

						}
					} else if (name.equalsIgnoreCase("durationStart")) {
						try {
							currentLicense.setDurationStart(dateFormat
									.parse(builderString));
						} catch (ParseException e) {
						}
					} else if (name.equalsIgnoreCase("IMEI")) {
						currentLicense.setIMEI(builderString);
					} else if (name.equalsIgnoreCase("IMSI")) {
						currentLicense.setIMSI(builderString);
					} else if (name.equalsIgnoreCase("licenseCreateDate")) {
						try {
							currentLicense.setLicenseCreateDate(dateFormat
									.parse(builderString));
						} catch (ParseException e) {

						}
					} else if (name.equalsIgnoreCase("licenseId")) {
						currentLicense.setLicenseId(builderString);
					} else if (name.equalsIgnoreCase("licenseType")) {
						currentLicense.setLicenseType(Integer
								.parseInt(builderString));
					} else if (name.equalsIgnoreCase("licensedTimes")) {
						currentLicense.setLicensedTimes(Integer
								.parseInt(builderString));
					} else if (name.equalsIgnoreCase("macAddress")) {
						currentLicense.setMacAddress(builderString);
					} else if (name.equalsIgnoreCase("storeClientID")) {
						currentLicense.setStoreClientID(builderString);
					} else if (name.equalsIgnoreCase("userId")) {
						currentLicense.setUserId(builderString);
					} else if (name.equalsIgnoreCase("licensedByIMEI")) {
						if (builderString.equals("true")) {
							currentLicense.setLicensedByIMEI(true);
						} else if (builderString.equals("false")) {
							currentLicense.setLicensedByIMEI(false);
						}
					} else if (name.equalsIgnoreCase("licensedByIMSI")) {
						if (builderString.equals("true")) {
							currentLicense.setLicensedByIMSI(true);
						} else if (builderString.equals("false")) {
							currentLicense.setLicensedByIMSI(false);
						}
					} else if (name.equalsIgnoreCase("licensedByMacAddress")) {
						if (builderString.equals("true")) {
							currentLicense.setLicensedByMacAddress(true);
						} else if (builderString.equals("false")) {
							currentLicense.setLicensedByMacAddress(false);
						}
					} else if (name.equalsIgnoreCase("licensedByUser")) {
						if (builderString.equals("true")) {
							currentLicense.setLicensedByUser(true);
						} else if (builderString.equals("false")) {
							currentLicense.setLicensedByUser(false);
						}
					} else if (name.equalsIgnoreCase("license")) {
						licenses.add(currentLicense);
					}
				}
			}
			builder.setLength(0);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		licenses = new ArrayList<License>();
		builder = new StringBuilder();
	}

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
        if(name == null || name.trim().length() == 0)
        	name =  localName;
		if (name != null && name.equalsIgnoreCase("license")) {
			this.currentLicense = new License();
		}
	}

}
