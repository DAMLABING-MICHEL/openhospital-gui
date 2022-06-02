package org.isf.pregnancyexam.model;

import org.isf.pregnancy.model.PregnancyVisit;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCYEXAMRESULT
 * it has a reference to a {@link PregnancyVisit}, another reference
 * to a {@link PregnancyExam} and a outcome as string
 */
public class PregnancyExamResult {
	
	/**
	 */
	private int visitid;
	/**
	 */
	private int pregresid;
	/**
	 */
	private String examCode;
	/**
	 */
	private String outcome;
	
	/**
	 * Initializes a new PregnancyExamResult
	 */
	public PregnancyExamResult(){
		visitid = 0;
		examCode = "";
		outcome = "";
		pregresid = 0;
	}
	/**
	 * 
	 * @param visit a reference to a {@link PregnancyVisit}
	 * @param examcode a reference to a {@link PregnancyExam}
	 * @param res outcome as string
	 */
	public PregnancyExamResult(int visit, String examcode, String res){
		this.visitid = visit;
		this.examCode =  examcode;
		this.outcome = res;
	}
	/**
	 * @return  the reference to a  {@link PregnancyVisit}  
	 */
	public int getVisitid() {
		return visitid;
	}
	/**
	 * @param visitid  the reference to a  {@link PregnancyVisit}  
	 */
	public void setVisitid(int visitid) {
		this.visitid = visitid;
	}
	/**
	 * @return  the reference to a  {@link PregnancyExam}  
	 */
	public String getExamCode() {
		return examCode;
	}
	/**
	 * @param examid  the reference to a  {@link PregnancyExam}  
	 */
	public void setExamCode(String examid) {
		this.examCode = examid;
	}
	/**
	 * @return  the result as String
	 */
	public String getOutcome() {
		return outcome;
	}
	/**
	 * @param result  the result as String
	 */
	public void setOutcome(String result) {
		this.outcome = result;
	}
	/**
	 * 
	 * @return the primary key of the PREGNANCYEXAMRESULT table
	 */
	public int getPregnancyExamResultId() {
		return pregresid;
	}
	/**
	 * 
	 * @param resultId the primary key of the PREGNANCYEXAMRESULT table
	 */
	public void setPregnancyExamResultId(int resultId) {
		this.pregresid = resultId;
	}
	

}
