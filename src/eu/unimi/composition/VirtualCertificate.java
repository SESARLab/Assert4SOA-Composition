package eu.unimi.composition;

public class VirtualCertificate {
	private String property;
	private String model;
	private String test;
	
	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the test
	 */
	public String getTest() {
		return test;
	}
	/**
	 * @param test the test to set
	 */
	public void setTest(String test) {
		this.test = test;
	}
	/**
	 * @param property The security property
	 * @param model The Model Type
	 * @param test The Test Type
	 */
	public VirtualCertificate(String property, String model, String test) {
		this.property = property;
		this.model = model;
		this.test = test;
	}
	
	
}
