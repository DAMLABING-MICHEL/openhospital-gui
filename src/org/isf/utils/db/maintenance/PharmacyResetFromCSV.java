package org.isf.utils.db.maintenance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 
 */

/**
 * @author Mwithi
 * 
 */
public class PharmacyResetFromCSV {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

//		BufferedReader inputMainPharmacy = new BufferedReader(new FileReader(new File("Path\\to\\dump\\medicals_dump_09.02.2012.csv")));
//		Writer output = new BufferedWriter(new FileWriter(new File("Path\\to\\dump\\medicals_dump_09.02.2012.sql")));

//		BufferedReader inputWardPharmacy = new BufferedReader(new FileReader(new File("E:\\CUAMM\\Wolisso\\Pharmacy2014\\OPD INVENTORY REVISED.csv")));
//		Writer output2 = new BufferedWriter(new FileWriter(new File("E:\\CUAMM\\Wolisso\\Pharmacy2014\\OPD INVENTORY REVISED.sql")));

		BufferedReader inputPrices = new BufferedReader(new FileReader(new File("E:\\CUAMM\\Wolisso\\Pharmacy2014\\OPD PRICE REVISED.csv")));
		Writer output3 = new BufferedWriter(new FileWriter(new File("E:\\CUAMM\\Wolisso\\Pharmacy2014\\OPD PRICE REVISED.sql")));

		
		try {
			String line = null;
			String[] st = null;

//			output.write("UPDATE MEDICALDSR SET MDSR_INI_STOCK_QTI = 0, MDSR_IN_QTI = 0, MDSR_OUT_QTI = 0 WHERE 1;\n");
//			output.write("DELETE FROM MEDICALDSRSTOCKMOV;\n");
//			output.write("DELETE FROM MEDICALDSRWARD;\n");
//			output.write("DELETE FROM MEDICALDSRLOT;\n");
//
//			while ((line = inputMainPharmacy.readLine()) != null) {
//				System.out.println(line);
//				st = line.replace("\"", "").split(";");
//
//				output.write("UPDATE MEDICALDSR SET MDSR_INI_STOCK_QTI = " + st[1] + " WHERE MDSR_CODE = '" + st[0] + "';\n");
//
//			}
//
//			output2.write("DELETE FROM MEDICALDSRWARD;\n");
//
//			while ((line = inputWardPharmacy.readLine()) != null) {
//				System.out.println(line);
//				st = line.replace("\"", "").split(";");
//
//				output2.write("INSERT INTO MEDICALDSRWARD (MDSRWRD_WRD_ID_A, MDSRWRD_MDSR_ID, MDSRWRD_IN_QTI, MDSRWRD_OUT_QTI) VALUES ('D','" + st[0] + "', " + st[3] + ", 0);\n");
//
//			}
//			
			output3.write("DELETE FROM PRICES WHERE PRC_LST_ID = 1 AND PRC_GRP = 'MED';\n");

			while ((line = inputPrices.readLine()) != null) {
				System.out.println(line);
				st = line.replace("\"", "").split(";");

				output3.write("UPDATE PRICES SET PRC_PRICE = " + st[3] + " WHERE PRC_LST_ID = 1 AND PRC_GRP = 'MED' AND PRC_ITEM = " + st[0] + ";\n");

			}
		} finally {
//			inputMainPharmacy.close();
//			output.close();
//			inputWardPharmacy.close();
//			output2.close();
			inputPrices.close();
			output3.close();
		}

	}

}
