/**
 * 
 */
package org.isf.menu.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * @author Mwithi
 *
 */
public class Session {
	
	private int sessionID;
	private String user;
	private GregorianCalendar login;
	private GregorianCalendar logout;
	
	/**
	 * default constructor
	 */
	public void session() {};
	
	/**
	 * @param sessionID
	 * @param user
	 * @param login
	 * @param logout
	 */
	public Session(int sessionID, String user, GregorianCalendar login, GregorianCalendar logout) {
		super();
		this.sessionID = sessionID;
		this.user = user;
		this.login = login;
		this.logout = logout;
	}

	/**
	 * @return the sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the login
	 */
	public GregorianCalendar getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(GregorianCalendar login) {
		this.login = login;
	}
	/**
	 * @return the logout
	 */
	public GregorianCalendar getLogout() {
		return logout;
	}
	/**
	 * @param logout the logout to set
	 */
	public void setLogout(GregorianCalendar logout) {
		this.logout = logout;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(formatDate(login));
		strBld.append(" -> ");
		strBld.append(formatDate(logout));
		strBld.append(" - ");
		strBld.append(user);
		return strBld.toString();
	}

	private String formatDate(GregorianCalendar calendar) {
		if (calendar == null) return "online";
		GregorianCalendar today = new GregorianCalendar();
		today.set(GregorianCalendar.HOUR_OF_DAY, 0);
		today.set(GregorianCalendar.MINUTE, 0);
		today.set(GregorianCalendar.SECOND, 0);
		SimpleDateFormat sdfToday = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
		if (calendar.before(today)) return sdf.format(calendar.getTime());
		else return sdfToday.format(calendar.getTime());
	}
}
