package pregnancydbpopulation;


import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JOptionPane;

import junit.framework.Assert;

import org.isf.admission.gui.PatientSummary;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyExam;
import org.isf.pregnancy.model.PregnancyExamResult;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancyexam.manager.PregnancyExamManager;
import org.junit.Test;

import com.apple.crypto.provider.Debug;
import com.sun.codemodel.internal.JOp;

import sun.util.resources.LocaleData;

public class PregnancyDbPopulation {
	private PregnancyExamManager pregexManager;
	private PatientBrowserManager patientManager;
	private PregnancyCareManager pregManager;
	int pregexprenatalcount = 10;
	int pregexpostnatalcount = 10;
	int patientcount = 10;
	int pregcount = 4;
	int visitcount = 4;

	@Test
	public void testInsertExams() {
		pregexprenatalcount=  new Integer(JOptionPane.showInputDialog("Number of prenatal exams to insert "));
		pregexpostnatalcount=  new Integer(JOptionPane.showInputDialog("Number of postnatal exams to insert "));
		pregexManager = new PregnancyExamManager();
		boolean insres = true;
		for (int a = 0; a < pregexprenatalcount; a++) {
			Integer aint = new Integer(a+1);
			PregnancyExam pregex = new PregnancyExam("PRE"+aint.toString(),
					"Prenatal PregnancyExam" + aint.toString(), -1, "Prenatal PregnancyExamDescription" + aint.toString(),
					"one value; two values");
			boolean ir = pregexManager.insertPregnancyExam(pregex);
			if (!ir)
				insres = false;
		}
		for (int a = 0; a < pregexpostnatalcount; a++) {
			Integer aint = new Integer(a+1);
			PregnancyExam pregex = new PregnancyExam("POST"+aint.toString(),
					"Postnatal PregnancyExam" + aint.toString(), 1, "Postnatal PregnancyExamDescription" + aint.toString(),
					"one value; two values");
			boolean ir = pregexManager.insertPregnancyExam(pregex);
			if (!ir)
				insres = false;
		}
		Assert.assertTrue(insres);
	}

	@Test
	public void testInsertPatients() {
		patientcount =new Integer(JOptionPane.showInputDialog("Number of patients to insert "));
		patientManager = new PatientBrowserManager();
		boolean insres = true;
		for (int a = 0; a < patientcount; a++) {
			Integer aint = new Integer(a);
			Patient pat = new Patient();
			pat.setFirstName("FemaleTestPatient" + aint.toString());
			pat.setSecondName("Surname" + aint.toString());
			pat.setAge(23);
			pat.setBirthDate("");
			pat.setAgetype("");
			pat.setSex('F');
			pat.setFather_name("testfather"+aint.toString());
			pat.setMother_name("testmother"+aint.toString());
			pat.setFather('V');
			pat.setMother('M');
			pat.setHasAssurance('Y');
			pat.setParentTogether('Y');
			pat.setNote("note"+aint.toString());
			boolean res = patientManager.newPatient(pat);
			if (!res)
				insres = false;
		}
		Assert.assertTrue(insres);

	}

	@Test
	public void testInsertPregnanciesVisitsAndResults() {
		pregManager = new PregnancyCareManager();
		pregexManager = new PregnancyExamManager();
		pregcount= new Integer(JOptionPane.showInputDialog("Number of pregnancies per patient to insert "));
		visitcount= new Integer(JOptionPane.showInputDialog("Number of pregnancy visits per pregnancy to insert "));
		boolean insertres = true;
		ArrayList<Patient> patients = pregManager.getPregnancyPatients("Female");
		ArrayList<PregnancyExam> pregnancyexams = pregexManager
				.getPregnancyExams();
		for (int a = 0; a < patients.size(); a++) {
			Patient pat = patients.get(a);
			for (int b = 0; b < pregcount; b++) {
				Pregnancy preg = new Pregnancy(pat.getCode());
				preg.setPregnancynr(b+1);
				int pregid = pregManager.newPregnancy(preg);
				if(pregid<0)
					insertres= false;
				for(int c=0; c<visitcount; c++){
					int type = (c%2==1)?-1:1;
					PregnancyVisit vis = new PregnancyVisit(pat.getCode(), pregid, type);
					vis.set(1, vis.get(1)-(pregcount-b));
					
					if (type== -1){
						vis.set(3, -(c+visitcount));
						vis.setNote("Note for Prenatal visit");
					}
					else{
						vis.set(3, -c);
						vis.setNote("Note for Postnatal visit");
					}
					int visitid = pregManager.newVisit(vis);
					if(visitid <0)
						insertres = false;
					ArrayList<PregnancyExamResult> pregexres = new ArrayList<PregnancyExamResult>();
					for(int d=0; d<pregnancyexams.size(); d++){
						Integer dint = new Integer(d);
						PregnancyExam ex = pregnancyexams.get(d);
						PregnancyExamResult res = new PregnancyExamResult();
						res.setExamCode(ex.getExamId());
						res.setVisitid(visitid);
						res.setOutcome("res" + dint.toString());
						pregexres.add(res);
						
					}
					boolean ir = pregManager.newExamOutcomes(visitid, pregexres);
					if(!ir)
						insertres = false;
				}
			}
		}
		Assert.assertTrue(insertres);

	}

}
