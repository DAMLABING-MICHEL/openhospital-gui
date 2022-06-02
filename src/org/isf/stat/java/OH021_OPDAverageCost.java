package org.isf.stat.java;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.time.TimeTools;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/*--------------------------------------------------------
 * GenericReportLauncer2Dates
 *  - lancia tutti i report che come parametri hanno "da data" "a data"
 * 	- la classe prevede l'inizializzazione attraverso 
 *    dadata, adata, nome del report (senza .jasper)
 *---------------------------------------------------------
 * modification history
 * 09/06/2007 - prima versione
 *
 *-----------------------------------------------------------------*/
public class OH021_OPDAverageCost {
	
	private final String CARDS = "C1 -|C2 -|C3 -|EMG -|MC1 -|MC2 -|MC3 -|MEMG -|PC1 -|PC2 -|PC3 -|PEMG -|ANC -|ANCC -|NA -|PAD -";
	private final String LAB = "ST0 -|ST1 -|ST2 -|ST3 -|ST4 -|MT0 -|MT1 -|MT2 -|MT3 -|MT4 -|PT0 -|PT1 -|PT2 -|PT3 -|PT4 -";
	private final String XRAY = "X1 -|X4 -|X2 -|X3 -|US -|ECG -|MX1 -|MX4 -|MX2 -|MX3 -|MUS1 -|MECG -|PX1 -|PX4 -|PX2 -|PX3 -|PUS -|PECG -";
	private final String FREE = "FREE|COFF|STAFF|EXEMPT";
	
	public OH021_OPDAverageCost() {}

	public void OH021_OPDAverageCost(String fromDate, String toDate, String jasperFileName, boolean toCSV) {
		try {
			//Data Source Map & Collection (columns & rows) for report
			Map row;
	        Collection data = new ArrayList();
	        
	        //HashMaps of dictionaries
	        DiseaseBrowserManager disMan = new DiseaseBrowserManager();
	        HashMap<String, String> disMap = new HashMap<String, String>();
	        ArrayList<Disease> disList = disMan.getDiseaseAll();
	        for (Disease disease : disList) {
	        	disMap.put(disease.getCode(), disease.getDescription());
	        }
			
			//Main Managers
			BillBrowserManager billMan = new BillBrowserManager();
			PatientBrowserManager patMan = new PatientBrowserManager();
			OpdBrowserManager opdMan = new OpdBrowserManager();
			AdmissionBrowserManager admMan = new AdmissionBrowserManager();

			// OPD list between two dates
			ArrayList<Opd> opdList = opdMan.getOpd(MessageBundle.getMessage("angal.opd.alltype"), MessageBundle.getMessage("angal.opd.alldisease"), toCalendar(fromDate), toCalendar(toDate), 0, 0, 'A', "A");
			if (GeneralData.DEBUG) System.out.println("OPDs found: " + opdList.size());
			if (opdList == null) return;

			// Mapping each opd with its summary amounts taken from its billItems between the opd and the next
			// event (another OPD or Admission)
			for (Opd opd : opdList) {
				if (GeneralData.DEBUG) System.out.println("Opd: " + opd.getCode());
				
				int patientcode = opd.getpatientCode();
				Patient pat = patMan.getPatient(patientcode);
				//ArrayList<Bill> patBills = billMan.getPatientBills(patientcode);
				
				//Starting date for bills retrieval
				GregorianCalendar startEncounter = opd.getVisitDate();
				
				//Ending date for bills retrieval (to be defined)
				GregorianCalendar endEncounter = null;
				
				//looking for OPD visit dates
				ArrayList<GregorianCalendar> dates = new ArrayList<GregorianCalendar>();
				Opd nextOpd = opdMan.getNextOpd(opd);
				if (nextOpd != null) dates.add(nextOpd.getVisitDate());
//				ArrayList<Opd> patientOpdList = opdMan.getOpdList(patientcode);
//				for (Opd anOpd : patientOpdList) {
//					dates.add(anOpd.getVisitDate());
//				}
				
				//looking for Admissions dates
				Admission nextAdmission = admMan.getNextAdmissions(opd);
				if (nextAdmission != null) dates.add(nextAdmission.getAdmDate());
//				ArrayList<Admission> patientAdmList = admMan.getAdmissions(pat);
//				for (Admission anAdm : patientAdmList) {
//					dates.add(anAdm.getAdmDate());
//				}
				
				//Sorting all dates in ascending order
				Collections.sort(dates);
				
				//looking for the date just after this OPD, starting from the oldest
				for (GregorianCalendar gc : dates) {
					if (TimeTools.isSameDay(gc, startEncounter) || gc.after(startEncounter)) {
						endEncounter = new GregorianCalendar();
						endEncounter.setTime(gc.getTime());
						break;
					}
				}
				if (GeneralData.DEBUG) System.out.println("StartEncounter: " + toString(startEncounter));
				if (GeneralData.DEBUG && endEncounter != null) System.out.println("EndEncounter: " + toString(endEncounter));
				
				//selecting bills concerning the opd
//				ArrayList<Bill> opdBills = new ArrayList<Bill>();
//				for (Bill bill : patBills) {
//					String status = bill.getStatus();
//					if (status.equals("D") || status.equals("X")) continue;
//					if (endEncounter == null) {
//						if (bill.getDate().after(startEncounter))
//							opdBills.add(bill);
//					} else {
//						if (bill.getDate().after(startEncounter) &&
//								bill.getDate().before(endEncounter))
//							opdBills.add(bill);
//					}
//				}
				ArrayList<Bill> opdBills = billMan.getPatientBills(patientcode, startEncounter, endEncounter);
				if (GeneralData.DEBUG) System.out.println("Bills found: " + opdBills.size());
				
				Payments payments = new Payments();
				for (Bill bill : opdBills) {
					ArrayList<BillItem> billItems = billMan.getItems(bill.getId());
					if (GeneralData.DEBUG) System.out.println("BillItems found: " + billItems.size());
					
					for (BillItem billItem : billItems) {
						double amount = billItem.getItemAmount();
						int qty = billItem.getItemQuantity();
						String description = billItem.getItemDescription();
						if (GeneralData.DEBUG) System.out.println("Item description: " + description);
						if (description.matches("^.*?("+CARDS+").*$")) payments.addCards(amount, qty);
						else if (description.matches("^.*?("+LAB+").*$")) payments.addLab(amount, qty);
						else if (description.matches("^.*?("+XRAY+").*$")) payments.addXRay(amount, qty);
						else if (description.matches("^.*?("+FREE+").*$")) payments.addFree(amount, qty);
						else if (billItem.getPriceID().contains("MED")) payments.addDrugs(amount, qty);
						else payments.addOther(amount, qty);
					}
				}
				
				//Fields calculations
				DateTime opdDate = new DateTime(startEncounter);
	        	DateTime birthdate = new DateTime(pat.getBirthDate());
	        	
	        	Period age = new Period(birthdate, opdDate, PeriodType.years());
	        	
	        	
	        	//Report data (one row)
	        	if (GeneralData.DEBUG) System.out.println(new Timestamp(startEncounter.getTimeInMillis()));
				row = new LinkedHashMap();
				row.put("PAT_ID", pat.getCode());
				row.put("PAT_NAME", pat.getName());
				row.put("PAT_WOREDA", pat.getCity());
				row.put("PAT_AGE", age.getYears());
				row.put("PAT_SEX", ""+pat.getSex());
	            row.put("OPD_DATE", new Timestamp(startEncounter.getTimeInMillis()));
	            row.put("OPD_DIAG", disMap.get(opd.getDisease()));
	            row.put("BLL_CARDS", payments.getCards());
	            row.put("BLL_DRUGS", payments.getDrugs());
	            row.put("BLL_LAB",  payments.getLab());
	            row.put("BLL_XRAY",  payments.getXRay());
	            row.put("BLL_OTHER",  payments.getOther());
	            row.put("BLL_FREE",  payments.getFree());
	            row.put("BLL_TOTAL",  payments.getTotal());
	            
				data.add(row);
			}

			
			//Normal report
			HashMap<String, String> parameters = new HashMap<String, String>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();

			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("fromdate", fromDate + ""); // real param
			parameters.put("todate", toDate + ""); // real param

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());
			
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);

			JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
			
			if (toCSV) {
				
				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xls)", "xls");
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fcExcel.setSelectedFile(new File(jasperFileName));

				int iRetVal = fcExcel.showSaveDialog(null);
				if (iRetVal == JFileChooser.APPROVE_OPTION) {
					File exportFile = fcExcel.getSelectedFile();
					if (!exportFile.getName().endsWith("xls"))
						exportFile = new File(exportFile.getAbsoluteFile() + ".xls");

					ExcelExporter xlsExport = new ExcelExporter();
					xlsExport.exportDataToExcel(data, exportFile);
				}

			} else {
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
				String PDFfile = "rpt/PDF/"+jasperFileName+".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
				
				if (GeneralData.INTERNALVIEWER)
					JasperViewer.viewReport(jasperPrint, false);
				else {
					try {
						Runtime rt = Runtime.getRuntime();
						rt.exec(GeneralData.VIEWER + " " + PDFfile);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GregorianCalendar toCalendar(String fromDate) {
		SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy");
		GregorianCalendar gc = null;
		try {
			gc = new GregorianCalendar();
			gc.setTime(pattern.parse(fromDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return gc;
	}
	
	private String toString(GregorianCalendar fromDate) {
		SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy");
		return pattern.format(fromDate.getTime());
	}
	
	public static void main(String[] args) {
		OH021_OPDAverageCost thisClass = new OH021_OPDAverageCost();
		thisClass.OH021_OPDAverageCost("01/10/2017", "31/10/2017", "OH021_OPDAverageCost", false);
	}
	
private class Payments {
		
		private BigDecimal cards;
		private BigDecimal drugs;
		private BigDecimal lab;
		private BigDecimal xray;
		private BigDecimal other;
		private BigDecimal free;
		private BigDecimal total;
		
		public Payments() {
			this.cards = new BigDecimal(0);
			this.drugs = new BigDecimal(0);
			this.lab = new BigDecimal(0);
			this.xray = new BigDecimal(0);
			this.other = new BigDecimal(0);
			this.free = new BigDecimal(0);
			this.total = new BigDecimal(0);
		}
		public void addLab(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.lab = lab.add(amount);
			addTotal(amount);
			
		}
		public void addFree(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.free = free.add(amount);
			addTotal(amount);
			
		}
		public void addCards(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.cards = cards.add(amount);
			addTotal(amount);
			
		}
		public void addDrugs(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.drugs = drugs.add(amount);
			addTotal(amount);
		}
		public void addXRay(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.xray = xray.add(amount);
			addTotal(amount);
		}
		public void addOther(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.other = other.add(amount);
			addTotal(amount);
		}
		
		private void addTotal(BigDecimal amount) {
			this.total = total.add(amount);
		}
		public BigDecimal getCards() {
			return cards;
		}
		public BigDecimal getDrugs() {
			return drugs;
		}
		public BigDecimal getXRay() {
			return xray;
		}
		public BigDecimal getLab() {
			return lab;
		}
		public BigDecimal getOther() {
			return other;
		}
		public BigDecimal getFree() {
			return free;
		}
		public BigDecimal getTotal() {
			return total;
		}
		
	}
}
