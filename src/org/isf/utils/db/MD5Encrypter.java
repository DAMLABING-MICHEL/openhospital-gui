/**
 * 
 */
package org.isf.utils.db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Mwithi
 *
 */
public class MD5Encrypter {

	private String md5;
	
	/**
	 * 
	 */
	public MD5Encrypter(String password) {
		super();
		this.md5 = md5(password);
	}

	private String md5(String passwd) {
		String md5 = null;
		try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwd.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            md5 = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
		return md5;
	}
	
	public String getMd5() {
		return md5;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MD5Encrypter md5 = new MD5Encrypter("ciao");
		System.out.println(md5.getMd5());
	}

}
