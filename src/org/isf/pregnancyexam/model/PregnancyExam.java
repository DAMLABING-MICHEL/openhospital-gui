package org.isf.pregnancyexam.model;

public class PregnancyExam {
	private String examId;
	private String examDesc;
	private int examType = -1;
	private String examDefault = null;
	private String examValues = null;

	public PregnancyExam(String id, String desc, int type, String defaultvalue, String values) {
		this.examId = id;
		this.examDesc = desc;
		this.examType = type;
		this.examDefault = defaultvalue;
		this.examValues = values;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getExamDesc() {
		return examDesc;
	}

	public void setExamDesc(String examDesc) {
		this.examDesc = examDesc;
	}

	public int getExamType() {
		return examType;
	}

	public void setExamType(int examType) {
		this.examType = examType;
	}

	public String getExamDefault() {
		return examDefault;
	}

	public void setExamDefault(String examDefault) {
		this.examDefault = examDefault;
	}

	public String getExamValues() {
		return examValues;
	}

	public void setExamValues(String examValues) {
		this.examValues = examValues;
	}

}
