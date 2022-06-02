package org.isf.utils.time;

import org.isf.ward.model.Ward;

public class RememberOpdWard {
	private static Ward lastOpdWard=null;
	
	public static Ward getLastOpdWard() 	{
		return lastOpdWard;
	}
	public static void setLastOpdWard(Ward ward) 	{
		lastOpdWard=ward;
	}
}
