/*
 * Created on 15/giu/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.stat.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.HashMap;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.generaldata.CardPrinter;
import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.utils.db.DbSingleConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericReportPatient {
	
	private static Logger logger = LoggerFactory.getLogger(GenericReportPatient.class);

	public GenericReportPatient(Integer patientID, String jasperFileName, boolean printWithouPrompt, boolean defaultPrinter) {
		try{
			HashMap<String, String> parameters = new HashMap<String, String>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("patientID", String.valueOf(patientID)); // real param
		
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			//System.out.println("Clinical sheet jasper report name:"+sbFilename.toString());
			
			File jasperFile = new File(sbFilename.toString());
			
			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			String PDFfile = "rpt/PDF/"+jasperFileName + "_" + String.valueOf(patientID)+".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
			if (printWithouPrompt) {
				if (defaultPrinter) {
					JasperPrintManager.printReport(jasperPrint, false);
					return;
				} else if (CardPrinter.PRINTER.equals("no")) {
					JasperPrintManager.printReport(jasperPrint, true);
					return;
				}
				FileInputStream psStream = null;  
				DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			    PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
			    try {  
			        psStream = new FileInputStream(PDFfile);  
			        } catch (FileNotFoundException ffne) {  
			          ffne.printStackTrace();  
			        }  
			    if (psStream == null) {  
			        return;  
			    } 
			    if (services.length > 0) {
			        PrintService myService = null;
			        for(PrintService service : services) {
			            if(service.getName().contains(CardPrinter.PRINTER)) {
			                myService = service;
			                break;
			            }
			        }
			        DocPrintJob printJob = myService.createPrintJob();
			        Doc document = new SimpleDoc(psStream, flavor, null);
			        try {
			            printJob.print(document, null);
			        } catch (PrintException e) {
			            e.printStackTrace();
			        }
			    } else {
			    	logger.error("No PDF printer available.");
			    } 
				
			} else {
				if (GeneralData.INTERNALVIEWER)
					JasperViewer.viewReport(jasperPrint,false);
				else { 
					try{
						Runtime rt = Runtime.getRuntime();
						rt.exec(GeneralData.VIEWER +" "+ PDFfile);
						
					} catch(Exception e){
						e.printStackTrace();
					}
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
