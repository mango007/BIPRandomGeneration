import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.PartElementReference;

class RandomComponentParameterDescription{
	List<Integer> random = new ArrayList<Integer>();
	String value = null;
	List<String> group = new ArrayList<String>();
	RandomComponentParameterDescription(){}
	RandomComponentParameterDescription(RandomComponentParameterDescription rcpd){
		this.value = rcpd.value;
		for(Integer i: rcpd.random)
			this.random.add(i);
		for(String s: rcpd.group)
			this.group.add(s);
	}
}
class RandomComponentDescription{
	List<Integer> randomComponent = new ArrayList<Integer>();
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
	List<RandomComponentParameterDescription> par = new ArrayList<RandomComponentParameterDescription>();
	RandomComponentDescription(){}
	RandomComponentDescription(RandomComponentDescription rcd){
		this.name = rcd.name;
		for(Integer i: rcd.randomComponent)
			this.randomComponent.add(i);
		for(String s: rcd.group)
			this.group.add(s);
		for(RandomComponentParameterDescription rcpd: rcd.par){
			RandomComponentParameterDescription rrcpd = new RandomComponentParameterDescription();
			rrcpd.value = rcpd.value;
			for(Integer i: rcpd.random)
				rrcpd.random.add(i);
			this.par.add(rrcpd);
		}
	}
}
class RandomConnectorParameterDescription{
	List<Integer> random = new ArrayList<Integer>();
	String atom = null;
	String port = null;
	List<String> group = new ArrayList<String>();
	RandomConnectorParameterDescription(){}
	RandomConnectorParameterDescription(RandomConnectorParameterDescription rcpd){
		this.atom = rcpd.atom;
		this.port = rcpd.port;
		for(Integer i: rcpd.random)
			this.random.add(i);
		for(String s: rcpd.group)
			this.group.add(s);
	}
}
class RandomConnectorDescription{
	List<Integer> randomConnector = new ArrayList<Integer>();
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
	List<RandomConnectorParameterDescription> par= new ArrayList<RandomConnectorParameterDescription>();
	RandomConnectorDescription(){}
	RandomConnectorDescription(RandomConnectorDescription rcd){
		for(Integer i: rcd.randomConnector)
			this.randomConnector.add(i);
		this.name = rcd.name;
		for(String s: rcd.group)
			this.group.add(s);
		for(RandomConnectorParameterDescription rcpd: rcd.par){
			RandomConnectorParameterDescription rrcpd = new RandomConnectorParameterDescription();
			rrcpd.atom = rcpd.atom;
			rrcpd.port = rcpd.port;
			for(Integer i: rcpd.random)
				rrcpd.random.add(i);
			this.par.add(rrcpd);
		}
	}
}

public class RandomCompoundDescription {
	List<Integer> randomComponent = new ArrayList<Integer>();
	List<Integer> randomConnector = new ArrayList<Integer>();
	List<RandomComponentDescription> randomComponentDes = new ArrayList<RandomComponentDescription>();
	List<RandomConnectorDescription> randomConnectorDes = new ArrayList<RandomConnectorDescription>();
	Random randomGenerator = new Random();
	String originalCompoundType = null;
	boolean isRandom = false;
	public RandomCompoundDescription(String instuctionFile, String compoundName) throws Exception{
		
		Reader r = new BufferedReader(new FileReader(instuctionFile));
		String sCurrentLine;
		
		while ((sCurrentLine = ((BufferedReader)r).readLine()) != null) {
			String[] parser0 = sCurrentLine.split(" ");

			if(parser0.length > 0 && parser0[0].equals("compound:"+compoundName)){
				isRandom = true;
				if(parser0.length > 2)
					originalCompoundType = parser0[2];
				while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("end")){
					String[] parser = sCurrentLine.split(":");
					if(parser[0].equals("	RamdonGroup")){
						String[] parser1 = parser[1].split("#");
						/*if(!parser1[0].equals("	RamdonGroup")){
							System.out.println("You should specify your random group");
							return;
						}*/
						
						for(int i = 0; 2*i+1 < parser1.length; i++){
							BIPRandomBuilder.groupl.put(parser1[2*i+1], randomGenerator.nextInt(10)+1);
							//BIPRandomBuilder.groupl.put(parser1[2*i+1], 2);
						}
					}
					else if(parser[0].equals("	RandomComponent")){
						RandomComponentDescription rcd = new RandomComponentDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomComponent.add(Integer.parseInt(parser1[i]));
								rcd.randomComponent.add(Integer.parseInt(parser1[i]));
							}		
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rcd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		name")){
								if(parser2.length > 1)
									rcd.name = parser2[1];
							}
							else if(parser2[0].equals("		parameter")){
								RandomComponentParameterDescription rcpd = new RandomComponentParameterDescription();
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split(",");
									for(int i = 0; i < parser3.length; i++)
										rcpd.random.add(Integer.parseInt(parser3[i]));
								}
								while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			value")){
										if(parser3.length > 1){
											rcpd.value = parser3[1];
										}
									}
									else if(parser3[0].equals("			group")){
										if(parser3.length > 1){
											String[] parser4 = parser3[1].split("#");
											for(int i = 0; 2*i+1 < parser4.length; i++){
												rcpd.group.add(parser4[2*i+1]);
											}
										}
									}
									
								}
								rcd.par.add(rcpd);	
							}
							else if(parser2[0].equals("		type")){
								if(parser2.length > 1)
									rcd.type = parser2[1];
							}
						}
						randomComponentDes.add(rcd);	
					}
					else if(parser[0].equals("	RandomConnector")){
						RandomConnectorDescription rcd = new RandomConnectorDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomConnector.add(Integer.parseInt(parser1[i]));
								rcd.randomConnector.add(Integer.parseInt(parser1[i]));
							}		
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rcd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		name")){
								if(parser2.length > 1)
									rcd.name = parser2[1];
							}
							else if(parser2[0].equals("		parameter")){
								RandomConnectorParameterDescription rcpd = new RandomConnectorParameterDescription();
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split(",");
									for(int i = 0; i < parser3.length; i++)
										rcpd.random.add(Integer.parseInt(parser3[i]));
								}
								while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			atomic")){
										if(parser3.length > 1)
											rcpd.atom = parser3[1];
									}
									else if(parser3[0].equals("			port")){
										if(parser3.length > 1)
											rcpd.port = parser3[1];
									}
									else if(parser3[0].equals("			group")){
										if(parser3.length > 1){
											String[] parser4 = parser3[1].split("#");
											for(int i = 0; 2*i+1 < parser4.length; i++){
												rcpd.group.add(parser4[2*i+1]);
											}
										}
									}
										
								}
								rcd.par.add(rcpd);	
							}	
							else if(parser2[0].equals("		type")){
								if(parser2.length > 1)
									rcd.type = parser2[1];
							}
						}
						randomConnectorDes.add(rcd);
					}
				}
			}
		}
		r.close();
	}
	
	
	public void createCompoundRandomPart(CompoundType ct, Map<String, AtomType> atl, Map<String, ConnectorType> contl, Map<String, Integer> groupl,
			List<insComponent> coml, List<insConnector> cntl){
		for(RandomComponentDescription rcd: randomComponentDes){
			if(rcd.name == null)
				continue;
			if(! rcd.randomComponent.isEmpty())
				createRandomComponent(rcd, 0, groupl, atl, ct.getSubcomponent().get(rcd.randomComponent.get(0)), coml);
			else{
				createRandomComponent(rcd, 0, groupl, atl, null, coml);
			}
		}
		
		for(RandomConnectorDescription rcd: randomConnectorDes){
			if(rcd.name == null)
				continue;
			if(! rcd.randomConnector.isEmpty())
				createRandomConnector(rcd, 0, groupl, contl, ct.getConnector().get(rcd.randomConnector.get(0)), cntl);
			else
				createRandomConnector(rcd, 0, groupl, contl, null, cntl);
		}
		
	}
	void addRandomComponentParameter(RandomComponentParameterDescription rcpd, int position, Map<String, Integer> groupl, List<Object> para){
		if(rcpd.group.size() == position){
			if(rcpd.value.equals("true") || rcpd.value.equals("false"))
				para.add(Boolean.parseBoolean(rcpd.value));
			else
				para.add(Integer.parseInt(rcpd.value));
		}
		else{
			for(Integer i = 0; i < groupl.get(rcpd.group.get(position)); i++){
				RandomComponentParameterDescription rrcpd = new RandomComponentParameterDescription(rcpd);
				
				if(rrcpd.value != null)
					rrcpd.value = rrcpd.value.replaceAll("#"+rrcpd.group.get(position)+"#", i.toString());
				
				addRandomComponentParameter(rrcpd, position+1, groupl, para);
				
			}
		}
	}
	void createRandomComponent(RandomComponentDescription rcd, int position, Map<String, Integer> groupl, Map<String, AtomType> atl, Component c, List<insComponent> coml){
		if(rcd.group.size() == position){
			if(c != null){
				List<Object> para = new ArrayList<Object>();
				for(int i = 0; i < c.getActualData().size(); i++){
					boolean isContain = false;
					for(RandomComponentParameterDescription rcpd: rcd.par){
						if(rcpd.random.contains(i)){
							isContain = true;
							if(rcpd.random.get(0) == i){
								addRandomComponentParameter(rcpd, 0, groupl, para);
							}
							break;
						}
					}
					if(!isContain){
						if(c.getActualData().get(i) instanceof BooleanLiteral)
							para.add(((BooleanLiteral)c.getActualData().get(i)).isBValue());
						else if(c.getActualData().get(i) instanceof IntegerLiteral)
							para.add(((IntegerLiteral)c.getActualData().get(i)).getIValue());
					}
				}
				insComponent insc;
				if(rcd.type != null)
					insc = new insComponent(atl.get(rcd.type), rcd.name, para);
				else
					insc = new insComponent(atl.get(c.getType().getName()), rcd.name, para);
				coml.add(insc);
			}
			else{
				List<Object> para = new ArrayList<Object>();
				for(RandomComponentParameterDescription rcpd: rcd.par)
					addRandomComponentParameter(rcpd, 0, groupl, para);
				insComponent insc = new insComponent(atl.get(rcd.type), rcd.name, para);	
				coml.add(insc);		
			}
		}
		else{
			for(Integer i = 0; i < groupl.get(rcd.group.get(position)); i++){
				RandomComponentDescription rrcd = new RandomComponentDescription(rcd);
				if(rrcd.name != null)
					rrcd.name = rrcd.name.replaceAll("#"+rcd.group.get(position)+"#", i.toString());
				for(RandomComponentParameterDescription rcpd: rrcd.par)
					if(rcpd.value != null)
						rcpd.value = rcpd.value.replaceAll("#"+rcd.group.get(position)+"#", i.toString());
				createRandomComponent(rrcd, position+1, groupl, atl, c, coml);
			}
			
		}
	}
	
	void addRandomConnectorParameter(RandomConnectorParameterDescription rcpd, int position, Map<String, Integer> groupl, InnerPortReference innerPR, List<String> para){
		if(rcpd.group.size() == position){
			if(innerPR != null){
				StringBuffer sb = new StringBuffer();
				if(rcpd.atom != null)
					sb.append(rcpd.atom);
				else
					sb.append(innerPR.getTargetInstance().getTargetPart().getName());
				sb.append(".");
				if(rcpd.port != null)
					sb.append(rcpd.port);
				else
					sb.append(innerPR.getTargetPort().getName());
				para.add(sb.toString());
			}
			else{
				StringBuffer sb = new StringBuffer();
				if(rcpd.atom != null)
					sb.append(rcpd.atom);
				if(rcpd.port != null)
					sb.append(rcpd.port);
				para.add(sb.toString());
			}
		}
		else{
			for(Integer i = 0; i < groupl.get(rcpd.group.get(position)); i++){
				RandomConnectorParameterDescription rrcpd = new RandomConnectorParameterDescription(rcpd);
				
				if(rrcpd.atom != null)
					rrcpd.atom = rrcpd.atom.replaceAll("#"+rrcpd.group.get(position)+"#", i.toString());
				if(rrcpd.port != null)
					rrcpd.port = rrcpd.port.replaceAll("#"+rrcpd.group.get(position)+"#", i.toString());
				
				addRandomConnectorParameter(rrcpd, position+1, groupl, innerPR, para);
				
			}
		}
	}
	
	void createRandomConnector(RandomConnectorDescription rcd, int position, Map<String, Integer> groupl, Map<String, ConnectorType> contl, Connector c, List<insConnector> cntl){
		if(rcd.group.size() == position){
			if(c != null){
				List<String> para = new ArrayList<String>();
				for(int i = 0; i < c.getActualPort().size(); i++){
					boolean isContain = false;
					for(RandomConnectorParameterDescription rcpd: rcd.par){
						if(rcpd.random.contains(i)){
							isContain = true;
							if(rcpd.random.get(0) == i){
								addRandomConnectorParameter(rcpd, 0, groupl, (InnerPortReference)c.getActualPort().get(i), para);
							}
							break;
						}
					}
					if(!isContain){
						StringBuffer sb = new StringBuffer();
						sb.append(((InnerPortReference)c.getActualPort().get(i)).getTargetInstance().getTargetPart().getName());
						sb.append(".");
						sb.append(((InnerPortReference)c.getActualPort().get(i)).getTargetPort().getName());
						para.add(sb.toString());
					}
					
				}
				insConnector insc;
				if(rcd.type != null)
					insc = new insConnector(contl.get(rcd.type), rcd.name, para);
				else
					insc = new insConnector(contl.get(c.getType().getName()), rcd.name, para);
				cntl.add(insc);
			}
			else{
				List<String> para = new ArrayList<String>();
				for(RandomConnectorParameterDescription rcpd: rcd.par){
					addRandomConnectorParameter(rcpd, 0, groupl, null, para);
				}
				insConnector insc = new insConnector(contl.get(rcd.type), rcd.name, para);
				cntl.add(insc);
			}

		}
		else{
			for(Integer i = 0; i < groupl.get(rcd.group.get(position)); i++){
				RandomConnectorDescription rrcd = new RandomConnectorDescription(rcd);
				if(rrcd.name != null)
					rrcd.name = rrcd.name.replaceAll("#"+rcd.group.get(position)+"#", i.toString());
				for(RandomConnectorParameterDescription rcpd: rrcd.par){
					if(rcpd.atom != null)
						rcpd.atom = rcpd.atom.replaceAll("#"+rcd.group.get(position)+"#", i.toString());
					if(rcpd.port != null)
						rcpd.port = rcpd.port.replaceAll("#"+rcd.group.get(position)+"#", i.toString());
				}	
				createRandomConnector(rrcd, position+1, groupl, contl, c, cntl);
			}
		}
	}
}
