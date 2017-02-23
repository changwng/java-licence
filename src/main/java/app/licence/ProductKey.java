package app.licence;

import java.io.Serializable;

public class ProductKey implements Serializable {

	private static final long serialVersionUID = -6247120642160007636L;

	private String licence;
	private String expiryDate;
	private String macAddress;
	
	public ProductKey() {
		// TODO Auto-generated constructor stub
	}

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public String toString() {
		return "ProductKey [licence=" + licence + ", expiryDate=" + expiryDate + ", macAddress=" + macAddress + "]";
	}

}
