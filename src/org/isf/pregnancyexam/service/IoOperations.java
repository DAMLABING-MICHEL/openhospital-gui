package org.isf.pregnancyexam.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.pregnancyexam.model.PregnancyExamResult;
import org.isf.utils.db.DbQuery;


/**
 * @author Martin Reinstadler this class performs database readings and
 *         insertions for the tables: EXAM, EXAMTYPE, PREGNANCYEXAM
 * 
 */
public class IoOperations {
	
	/**
	 * 
	 * @return the list of {@link PregnancyExam} ordered by prenatal exams and
	 *         postnatal exams
	 */
	public ArrayList<PregnancyExam> getPregnancyExams() {
		ArrayList<PregnancyExam> exams = new ArrayList<PregnancyExam>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr = new StringBuffer(
				"SELECT PEX.* FROM PREGNANCYEXAM PEX  ");
		
		stringBfr.append("ORDER BY PEX.PREGEX_TYPE");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				PregnancyExam ex = new PregnancyExam(resultSet.getString("PREGEX_ID"),
						resultSet.getString("PREGEX_DESC") ,resultSet.getInt("PREGEX_TYPE"), 
						resultSet.getString("PREGEX_DEFAULT"), resultSet.getString("PREGEX_VALUES"));
				exams.add(ex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exams;
	}
	
	/**
	 * @param visittype the type of the {@link PregnancyVisit} from -1 to 1
	 * @return the list of {@link PregnancyExam} for the visittype
	 */
	public ArrayList<PregnancyExam> getPregnancyExams(int visittype) {
		ArrayList<PregnancyExam> exams = new ArrayList<PregnancyExam>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr = new StringBuffer("SELECT PEX.* FROM PREGNANCYEXAM PEX  ");
		stringBfr.append(" WHERE PEX.PREGEX_TYPE = ").append(visittype);
		stringBfr.append(" ORDER BY PEX.PREGEX_TYPE");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				PregnancyExam ex = new PregnancyExam(
						resultSet.getString("PREGEX_ID"),
						resultSet.getString("PREGEX_DESC"),
						resultSet.getInt("PREGEX_TYPE"), 
						resultSet.getString("PREGEX_DEFAULT"), 
						resultSet.getString("PREGEX_VALUES"));
				exams.add(ex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exams;
	}

	/**
	 * Deletes a {@link PregnancyExam} from the database
	 * 
	 * @param examCode
	 *            the id of the {@link PregnancyExam}
	 * @return true if the exam is deleted successfully
	 */
	public boolean deletePregnancyExam(String examCode) {
		StringBuffer stringBfr = new StringBuffer();
		
			stringBfr.append("DELETE FROM PREGNANCYEXAM WHERE "
					+ "PREGEX_ID = '" + examCode + "'");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param examCode
	 *            the code of the {@link PregnancyExam}
	 * @param description
	 *            the desciption of the {@link PregnancyExam}
	 * @param examtype
	 *            the type of the {@link PregnancyExam}
	 * @param defval
	 *            the defaultvalue of the {@link PregnancyExam}
	 * @param values
	 *            the possible values of the {@link PregnancyExam}
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancyExam(String examCode, String description,
			int examtype, String defval, String values) {

		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCYEXAM SET ");
		stringBfr.append("PREGEX_DESC = '" + description + "', ");
		stringBfr.append("PREGEX_TYPE = " + examtype + ", ");
		String defaultvalue = defval == null ? "null, " : "'" + defval + "', ";
		stringBfr.append("PREGEX_DEFAULT = " + defaultvalue);
		String val = values == null ? "null " : "'" + values + "' ";
		stringBfr.append("PREGEX_VALUES = " + val);
		stringBfr.append(" WHERE PREGEX_ID = '" + examCode + "' ");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	

	/**
	 * 
	 * @param code
	 *            the code of the {@link PregnancyExam}
	 
	 * @return true if the {@link PregnancyExam} exists already
	 */
	public boolean existsPregnancyExam(String code) {
		StringBuffer stringBfr = new StringBuffer();
		
			stringBfr.append("SELECT * FROM PREGNANCYEXAM WHERE PREGEX_ID = '"+ code+"'");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);

			if (resultSet.next()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param examCode
	 *            the code of the {@link PregnancyExam}
	 * @param description
	 *            the description of the {@link PregnancyExam}
	 * @param examtype
	 *            the type of the {@link PregnancyExam}
	 * @param defval
	 *            the defaultvalue of the {@link PregnancyExam}
	 * @param values
	 *            the values of the {@link PregnancyExam};
	 * @return true if the tuple is inserted correctly
	 */
	public boolean insertPregnancyExam(String examCode, String description,
			int examtype, String defval, String values) {
		StringBuffer stringBfr = new StringBuffer(
				"INSERT INTO PREGNANCYEXAM (PREGEX_ID, PREGEX_DESC, PREGEX_TYPE, PREGEX_DEFAULT, PREGEX_VALUES) ");
		stringBfr.append("VALUES ('" + examCode + "' , '" + description
				+ "' , " + examtype + ", ");
		stringBfr.append(defval == null ? "null, " : "'" + defval + "', ");
		stringBfr.append(values == null ? "null )" : "'" + values + "') ");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * @param examCode the code of the {@link PregnancyExam}
	 * @return true if all the {@link PregnancyExamResult} for 
	 * the specified visit are deleted
	 */
	public boolean deletePregnancyExamResults(String examCode) {
		StringBuffer stringBfr = new StringBuffer(
				"DELETE FROM PREGNANCYEXAMRESULT WHERE PEXRES_PREGEX_ID = '");
		stringBfr.append(examCode+"'");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	


}
