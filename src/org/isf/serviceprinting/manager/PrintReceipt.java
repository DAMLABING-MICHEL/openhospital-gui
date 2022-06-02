package org.isf.serviceprinting.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.isf.generaldata.TxtPrinter;

/**
 * 
 * @author Mwithi
 * 
 *         This class will read generic/text printer parameters and compile and
 *         print given jasper report. A copy will be at given file path
 * 
 */
public class PrintReceipt {

	/**
	 * @param jasperFileName
	 * @param parameters
	 * @param conn
	 */
	public PrintReceipt(String jasperFileName, HashMap<String, Object> parameters, Connection conn) {
		StringBuilder sbFilename;
		File jasperFile;
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		TxtPrinter.getTxtPrinter();
		
		try {
			PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

			if (printService != null) {
				getPrinter(printService);
				if (TxtPrinter.MODE.equalsIgnoreCase("ZPL")) {
					
					sbFilename = new StringBuilder();
					sbFilename.append("rpt");
					sbFilename.append(File.separator);
					sbFilename.append(jasperFileName);
					sbFilename.append("Txt");
					sbFilename.append(".jasper");

					jasperFile = new File(sbFilename.toString());
					
					parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
					
					jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
					
					String TXTfile = "rpt/PDF/" + jasperFile.getName() + ".txt";
					
					JRTextExporter exporter = new JRTextExporter();
					exporter.setParameter(JRTextExporterParameter.JASPER_PRINT, jasperPrint);
					exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE_NAME, TXTfile);
					exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, TxtPrinter.CHAR_WIDTH);
					exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, TxtPrinter.CHAR_HEIGHT);
					exporter.exportReport();
					
					printFileZPL(TXTfile, printService);
					
				} else if (TxtPrinter.MODE.equalsIgnoreCase("TXT")) {
						
					sbFilename = new StringBuilder();
					sbFilename.append("rpt");
					sbFilename.append(File.separator);
					sbFilename.append(jasperFileName);
					sbFilename.append("Txt");
					sbFilename.append(".jasper");

					jasperFile = new File(sbFilename.toString());
					
					//parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
					
					jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
					
					if (jasperPrint.getPages().size() > 1) {
						printReversPages(jasperPrint, printService);
					} else {
						JasperPrintManager.printReport(jasperPrint, false);
					}
					
				} else if (TxtPrinter.MODE.equalsIgnoreCase("PDF")) {
					
					sbFilename = new StringBuilder();
					sbFilename.append("rpt");
					sbFilename.append(File.separator);
					sbFilename.append(jasperFileName);
					sbFilename.append("PDF");
					sbFilename.append(".jasper");

					jasperFile = new File(sbFilename.toString());
					
					//parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
					
					jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
					
					if (jasperPrint.getPages().size() > 1) {
						printReversPages(jasperPrint, printService);
					} else {
						JasperPrintManager.printReport(jasperPrint, false);
					}

				} else {
					System.out.println("invalid MODE");
					System.out.println("MODE: " + TxtPrinter.MODE);
				}
			} else {
				System.out.println("printer was not found.");
				System.out.println(printService);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param printService
	 */
	private void printFileZPL(String file, PrintService printService) {
		try {
			
			System.out.println("Using: " + printService.getName());
			DocPrintJob job = printService.createPrintJob();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			DocAttributeSet das = new HashDocAttributeSet();
			
			FileReader frStream = new FileReader(file);
			BufferedReader brStream = new BufferedReader(frStream);
			
			int charH = TxtPrinter.ZPL_ROW_HEIGHT;
			String font = "^A" + TxtPrinter.ZPL_FONT_TYPE;
			String aLine = brStream.readLine();
			String header = "^XA^LH0,30" + aLine;//starting point
			
			StringBuilder zpl = new StringBuilder();
			int i = 0;
			while (!aLine.equals("")) {
				//System.out.println(aLine);
				zpl.append("^FO0," + (i * charH));//line position
				zpl.append(font + "," + charH);//font size
				zpl.append("^FD" + aLine + "^FS");//line field
				aLine = brStream.readLine();
				i++;
			}
			zpl.append("^XZ");//end
			String labelLenght = "^LL" + charH * i;
			header+=labelLenght;
			String label = header+zpl;
			
			System.out.println(label);
			byte[] by = label.getBytes();
			Doc doc = new SimpleDoc(by, flavor, das);
			job.print(doc, pras);
			brStream.close();
			frStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PrintException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param printService
	 */
	private void printReversPages(JasperPrint jasperPrint, PrintService printService) {
		try {
			
			List pages = jasperPrint.getPages();
			for (int i = pages.size() - 1; i >= 0; i--) {
				
				JasperPrintManager.printPage(jasperPrint, i, false);
			}
			
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param printService
	 */
	private void getPrinter(PrintService printService) {
		System.out.println("Printer: " + printService.getName());
		System.out.println("Supported flavors:");
		DocFlavor[] flavors = printService.getSupportedDocFlavors();
		if (flavors != null) {
			for (DocFlavor flavor : flavors) {
				System.out.println(flavor);
			}
		}
		System.out.println("Attributes:");
		Attribute[] attributes = printService.getAttributes().toArray();
		if (attributes != null) {
			for (Attribute attr : attributes) {
				System.out.println(attr.getName() + ": " + (attr.getClass()).toString());
			}
		}
	}
}
