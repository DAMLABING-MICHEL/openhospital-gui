package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPrinter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String FILE_PROPERTIES = "cardPrinter.properties";

	public static boolean USE_DEFAULT_PRINTER;
	public static String PRINTER;
	public static boolean PRINT_AFTER_NEW_PATIENT;
	public static boolean PRINT_AFTER_NEW_ADMISSION;
    public static boolean PRINT_WITHOUT_ASK;
    public static String CARD_FILE;
    public static String ADMISSION_FILE;
    
    private static boolean DEFAULT_USE_DEFAULT_PRINTER = true;
    private static boolean DEFAULT_PRINT_AFTER_NEW_PATIENT = false;
    private static boolean DEFAULT_PRINT_AFTER_NEW_ADMISSION = false;
    private static boolean DEFAULT_PRINT_WITHOUT_ASK = false;
    private static String DEFAULT_CARD_FILE = "opd_forms";
    private static String DEFAULT_ADMISSION_FILE = "admission_forms";
    
    private static CardPrinter mySingleData;
	private Properties p;
	
	public static CardPrinter getCardPrinter() {
        if (mySingleData == null){ 
        	mySingleData = new CardPrinter();        	
        }
        return mySingleData;
    }

    private CardPrinter() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + FILE_PROPERTIES));
			logger.info("File cardPrinter.properties loaded. ");
			USE_DEFAULT_PRINTER = myGetProperty("USE_DEFAULT_PRINTER", DEFAULT_USE_DEFAULT_PRINTER);
			if (!USE_DEFAULT_PRINTER) PRINTER = p.getProperty("USE_DEFAULT_PRINTER");
			PRINT_WITHOUT_ASK = myGetProperty("PRINT_WITHOUT_ASK", DEFAULT_PRINT_WITHOUT_ASK);
			PRINT_AFTER_NEW_PATIENT = myGetProperty("PRINT_AFTER_NEW_PATIENT", DEFAULT_PRINT_AFTER_NEW_PATIENT);
			PRINT_AFTER_NEW_ADMISSION = myGetProperty("PRINT_AFTER_NEW_ADMISSION", DEFAULT_PRINT_AFTER_NEW_ADMISSION);
			CARD_FILE = myGetProperty("CARD_FILE", DEFAULT_CARD_FILE);
			ADMISSION_FILE = myGetProperty("ADMISSION_FILE", DEFAULT_ADMISSION_FILE);
			
    	} catch (Exception e) {//no file
    		logger.error(">> " + FILE_PROPERTIES + " file not found.");
			System.exit(1);
		}
    }
    
	/**
	 * 
	 * Method to retrieve a boolean property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private boolean myGetProperty(String property, boolean defaultValue) {
		boolean value;
		try {
			value = p.getProperty(property).equalsIgnoreCase("YES");
		} catch (Exception e) {
			logger.warn(">> " + property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}

    /**
	 * 
	 * Method to retrieve a string property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private String myGetProperty(String property, String defaultValue) {
		String value;
		value = p.getProperty(property);
		if (value == null) {
			logger.warn(">> " + property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}
}

