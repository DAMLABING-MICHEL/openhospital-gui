package org.isf.pregnancyexam.manager;

import java.util.ArrayList;

import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.pregnancyexam.service.IoOperations;

/**
 * Martin Reinstadler This class communicates with the IoOperation class. The
 * GUI instantiates this class for simplicity
 * 
 */
public class PregnancyExamManager {
	/**
	 */
	private IoOperations ioOperations = new IoOperations();

	/**
	 * 
	 * @return the list of {@link PregnancyExam} ordered by prenatal exams and
	 *         postnatal exams
	 */
	public ArrayList<PregnancyExam> getPregnancyExams() {
		return ioOperations.getPregnancyExams();
	}
	
	/**
	 * @param visittype the type of the {@link PregnancyVisit} from -1 to 1
	 * @return the list of {@link PregnancyExam} for the visittype
	 */
	public ArrayList<PregnancyExam> getPregnancyExams(int visittype) {
		return ioOperations.getPregnancyExams(visittype);
	}

	/**
	 * 
	 * @param examCode
	 *            the id of the {@link PregnancyExam}
	 
	 * @return true if the exam is deleted correctly
	 */
	public boolean deletePregnancyExam(String examCode) {
		
			ioOperations.deletePregnancyExamResults(examCode);
		return ioOperations.deletePregnancyExam(examCode);
	}

	/**
	 * 
	 * @param exam
	 *            the {@link PregnancyExam} to be inserted
	 
	 * @return true if the exam is inserted correctly
	 */
	public boolean insertPregnancyExam(PregnancyExam exam) {
			return ioOperations.insertPregnancyExam(exam.getExamId(), exam
					.getExamDesc(), exam.getExamType(), exam
					.getExamDefault(), exam.getExamValues());
	}

	/**
	 * 
	 * @param exam
	 *            the {@link PregnancyExam} to be inserted
	
	 * @return true if the exam is updated correctly
	 */
	public boolean updatePregnancyExam(PregnancyExam exam) {
		
		return ioOperations.updatePregnancyExam(exam.getExamId(), exam
					.getExamDesc(), exam.getExamType(), exam
					.getExamDefault(), exam.getExamValues());
	}

	/**
	 * 
	 * @param exam
	 *            the {@link PregnancyExam} to check for existance
	 * @return true if there is already a tuple with the same code and the type
	 *         as the specified {@link PregnancyExam}
	 */
	public boolean existsPregnancyExam(PregnancyExam exam) {
		return ioOperations.existsPregnancyExam(exam.getExamId());
	}

}
