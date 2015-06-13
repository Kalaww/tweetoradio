package tweetoradio.diffuseur;

import tweetoradio.util.*;

import java.util.HashMap;
import java.util.Map.Entry;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Diffuseur
 */
public class Diffuseur{
	

	/**
	 * Identifiant sur 8 caracteres
	 */
	private String id;

	/**
	 * Port de communication avec les clients
	 */
	private int port;

	/**
	 * Addresse IPv4 de multi-diffusion
	 */
	private String ipMultiDiffusion;
	
	/**
	 * Port de multi-diffusion
	 */
	private int portMultiDiffusion;

	/**
	 * Ip du gestionnaire
	 */
	private String ipGestionnaire;

	/**
	 * Port du gestionnaire
	 */
	private int portGestionnaire;

	/**
	 * Liste des messages à diffuser
	 */
	private MessageList messages;

	/**
	 * MAIN Diffuseur
	 * @param args args
	 */
	public static void main(String[] args){
		Diffuseur d = new Diffuseur();

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-c")){
				HashMap<String, String> map = Config.read(args[++i]);
				for(Entry<String, String> entry : map.entrySet()){
					String k = entry.getKey();
					String v = entry.getValue();
					if(k.equals("id"))
						d.id = v;
					else if(k.equals("port_tcp"))
						d.port = Integer.parseInt(v);
					else if(k.equals("ip_diffusion"))
						d.ipMultiDiffusion = v;
					else if(k.equals("port_diffusion"))
						d.portMultiDiffusion = Integer.parseInt(v);
					else if(k.equals("ip_gestionnaire"))
						d.ipGestionnaire = v;
					else if(k.equals("port_gestionnaire"))
						d.portGestionnaire = Integer.parseInt(v);
					else if(k.equals("debug") && (v.equals("1") || v.equals("true")))
						Log.DEBUG = true;
					else if(k.equals("out_log"))
						Log.LOG_OUT = v;
					else if(k.equals("out_debug"))
						Log.DEBUG_OUT = v;
				}
			}
		}

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-id"))
				d.id = args[++i];
			else if(args[i].equals("-p"))
				d.port = Integer.parseInt(args[++i]);
			else if(args[i].equals("-ipD"))
				d.ipMultiDiffusion = args[++i];
			else if(args[i].equals("-pD"))
				d.portMultiDiffusion = Integer.parseInt(args[++i]);
			else if(args[i].equals("-ipG"))
				d.ipGestionnaire = args[++i];
			else if(args[i].equals("-pG"))
				d.portGestionnaire = Integer.parseInt(args[++i]);
			else if(args[i].equals("-d"))
				Log.DEBUG = true;
			else if(args[i].equals("-outL"))
				Log.LOG_OUT = args[++i];
			else if(args[i].equals("-outD"))
				Log.DEBUG_OUT = args[++i];
		}

		d.getMessagesList().addMessage(d.id, "Premier message");
		d.getMessagesList().addMessage(d.id, "Second message");

		d.start();
	}

	/**
	 * Constructeur
	 */
	public Diffuseur(){
		messages = new MessageList();
	}

	/**
	 * Demarre le diffuseur
	 */
	public void start(){
		Thread threadMultiDiff = new Thread(new ServiceMultiDiffusion(this));
		Thread threadModeClient = new Thread(new ServiceClient(this));
		Thread threadGestionnaire = new Thread(new ServiceGestionnaire(this, ipGestionnaire, portGestionnaire));

		threadMultiDiff.start();
		threadModeClient.start();
		threadGestionnaire.start();

	}

	public MessageList getMessagesList(){
		return messages;
	}

	public String getID(){
		return id;
	}

	public String getIP(){
		try{
			return InetAddress.getLocalHost().getHostAddress();
		}catch(UnknownHostException e){
			Log.printLog("[Diffuseur] "+e.getMessage());
		}
		return "";
	}

	public int getPort(){
		return port;
	}

	public String getIPMultiDiffusion(){
		return ipMultiDiffusion;
	}

	public int getPortMultiDiffusion(){
		return portMultiDiffusion;
	}
	
}