package tweetoradio.client;

import tweetoradio.util.*;

import java.lang.Runnable;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.UnknownHostException;

public class ServiceDiffusion implements Runnable{
	
	private Client client;

	private boolean alive;

	private MulticastSocket socket;

	public ServiceDiffusion(Client _client){
		client = _client;
		alive = true;
	}


	public void run(){
		socket = null;
		try{
			socket = new MulticastSocket(client.getInfoDiffuseurCourant().portMultiDiffusion);
		}catch(IOException e){
			Log.printLog("[Service Diffusion] "+e.getMessage());
			return;
		}

		try{
			socket.joinGroup(InetAddress.getByName(client.getInfoDiffuseurCourant().ipMultiDiffusion));
		}catch(UnknownHostException e){
			Log.printLog("[Service Diffusion] "+e.getMessage());
			return;
		}catch(IOException e){
			Log.printLog("[Service Diffusion] "+e.getMessage());
			return;
		}

		byte[] data = new byte[MessageType.SIZE_DIFF];

		DatagramPacket paquet = null;
		paquet = new DatagramPacket(data, data.length);

		while(alive){
			try{
				socket.receive(paquet);
			}catch(IOException e){
				Log.printLog("[Service Diffusion] "+e.getMessage());
				continue;
			}
			
			String recv = new String(paquet.getData(), 0, paquet.getLength());
			Log.printDebug("[Service Diffusion] Recu message:\n"+recv.substring(0, recv.length()-2));

			MessageParser mp = null;
			try{
				mp = Parse.parser(recv);
			}catch(TypeMessageInconnuException e){
				Log.printLog("[Service Diffusion] "+e.getMessage());
				mp = new MessageParser();
			}

			if(mp.type.equals(MessageType.DIFF)){
				Log.print2(new Message(mp.id, mp.contenu).afficher());
			}else{
				Log.printLog("[Service Diffusion] Ce service ne gère que des messages de type DIFF");
			}
		}
	}

	public void close(){
		alive = false;
		if(socket != null) socket.close();
	}
}