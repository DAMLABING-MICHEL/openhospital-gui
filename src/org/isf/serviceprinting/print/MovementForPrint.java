package org.isf.serviceprinting.print;

import java.util.Date;
import java.util.GregorianCalendar;

import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.model.MissingMedical;

/**
 * 		   @author mwithi
 * 
 */
public class MovementForPrint implements Comparable<MovementForPrint>{

	private String ward;
	private Date date;
	private String description;
	private String medical;
	private double quantity;
	
	public MovementForPrint(Movement mov) {
		
		super();
		this.ward = mov.getWard().getDescription();
		this.date = removeTime(mov.getDate());
		this.description = mov.getMedical().getDescription();
		this.medical = mov.getMedical().getDescriptionWithCode();
		this.quantity = mov.getQuantity();
	}
	
	public MovementForPrint(MissingMedical mov) {
		super();
		this.ward = mov.getWard().getDescription();
		this.date = removeTime(mov.getDate());
		this.description = mov.getMedical().getDescription();
		this.medical = mov.getMedical().getDescriptionWithCode();
		this.quantity = mov.getQty();
	}

	public String getWard() {
		return ward;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getMedical() {
		return medical;
	}

	public double getQuantity() {
		return quantity;
	}

	public String toString(){
		return medical;
	}

	@Override
	public int compareTo(MovementForPrint o) {
		return this.date.compareTo(o.getDate());
	}
	
	private Date removeTime(GregorianCalendar date) {
		GregorianCalendar newDate = date;
		date.set(GregorianCalendar.HOUR_OF_DAY, 0);
		date.set(GregorianCalendar.MINUTE, 0);
		date.set(GregorianCalendar.SECOND, 0);
		return newDate.getTime();
	}
}
