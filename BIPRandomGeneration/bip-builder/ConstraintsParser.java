package org.bip.builder;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.bip.behaviour.Port;
import org.bip.glue.Accepts;
import org.bip.glue.BIPGlue;
import org.bip.glue.Requires;
import org.bip.util.BIPGlueParser;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import com.sun.tools.jdi.LinkedHashMap;





public class ConstraintsParser {

	public static BIPGlue fromFile;
	public String booleanFunction;
	private static ArrayList<ConnectPort> portlist = new ArrayList<ConnectPort>();
	private static Map<String, ConnectPort> portMap = new LinkedHashMap();
	
	
	private static BIPGlue createExample()
	{
		
		Port a = new Port();
		Port b = new Port();
		Port c = new Port();
		Port d = new Port();
		a.id = "a";
		b.id = "b";
		c.id = "c";
		d.id = "d";
		
		a.specType = "example";
		b.specType = "example";
		c.specType = "example";
		d.specType = "example";
		
		ArrayList<Port> ar = new ArrayList<Port>();
		ar.add(b);
		ArrayList<Port> aa = new ArrayList<Port>();
		aa.add(c);
		aa.add(b);
		aa.add(d);
		
		ArrayList<Port> br = new ArrayList<Port>();
		br.add(a);
		br.add(c);
		ArrayList<Port> ba = new ArrayList<Port>();
		ba.add(a);
		ba.add(c);
		
		ArrayList<Port> cr = new ArrayList<Port>();
		cr.add(a);
		ArrayList<Port> ca = new ArrayList<Port>();
		ca.add(a);
		ca.add(b);
		
		Requires ra = new Requires();
		ra.effect = a;
		ra.causes = ar;
		
		Requires rb = new Requires();
		rb.effect = b;
		rb.causes = br;
		
		Requires rc = new Requires();
		rc.effect = c;
		rc.causes = cr;
		
		Accepts xa = new Accepts();
		xa.effect = a;
		xa.causes = aa;
		
		Accepts xb = new Accepts();
		xb.effect = b;
		xb.causes = ba;
		
		Accepts xc = new Accepts();
		xc.effect = c;
		xc.causes = ca;
		
		ArrayList<Accepts> acceptConstraints = new ArrayList<Accepts>();
		acceptConstraints.add(xa);
		acceptConstraints.add(xb);
		acceptConstraints.add(xc);
		
		ArrayList<Requires> requireConstraints = new ArrayList<Requires>();
		requireConstraints.add(ra);
		requireConstraints.add(rb);
		requireConstraints.add(rc);
		
		BIPGlue bg = new BIPGlue(acceptConstraints, requireConstraints);
		return bg;
	}
	
	public ConstraintsParser(String fileName){
		this.fromFile = read(fileName);
		getAcceptsConstraints();
		getRequiresConstraints();
		addIrrelvantPorts();
		this.booleanFunction=createBooleanExpression();
		
		
	}
	
	private static BIPGlue read (String fileName){
	
		try {
			fromFile = BIPGlueParser.toGlue(new FileInputStream(new File(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return fromFile;	
	}
	
	private void getAcceptsConstraints()
	{
		for (Accepts a:fromFile.acceptConstraints){
			ConnectPort cp = new ConnectPort(a.effect);
			cp.setAcceptedPorts(a.causes);
			portMap.put(cp.name(), cp); //to avoid duplicates
			portlist.add(cp);
		}	
	}
	
	private void getRequiresConstraints()
	{
		for (Requires r:fromFile.requiresConstraints){
			ConnectPort cp = new ConnectPort(r.effect);
			cp = portMap.get(r.effect.specType+"."+r.effect.id);
			cp.setRequiredPorts(r.causes);
			portMap.put(cp.name(), cp);
		
		}
	}
	
	private void addIrrelvantPorts()
	{
		for(ConnectPort cp:portlist)
			for (ConnectPort cp1:portlist)
				if (!cp.getAcceptedPortStrings().contains(cp1.name()) && !cp.name().equals(cp1.name()))
					cp.addIrrelevantPort(cp1);			
	}
	
	private void removeDuplicateConstraints()
	{
		for (ConnectPort cp:portMap.values())
		{
			ConnectPort ncp = cp;
			for (int i=0;i<ncp.getAcceptedPorts().size();i++)
			{
				for(Port p:ncp.getRequiredPorts())
				{
					if(ncp.getAcceptedPorts().get(i).id.equals(p.id) && ncp.getAcceptedPorts().get(i).specType.equals(p.specType))
						ncp.removeAcceptConstraint(i);
					
				}
				
				
			}
			portMap.put(ncp.name(), ncp);
		}
		
	}
	
	
	
	private void printConstraints()
	{
		for (ConnectPort cp: portMap.values())
		{		
			System.out.println(cp.name());
			System.out.println("requries:");
			for (Port p: cp.getRequiredPorts())
				System.out.println(p.specType+"."+p.id);
			System.out.println("and accepts:");
			for (Port p:cp.getAcceptedPorts())
				System.out.println(p.specType+"."+p.id);
			System.out.println("====================================================");
		}
		
	}

	private String createBooleanExpression()
	{
		String f="";
		for (ConnectPort cp:portlist)
		{
			if (!cp.getRequiredPorts().isEmpty())
			{
				f=f+("("+cp.name()+"->");
				for (Port req:cp.getRequiredPorts())
					f=f+(req.specType+"."+req.id+"&");
				
				f=f.substring(0, f.length()-1);
				f=f+")&";
				
			}
			if (!cp.getIrrelevantPorts().isEmpty())
			{
				f=f+("("+cp.name()+"->");
				for(ConnectPort irr:cp.getIrrelevantPorts())
					f=f+("!"+irr.name()+"&");
				
				f=f.substring(0, f.length()-1);
				f=f+")&";
	
			}
			
		}
		f=f.substring(0, f.length()-1);
		return f;
	}
	
	private String createFunction()
	{
		String f = "";
		for(ConnectPort cp:portlist)
		{
			if(!cp.getRequiredPorts().isEmpty())
			{
			String req="";
			for(Port p:cp.getRequiredPorts())
				req=req+p.specType+"."+p.id+"*";
	
			req = req.substring(0, req.length()-1);
			req=cp.name()+"*"+req;
			f=f+req+"+";
			String[] acc = listToArray(cp.getAcceptedPortsStrings());
			ICombinatoricsVector<String> initialVector = Factory.createVector(
			acc);
			for (int i=1;i<initialVector.getSize()+1;i++){
			Generator<String> gen = Factory.createSimpleCombinationGenerator(initialVector, i);

			for (ICombinatoricsVector<String> combination : gen) {
				String a="";
				for (int j=0; j<combination.getSize();j++)
				{
					a=a+combination.getValue(j)+"*";			
				}
				a=a.substring(0, a.length()-1);
				f=f+req+"*"+a;
				f=f+"+";
			
			   }		 
			}
			}
		}
		f=f.substring(0, f.length()-1);
		return f;
	}

	
	private String[] listToArray (List<String> list)
	{
		String[] sarray = new String[list.size()];
		sarray = list.toArray(sarray);
		
		return sarray;
	}
	
	public String getBooleanFunction()
	{
		return booleanFunction;
	}

}



