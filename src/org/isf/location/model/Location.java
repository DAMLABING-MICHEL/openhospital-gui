/**
 * Location.java - 05/mar/2013
 */
package org.isf.location.model;

/**
 * @author Mwithi
 *
 */
public class Location {
	
	private String City;
	
	private String Address;
	
	public Location() {}

	/**
	 * @param city
	 * @param address
	 */
	public Location(String city, String address) {
		super();
		City = city;
		Address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return City;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		City = city;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return Address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		Address = address;
	}
}
