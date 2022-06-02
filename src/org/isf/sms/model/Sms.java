package org.isf.sms.model;

import java.util.Date;

/**
 * @author Mwithi
 */
public class Sms {

	private int smsId;
	
	private Date smsDate;
	
	private Date smsDateSched;

	private String smsNumber;
	
	private String smsText;
	
	private Date smsDateSent;
	
	private String smsUser;
	
	private String module;
	
	private String moduleID;

	public Sms() {
	}

	public Sms(Date smsDateSched, String smsNumber, String smsText, String smsUser) {
		this.smsDateSched = smsDateSched;
		this.smsNumber = smsNumber;
		this.smsText = smsText;
		this.smsUser = smsUser;
	}

	public Sms(int smsId, Date smsDate, Date smsDateSched, String smsNumber, String smsText, Date smsDateSent, String smsUser,
			String module, String moduleID) {
		this.smsId = smsId;
		this.smsDate = smsDate;
		this.smsDateSched = smsDateSched;
		this.smsNumber = smsNumber;
		this.smsText = smsText;
		this.smsDateSent = smsDateSent;
		this.smsUser = smsUser;
		this.module = module;
		this.moduleID = moduleID;
	}

	public int getSmsId() {
		return this.smsId;
	}

	public void setSmsId(int smsId) {
		this.smsId = smsId;
	}

	public Date getSmsDate() {
		return this.smsDate;
	}

	public void setSmsDate(Date smsDate) {
		this.smsDate = smsDate;
	}

	public Date getSmsDateSched() {
		return this.smsDateSched;
	}

	public void setSmsDateSched(Date smsDateSched) {
		this.smsDateSched = smsDateSched;
	}

	public String getSmsNumber() {
		return this.smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public String getSmsText() {
		return this.smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public Date getSmsDateSent() {
		return this.smsDateSent;
	}

	public void setSmsDateSent(Date smsDateSent) {
		this.smsDateSent = smsDateSent;
	}

	public String getSmsUser() {
		return this.smsUser;
	}

	public void setSmsUser(String smsUser) {
		this.smsUser = smsUser;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

}
