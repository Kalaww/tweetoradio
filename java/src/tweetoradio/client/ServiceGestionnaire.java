package tweetoradio.client;

import tweetoradio.util.*;
import tweetoradio.diffuseur.InfoDiffuseur;

import java.lang.Runnable;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServiceGestionnaire implements Runnable{

	/**
	 * Référence dy cient
	 */
	private Client client;
	
	/**
	 * Constructeur
	 * @param  _client référence du client
	 */
	public ServiceGestionnaire(Client _client){
		client = _client;
	}

	public void run(){
		Socket socket = null;
		
		try{
			socket = new Socket(client.getIpGestionnaire(), client.getPortGestionnaire());
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			return;
		}

		Log.printLog("[Service Gestionnaire] Connexion avec "+client.getIpGestionnaire()+":"+client.getPortGestionnaire());


		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}

		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}


		String send;
		send = MessageType.LIST+"\r\n";
		Log.printDebug("[Service Gestionnaire] Envoi de: "+send.substring(0, send.length()-2));
		pw.print(send);
		pw.flush();

		String rep = null;
		try{
			rep = br.readLine();
		}catch (IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				br.close();
				pw.close();
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}

		Log.printDebug("[Service Gestionnaire] Réception de: "+rep);
		rep += "\r\n";

		MessageParser mp = null;
		try{
			mp = Parse.parser(rep);
		}catch (TypeMessageInconnuException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			mp = new MessageParser();
		}

		if(mp.type.equals(MessageType.LINB)){
			ArrayList<InfoDiffuseur> list = client.getDiffuseurs();
			list.clear();

			for(int i = 0; i < mp.nbDiff; i++){
				rep = null;
				try{
					rep = br.readLine();
				}catch (IOException e){
					Log.printLog("[Service Gestionnaire] "+e.getMessage());
					try{
						br.close();
						pw.close();
						socket.close();
					}catch(IOException ee){
						Log.printLog("[Service Gestionnaire] "+ee.getMessage());
					}
					continue;
				}

				Log.printDebug("[Service Gestionnaire] Réception de: "+rep);
				rep += "\r\n";

				MessageParser mpp = null;
				try{
					mpp = Parse.parser(rep);
				}catch(TypeMessageInconnuException e){
					Log.printLog("[Service Gestionnaire] "+e.getMessage());
					mpp = new MessageParser();
				}

				if(mpp.type.equals(MessageType.ITEM)){
					InfoDiffuseur inf = new InfoDiffuseur();
					inf.id = mpp.id;
					inf.ipMachine = mpp.ipMachine;
					inf.portMachine = mpp.portMachine;
					inf.ipMultiDiffusion = mpp.ipMultiDiffusion;
					inf.portMultiDiffusion = mpp.portMultiDiffusion;

					list.add(inf);
				}else{
					Log.printLog("[Service Gestionnaire] Le service attend un message de type ITEM");
				}
			}

		}else{
			Log.printLog("[Service Gestionnaire] Le service attend un message de type LINB");
		}

		try{
			br.close();
			pw.close();
			socket.close();
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
		}		
	}
}