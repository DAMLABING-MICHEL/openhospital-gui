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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.excel.ExcelExporter;

public class GenericReportPharmaceuticalStock {

	public GenericReportPharmaceuticalStock(Date date, String jasperFileName, String filter, String groupBy, String sortBy, boolean toExcel) {
		try{
	        HashMap<String, String> parameters = new HashMap<String, String>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			if (date == null)
				date = new Date();
			Format formatter;
			formatter = new SimpleDateFormat("E d, MMMM yyyy");
		    String dateReport = formatter.format(date);
		    formatter = new SimpleDateFormat("yyyy-MM-dd");
		    String dateQuery = formatter.format(date);
		    formatter = new SimpleDateFormat("yyyyMMdd");
		    String dateFile = formatter.format(date);
		    
		    parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("Date", dateReport);
			parameters.put("todate", dateQuery);
			if (groupBy != null) parameters.put("groupBy", groupBy);
			if (sortBy != null) parameters.put("sortBy", sortBy);
			if (filter != null) parameters.put("filter", filter);

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
			sbFilename.append(jasperFileName);
			File defaultFilename = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			
			if (toExcel) {
				JRQuery query = jasperReport.getMainDataset().getQuery();

				String queryString = query.getText();
				System.out.println("groupBy: " + groupBy);
				System.out.println("sortBy: " + sortBy);
				System.out.println("filter: " + filter);
				
				queryString = queryString.replace("$P{todate}", "'" + dateQuery + "'");
				if (groupBy != null) queryString = queryString.replace("$P{groupBy}", "'" + groupBy + "'");
				if (sortBy != null) queryString = queryString.replace("$P!{sortBy}", "'" + sortBy + "'");
				if (filter != null) queryString = queryString.replace("$P{filter}", "'" + filter + "'");

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

				String PDFfile = "rpt/PDF/"+jasperFileName + "_" + dateFile.toString()+".pdf";
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
	
}
