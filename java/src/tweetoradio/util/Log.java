package tweetoradio.util;

import java.io.*;

public abstract class Log{
	
	public static boolean DEBUG = false;

	public static String OUT_1 = "";

	public static String OUT_2 = "";

	public static String LOG_OUT = "";

	public static String DEBUG_OUT = "";

	public static void print1(String message){
		if(OUT_1.equals(""))
			System.out.println(message);
		else
			printFile(message, OUT_1);
	}

	public static void print2(String message){
		if(OUT_2.equals(""))
			System.out.println(message);
		else
			printFile(message, OUT_2);
	}

	public static void printLog(String message){
		if(LOG_OUT.equals(""))
			System.out.println("[LOG] "+message);
		else
			printFile("[LOG] "+message, LOG_OUT);
	}

	public static void printDebug(String message){
		if(!DEBUG)
			return;
		if(DEBUG_OUT.equals(""))
			System.out.println("[DEBUG] "+message);
		else
			printFile("[DEBUG] "+message, DEBUG_OUT);
	}

	private static void printFile(String message, String file){
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(file);
		}catch(IOException e){
			System.out.println("Impossbile d'ouvrir "+file+" en écriture");
		}

		pw.println(message);
		pw.flush();
		pw.close();
	}
}