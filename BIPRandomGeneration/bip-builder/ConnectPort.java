package org.bip.builder;
import java.util.ArrayList;
import java.util.List;

import org.bip.behaviour.Port;

public class ConnectPort {

	private ArrayList<Port> requiredPorts;
	private ArrayList<Port> acceptedPorts;
	private ArrayList<ConnectPort> irrelevantPorts;
	private String name;
	
	public ConnectPort(Port p)
	{
		this.requiredPorts = new ArrayList<Port>();
		this.acceptedPorts = new ArrayList<Port>();
		this.irrelevantPorts = new ArrayList<ConnectPort>();
		this.name = p.specType + "."+p.id;
	}
	

	
	public String name()
	{
		return name;
	}
	
	public void addIrrelevantPort (ConnectPort p)
	{
		this.irrelevantPorts.add(p);
	}
	
	public void setRequiredPorts(ArrayList<Port> rp)
	{
		this.requiredPorts = rp;
	}
	
	public void setAcceptedPorts(ArrayList<Port> ap)
	{
		this.acceptedPorts = ap;
	}
	
	
	public ArrayList<Port> getRequiredPorts()
	{
		return requiredPorts;
		
	}
	
	public ArrayList<Port> getAcceptedPorts()
	{
		return acceptedPorts;
		
	}
	
	public List<String> getAcceptedPortStrings()
	{
		List<String> aps = new ArrayList<String>();
		for (Port p:acceptedPorts)
		{
			aps.add(p.specType+"."+p.id);
		}	
		return aps;
	}
	
	public ArrayList<ConnectPort> getIrrelevantPorts()
	{
		return irrelevantPorts;
		
	}

	public void removeAcceptConstraint(int i)
	{
		this.acceptedPorts.remove(i);
	}
	
	public List<String> getAcceptedPortsStrings()
	{
		List<String> acceptedPortStrings = new ArrayList<String>();
		for (Port p:acceptedPorts)
			acceptedPortStrings.add(p.specType+"."+p.id);
		return acceptedPortStrings;
	}
	
}
