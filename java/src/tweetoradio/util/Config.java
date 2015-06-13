package tweetoradio.util;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Config{
	
	public static HashMap<String, String> read(String filename){
		HashMap<String, String> map = new HashMap<String, String>();

		BufferedReader br = null;
		try{
			br =  new BufferedReader(new FileReader(filename));
		}catch(IOException e){
			System.out.println("Impossible de lire le fichier "+filename+"\n"+e.getMessage());
			return map;
		}

		String line = "";
		
		while(true){
			try{
				line = br.readLine();
			}catch(IOException e){
				System.out.println("Impossible de lire le fichier "+filename+"\n"+e.getMessage());
				break;
			}

			if(line == null)
				break;

			String[] split = line.split("=");
			if(split.length == 2 && split[1].length() > 0)
				map.put(split[0], split[1]);
		}

		try{
			br.close();
		}catch(IOException e){
			System.out.println("Impossible de fermer le fichier "+filename+"\n"+e.getMessage());
		}

		return map;
	}
}