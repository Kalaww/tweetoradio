package tweetoradio.diffuseur;

public class InfoDiffuseur{
	
	public String id;

	public String ipMachine;

	public int portMachine;

	public String ipMultiDiffusion;

	public int portMultiDiffusion;

	public String toString(){
		return "id: "+id+" machine: "+ipMachine+":"+portMachine+" multiDiff: "+ipMultiDiffusion+":"+portMultiDiffusion;
	}

}