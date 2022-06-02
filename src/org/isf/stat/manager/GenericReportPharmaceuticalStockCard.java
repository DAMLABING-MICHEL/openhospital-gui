/*
 * Created on 15/giu/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.medicals.model.Medical;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.db.UTF8Control;
import org.isf.utils.excel.ExcelExporter;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class GenericReportPharmaceuticalStockCard {
	
	private final static Logger logger = LoggerFactory.getLogger(GenericReportPharmaceuticalStockCard.class);

	public GenericReportPharmaceuticalStockCard(String jasperFileName, Date dateFrom, Date dateTo, Medical medical, Ward ward, boolean toExcel) {
		try{
			
	        Map<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			if (dateFrom == null || dateTo == null)
				return;
			Format formatter;
		    formatter = new SimpleDateFormat("yyyy-MM-dd");
		    String dateFromQuery = formatter.format(dateFrom);
		    String dateToQuery = formatter.format(dateTo);
		    formatter = new SimpleDateFormat("yyyyMMdd");
		    StringBuilder fileName = new StringBuilder(jasperFileName);
		    if (ward != null) {
		    	fileName.append("_")
		    		.append(sanitizeFilename(ward.getDescription()));
		    }
		    if (medical != null) {
		    	fileName.append("_")
	    			.append(medical.getCode());
		    }
		    fileName.append("_from")
		    		.append(formatter.format(dateFrom))
		    		.append("to")
		    		.append(formatter.format(dateTo));
		    
		    String language = GeneralData.LANGUAGE;
		    ResourceBundle resourceBundle;
			try {
				resourceBundle = ResourceBundle.getBundle(
						jasperFileName, 
						new Locale(language), 
						new UTF8Control());
			} catch (MissingResourceException e) {
				logger.error(">> no resource bundle for language = " + language + " found for this report.");
				resourceBundle = ResourceBundle.getBundle("language", new Locale("en"));
			}
		    
		    parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("Currency", "ETB");
			parameters.put("fromdate", dateFromQuery);
			parameters.put("todate", dateToQuery);
			if (medical != null) {
				parameters.put("productID", String.valueOf(medical.getCode()));
			}
			parameters.put(JRParameter.REPORT_LOCALE, new Locale(language));
			parameters.put("REPORT_RESOURCE_BUNDLE", resourceBundle); //we need to pass our custom resource bundle
			if (ward != null) {
				parameters.put("WardCode", String.valueOf(ward.getCode()));
				parameters.put("WardName", String.valueOf(ward.getDescription()));
			}

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			//System.out.println("Jasper Report Name:"+sbFilename.toString());
			
			File jasperFile = new File(sbFilename.toString());
			
			sbFilename = new StringBuilder();
			sbFilename.append("PDF");
			sbFilename.append(File.separator);
			sbFilename.append(fileName);
			File defaultFilename = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			
			if (toExcel) {
				JRQuery query = jasperReport.getMainDataset().getQuery();

				String queryString = query.getText();
				
				queryString = queryString.replace("$P{fromdate}", "'" + dateFromQuery + "'");
				queryString = queryString.replace("$P{todate}", "'" + dateToQuery + "'");
				if (medical != null) queryString = queryString.replace("$P{productID}", "'" + String.valueOf(medical.getCode()) + "'");
				queryString = queryString.replace("$P{Currency}", "'ETB'");
				if (ward != null) queryString = queryString.replace("$P{WardCode}", "'" + ward.getCode() + "'");

				DbQueryLogger dbQuery = new DbQueryLogger();
				ResultSet resultSet = dbQuery.getData(queryString, true);
				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xlsx)","xlsx");
				FileNameExtensionFilter excelFilter2003 = new FileNameExtensionFilter("Excel 97-2003 (*.xls)","xls");
				fcExcel.addChoosableFileFilter(excelFilter);
				fcExcel.addChoosableFileFilter(excelFilter2003);
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fcExcel.setSelectedFile(defaultFilename);
				
				int iRetVal = fcExcel.showSaveDialog(null);
				if(iRetVal == JFileChooser.APPROVE_OPTION)
				{
					File exportFile = fcExcel.getSelectedFile();
					FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fcExcel.getFileFilter();
					String extension = selectedFilter.getExtensions()[0];
					if (!exportFile.getName().endsWith(extension)) exportFile = new File(exportFile.getAbsoluteFile() + "." + extension);
					
					ExcelExporter xlsExport = new ExcelExporter();
					if (exportFile.getName().endsWith(".xls"))
						xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
					else
						xlsExport.exportResultsetToExcel(resultSet, exportFile);
				}
				dbQuery.releaseConnection();
			
			} else {
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

				String PDFfile = "rpt/PDF/"+ fileName.toString()+".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
				
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

	private String sanitizeFilename(String inputName) {
	    return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}
}
