package org.isf.priceslist.model;

import java.util.Comparator;

/**
 * List model: represent a List
 * @author alex
 *
 */
public class PriceList {
    
	private int id;
    private String code;
    private String name;
    private String description;
    private String currency;
	
    public PriceList(int id, String code, String name, String description, String currency) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.currency = currency;
	}

	public int getId() {
		return id;
	}
	
    public void setId(int id) {
		this.id = id;
	}
	
    public String getCode() {
		return code;
	}
	
    public void setCode(String code) {
		this.code = code;
	}
	
    public String getName() {
		return name;
	}
	
    public void setName(String name) {
		this.name = name;
	}
	
    public String getDescription() {
		return description;
	}
	
    public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return name;
	}   
    
	public static Comparator<PriceList> NameComparator = new Comparator<PriceList>() {

		public int compare(PriceList s1, PriceList s2) {
			String priceList1 = s1.getName().toUpperCase();
			String priceList2 = s2.getName().toUpperCase();

			// ascending order
			return priceList1.compareTo(priceList2);

			// descending order
			// return StudentName2.compareTo(StudentName1);
		}
	};
}
