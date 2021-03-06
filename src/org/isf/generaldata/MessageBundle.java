/*
 * Created on 27/ott/07
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.generaldata;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBundle {
	
	private final static Logger logger = LoggerFactory.getLogger(MessageBundle.class);

	private static ResourceBundle resourceBundle = null;
	
	public static void initialize() throws RuntimeException {
		try {
			resourceBundle = ResourceBundle.getBundle("language", new Locale(GeneralData.LANGUAGE));
		} catch (MissingResourceException e) {
			logger.error(">> no resource bundle found.");
			System.exit(1);
			//throw new RuntimeException("no resource bundle found.");
		}
	}
	
	public static String getMessage(String key) {
		String message = "";
		
		try {
			if (resourceBundle != null) {
				//message = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				message = resourceBundle.getString(key);
			}
		} catch (MissingResourceException e) {
			message = key;
			logger.error(">> key not found: " + key);
		} /*catch (UnsupportedEncodingException e) {
			logger.error(">> language not supported.");
		}*/
		
		return message;
	}
	public static ResourceBundle getBundle(){
		return resourceBundle;
	}
}
