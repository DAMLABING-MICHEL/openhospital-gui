package org.isf.test;

import java.util.ArrayList;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.menu.model.UserMenuItem;

public class UMI {
	private  static UserBrowsingManager manager = new UserBrowsingManager();
	private static User myUser=new User("admin","admin","21232f297a57a5a743894a0e4a801fc3","administrator");
	private static ArrayList<UserMenuItem> umi = null;
	public static void main(String[] args) {
		umi = manager.getMenu(myUser);
		for(UserMenuItem u: umi)
		System.out.println("userMenuItem size :" +u.getAltLabel());
	}
}
