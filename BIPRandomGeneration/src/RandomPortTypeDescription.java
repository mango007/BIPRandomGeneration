import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



class RandomPortTypeParameterDescription{
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
	RandomPortTypeParameterDescription(){}
	RandomPortTypeParameterDescription(RandomPortTypeParameterDescription rppd){
		this.name = rppd.name;
		this.type = rppd.type;
		for(String s: rppd.group)
			this.group.add(s);
	}
	
}


public class RandomPortTypeDescription {
	boolean isRandomType = false;
	String name = null;
	
	List<RandomPortTypeParameterDescription> rParDes = new ArrayList<RandomPortTypeParameterDescription>();
	
	RandomPortTypeDescription(String instuctionFile, String typeName) throws IOException{
		Reader r = new BufferedReader(new FileReader(instuctionFile));
		String sCurrentLine;
		while ((sCurrentLine = ((BufferedReader)r).readLine()) != null) {
			if(sCurrentLine.equals("Port:"+typeName)){
				name = typeName;
				isRandomType = true;
				while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("end")){
					String[] parser = sCurrentLine.split(":");
					if(parser[0].equals("	RandomParameter")){
						RandomPortTypeParameterDescription rpd = new RandomPortTypeParameterDescription();
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group") && parser2.length > 1){
								String[] parser3 = parser2[1].split("#");
								for(int i = 0; 2*i+1 < parser3.length; i++){
									rpd.group.add(parser3[2*i+1]);
								}
							}
							else if(parser2[0].equals("		name") && parser2.length > 1){
								rpd.name = parser2[1];
							}		
							else if(parser2[0].equals("		type") && parser2.length > 1){
								rpd.type = parser2[1];
							}
						}
						rParDes.add(rpd);
						
					}
				}
			}
		}
		r.close();
	}
	
	
	void createPortRandomPart(Map<String, Integer> groupl, List<String> ptParType, List<String> ptParName){
		for(RandomPortTypeParameterDescription rpd: rParDes){
			addParameter(rpd, 0, groupl, ptParType, ptParName);
		}
		
	}
	
	void addParameter(RandomPortTypeParameterDescription rpd, int position , Map<String, Integer> groupl, List<String> ptParType, List<String> ptParName){
		if(rpd.group.size() == position){
			ptParType.add(rpd.type);
			ptParName.add(rpd.name);
		}
		else{
			for(Integer i = 0; i < groupl.get(rpd.group.get(position)); i++){
				RandomPortTypeParameterDescription rrpd = new RandomPortTypeParameterDescription(rpd);
				if(rrpd.name != null)
					rrpd.name = rrpd.name.replaceAll("#"+rrpd.group.get(position)+"#", i.toString());
				addParameter(rrpd, position+1, groupl, ptParType, ptParName);
			}
		}
			
	}
}
