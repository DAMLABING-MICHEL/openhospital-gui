package org.isf.stat.java;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

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
public class OH020_AdmissionAverageCost {
	
	private final String ADM = "M1 -|M2 -|S1 -|S2 -|S3 -|S4 -|OR1 -|OR2 -|OR3 -|OR4 -|MM1 -|MM2 -|SM1 -|SM2 -|SM3 -|SM4 -|MOR1 -|MOR2 -|MOR3 -|MOR4 -|P1 -|P2 -|PS1 -|PS2 -|PS3 -|PS4 -|POR1 -|POR2 -|POR3 -|POR4 -|SG1 -|SG2 -|SG3 -|D1 -|D2 -|D3 -|D4 -|D5 -|D6 -|D7 -|MWA -|N1 -|N2 -|OVN -|POVN -|MOVN -";
	private final String FREE = "FREE|COFF|STAFF|EXEMPT";
	private final String FOOD = "DAY|PDAY";
	
	//we don't need OTHER_CODES since what doesn't fall in previous lists is automatically OTHER
	private final String OTHER_CODES = "PHY -|PHY1 -|FR1 -|FR2 -|F -|E1 -|E2 -|E3 -|E4 -|E5 -|E6 -|E7 -|E8 -|E9 -|E10 -|E11 -|ENT1 -|ENT2 -|ENT3 -|DN1 -|DN2 -|DN3 -|DN4 -|DN5 -|DN6 -|DN7 -|DN8 -|DN9 -|DN10 -|DN11 -|DN12 -|DN13 -|DN14 -|DN15 -|DN16 -|SS1 -";
	
	public OH020_AdmissionAverageCost() {}

	public void OH020_AdmissionAverageCost(String fromDate, String toDate, String jasperFileName, boolean toCSV) {
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
	        DischargeTypeBrowserManager disTypeMan = new DischargeTypeBrowserManager();
	        HashMap<String, String> disTypeMap = new HashMap<String, String>();
	        ArrayList<DischargeType> disTypeList = disTypeMan.getDischargeType();
	        for (DischargeType disType : disTypeList) {
	        	disTypeMap.put(disType.getCode(), disType.getDescription());
	        }
	        WardBrowserManager wardMan = new WardBrowserManager();
	        HashMap<String, String> wardMap = new HashMap<String, String>();
	        ArrayList<Ward> wardList = wardMan.getWards();
	        for (Ward ward : wardList) {
	        	wardMap.put(ward.getCode(), ward.getDescription());
	        }
			
			//Main Managers
			BillBrowserManager billMan = new BillBrowserManager();
			AdmissionBrowserManager admMan = new AdmissionBrowserManager();
			OpdBrowserManager opdMan = new OpdBrowserManager();
			PatientBrowserManager patMan = new PatientBrowserManager();

			// Admission (only discharge) list between two dates
			ArrayList<Admission> admList = admMan.getDischarges(toCalendar(fromDate), toCalendar(toDate));
			if (GeneralData.DEBUG) System.out.println("Admissions found: " + admList.size());
			if (admList == null) return;
			Collections.sort(admList, new AdmissionWardComparator());

			// Mapping each admission with its summary amounts taken from its billItems
			for (Admission adm : admList) {
				if (GeneralData.DEBUG) System.out.println("Admission: " + adm.getId());
//				if (GeneralData.DEBUG && adm.getDeleted().equals("Y")) {
//					System.out.println("Admission DELETED!!!: " + adm.getId());
//					continue;
//				}
				
				//Starting date for bills retrieval
				GregorianCalendar startEncounter = adm.getAdmDate();
				
				//Ending date for bills retrieval (to be defined)
				GregorianCalendar endEncounter = null;
				
				int patientcode = adm.getPatId();
				Patient pat = patMan.getPatient(patientcode);
				//ArrayList<Bill> patBills = billMan.getPatientBills(patientcode);
				
				//looking for OPD visit dates
				ArrayList<GregorianCalendar> dates = new ArrayList<GregorianCalendar>();
				Opd nextOpd = opdMan.getNextOpd(adm);
				if (nextOpd != null) dates.add(nextOpd.getVisitDate());
//				ArrayList<Opd> patientOpdList = opdMan.getOpdList(adm.getPatId());
//				for (Opd anOpd : patientOpdList) {
//					dates.add(anOpd.getVisitDate());
//				}
				
				//looking for Admissions dates
				Admission nextAdmission = admMan.getNextAdmissions(adm);
				if (nextAdmission != null) dates.add(nextAdmission.getAdmDate());
//				ArrayList<Admission> patientAdmList = admMan.getAdmissions(pat);
//				for (Admission anAdm : patientAdmList) {
//					dates.add(anAdm.getAdmDate());
//				}
				
				//Sorting all dates in ascending order
				Collections.sort(dates);
				
				//looking for the date just after this discharge, starting from the oldest
				GregorianCalendar dischargeDate = adm.getDisDate();
				for (GregorianCalendar gc : dates) {
					if (TimeTools.isSameDay(gc, dischargeDate)) { //if the next encounter is the same date, we use the discharge date
						endEncounter = new GregorianCalendar();
						endEncounter.setTime(dischargeDate.getTime());
						break;
					}
					if (gc.after(dischargeDate)) {
						endEncounter = new GregorianCalendar();
						endEncounter.setTime(gc.getTime());
						break;
					}
				}
				if (GeneralData.DEBUG) System.out.println("StartEncounter: " + toString(startEncounter));
				if (GeneralData.DEBUG && endEncounter != null) System.out.println("EndEncounter: " + toString(endEncounter));
				if (adm.getPatId() == 330138) {
					System.out.println("StartEncounter: " + toString(startEncounter));
					if (endEncounter != null) System.out.println("EndEncounter: " + toString(endEncounter));
					else System.out.println("EndEncounter: " + endEncounter);
					
				}
				
//				ArrayList<Bill> admBills = new ArrayList<Bill>();
//				for (Bill bill : patBills) {
//					String status = bill.getStatus();
//					if (status.equals("D") || status.equals("X")) continue;
//					if (endEncounter == null) {
//						if (bill.getDate().after(startEncounter))
//							admBills.add(bill);
//					} else {
//						if (bill.getDate().after(startEncounter) &&
//								bill.getDate().before(endEncounter))
//							admBills.add(bill);
//					}
//				}
				ArrayList<Bill> admBills = billMan.getPatientBills(patientcode, startEncounter, endEncounter);
				if (GeneralData.DEBUG) System.out.println("Bills found: " + admBills.size());
				
				Payments payments = new Payments();
				for (Bill bill : admBills) {
					String status = bill.getStatus();
					if (status.equals("D") || status.equals("X")) continue;
					ArrayList<BillItem> billItems = billMan.getItems(bill.getId());
					if (GeneralData.DEBUG) System.out.println("BillItems found: " + billItems.size());
					
					for (BillItem billItem : billItems) {
						double amount = billItem.getItemAmount();
						int qty = billItem.getItemQuantity();
						String description = billItem.getItemDescription();
						
						if (description.matches("^.*?("+ADM+").*$")) payments.addAdm(amount, qty);
						else if (description.matches("^.*?("+FOOD+").*$")) payments.addFood(amount, qty);
						else if (description.matches("^.*?("+FREE+").*$")) payments.addFree(amount, qty);
						else if (billItem.getPriceID().contains("MED")) payments.addDrugs(amount, qty);
						else payments.addOther(amount, qty);
					}
				}
				
				//Fields calculations
				DateTime discharge = new DateTime(adm.getDisDate());
	        	DateTime admission = new DateTime(adm.getAdmDate());
	        	DateTime birthdate = new DateTime(pat.getBirthDate());
	        	
	        	Period lof = new Period(admission, discharge, PeriodType.days());
	        	Period age = new Period(birthdate, discharge, PeriodType.years());
	        	
	        	int days = lof.getDays() == 0 ? 1 : lof.getDays();
	        	
	        	//Report data (one row)
				row = new LinkedHashMap();
				row.put("PAT_ID", pat.getCode());
				row.put("PAT_NAME", pat.getName());
				row.put("PAT_WOREDA", pat.getCity());
				row.put("PAT_AGE", age.getYears());
				row.put("PAT_SEX", ""+pat.getSex());
	            row.put("ADM_WARD", wardMap.get(adm.getWardId()));
	            row.put("ADM_LOS", days);
	            row.put("ADM_DIAG", disMap.get(adm.getDiseaseOutId1()));
	            row.put("ADM_OUTCOME", disTypeMap.get(adm.getDisType()));
	            row.put("BLL_ADM", payments.getAdmission());
	            row.put("BLL_DRUGS", payments.getDrugs());
	            row.put("BLL_FOOD",  payments.getFood());
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
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Exel (*.xls)", "xls");
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
		SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return pattern.format(fromDate.getTime());
	}
	
	public static void main(String[] args) {
		OH020_AdmissionAverageCost thisClass = new OH020_AdmissionAverageCost();
		thisClass.OH020_AdmissionAverageCost("01/10/2017", "31/10/2017", "OH020_AdmissionAverageCost", true);
	}
	
private class Payments {
		
		private BigDecimal admission;
		private BigDecimal drugs;
		private BigDecimal food;
		private BigDecimal other;
		private BigDecimal free;
		private BigDecimal total;
		
		public Payments() {
			this.admission = new BigDecimal(0);
			this.drugs = new BigDecimal(0);
			this.food = new BigDecimal(0);
			this.other = new BigDecimal(0);
			this.free = new BigDecimal(0);
			this.total = new BigDecimal(0);
		}
		public void addFree(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.free = free.add(amount);
			addTotal(amount);
			
		}
		public void addFood(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.food = food.add(amount);
			addTotal(amount);
			
		}
		public void addAdm(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.admission = admission.add(amount);
			addTotal(amount);
			
		}
		public void addDrugs(double itemAmount, int qty) {
			BigDecimal amount = new BigDecimal(Double.toString(itemAmount)).multiply(new BigDecimal(qty));
			this.drugs = drugs.add(amount);
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
		public BigDecimal getAdmission() {
			return admission;
		}
		public BigDecimal getDrugs() {
			return drugs;
		}
		public BigDecimal getFood() {
			return food;
		}
		public BigDecimal getFree() {
			return free;
		}
		public BigDecimal getOther() {
			return other;
		}
		public BigDecimal getTotal() {
			return total;
		}
		
	}

	public class AdmissionWardComparator implements Comparator<Admission> {
	
	@Override
	public int compare(Admission o1, Admission o2) {
		return o1.getWardId().compareTo(o2.getWardId());
	}
}

}
