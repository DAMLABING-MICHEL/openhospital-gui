/**
 * @(#) Ward.java
 * 21-jan-2006
 */
package org.isf.ward.model;

/**
 * Pure Model Ward (Hospital wards): represents a ward
 * 
 * @author bob
 * 
 */
public class Ward {

    private String code;

    private String description;

    private String telephone;

    private String fax;

    private String email;

    private Integer beds;

    private Integer nurs;

    private Integer docs;
    
    private boolean isPharmacy;
    
    private boolean isFemale;
    
    private boolean isFree; //WOLISSO
    
    private Integer minimumMonths; //WOLISSO
    
    private Integer maximumMonths; //WOLISSO

    private Integer lock;

    public Integer getBeds() {
        return this.beds;
    }

   /**
    * 
    * @param code
    * @param description
    * @param telephone
    * @param fax
    * @param email
    * @param beds
    * @param nurs
    * @param docs
    * @param isPharmacy
    * @param isFemale
    * @param isFree
    * @param minAge
    * @param maxAge
    * @param lock
    */
    public Ward(String code, String description, String telephone, String fax,
			String email, Integer beds, Integer nurs, Integer docs,
			boolean isPharmacy, boolean isFemale, boolean isFree, Integer minAge, Integer maxAge, Integer lock) {
		super();
		this.code = code;
		this.description = description;
		this.telephone = telephone;
		this.fax = fax;
		this.email = email;
		this.beds = beds;
		this.nurs = nurs;
		this.docs = docs;
		this.isPharmacy = isPharmacy;
		this.isFemale = isFemale;
		this.isFree = isFree;
		this.minimumMonths = minAge;
		this.maximumMonths = maxAge;
		this.lock = lock;
	}
    
    /**
     * 
     * @param code
     * @param description
     * @param telephone
     * @param fax
     * @param email
     * @param beds
     * @param nurs
     * @param docs
     * @param minAge
     * @param lock
     */
    public Ward(String code, String description, String telephone, String fax,
			String email, Integer beds, Integer nurs, Integer docs, Integer minAge, Integer maxAge,
			Integer lock) {
		super();
		this.code = code;
		this.description = description;
		this.telephone = telephone;
		this.fax = fax;
		this.email = email;
		this.beds = beds;
		this.nurs = nurs;
		this.docs = docs;
		this.isPharmacy = false;
		this.isFemale = false;
		this.isFree = false;
		this.minimumMonths = minAge;
		this.maximumMonths = maxAge;
		this.lock = lock;
	}

	public void setBeds(Integer aBeds) {
        this.beds = aBeds;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String aCode) {
        this.code = aCode;
    }

    public Integer getDocs() {
        return this.docs;
    }

    public void setDocs(Integer aDocs) {
        this.docs = aDocs;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String aEmail) {
        this.email = aEmail;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String aFax) {
        this.fax = aFax;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String aDescription) {
        this.description = aDescription;
    }

    public Integer getNurs() {
        return this.nurs;
    }

    public void setNurs(Integer aNurs) {
        this.nurs = aNurs;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String aTelephone) {
        this.telephone = aTelephone;
    }

    public Integer getLock() {
        return this.lock;
    }

    public void setLock(Integer aLock) {
        this.lock = aLock;
    }

    public boolean isPharmacy() {
		return isPharmacy;
	}

	public void setPharmacy(boolean isPharmacy) {
		this.isPharmacy = isPharmacy;
	}

	public boolean isFemale() {
		return isFemale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}
	
	public Integer getMinimumMonths() {
		return minimumMonths;
	}

	public void setMinAge(Integer minAge) {
		this.minimumMonths = minAge;
	}
	
	public Integer getMaximumMonths() {
		return maximumMonths;
	}

	public void setMaxAge(Integer maxAge) {
		this.maximumMonths = maxAge;
	}

	public boolean equals(Object anObject) {
		return (anObject == null) || !(anObject instanceof Ward) ? false
				: (getCode().equals(((Ward) anObject).getCode())
						&& getDescription().equalsIgnoreCase(
								((Ward) anObject).getDescription())
						&& getTelephone().equalsIgnoreCase(
								((Ward) anObject).getTelephone()) && (getFax()
						.equalsIgnoreCase(((Ward) anObject).getFax()) && (getEmail()
						.equalsIgnoreCase(((Ward) anObject).getEmail()) && (getBeds()
						.equals(((Ward) anObject).getBeds()) && (getNurs()
						.equals(((Ward) anObject).getNurs()) && (getDocs()
						.equals(((Ward) anObject).getDocs()) && (getLock()
						.equals(((Ward) anObject).getLock()))))))));
	}

    public String toString() {
        return getDescription();
    }
}
