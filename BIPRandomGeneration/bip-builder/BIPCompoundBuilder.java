package org.bip.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bip.behaviour.BehaviourImpl;

import connectorsTransformers.Connector;

import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.PartElementReference;
import ujf.verimag.bip.Core.Modules.Module;

public class BIPCompoundBuilder {

	private static Module module; 
	private List<Component> bipComponents; 
	private CompoundType ct;
	private Map<BehaviourImpl,Integer> componentsAndInstances;
	private static Map<String, InnerPortReference> portRefMap = new HashMap<String, InnerPortReference>();
	private List<Connector> cl;

	//component instances should be passed to the constructor in the form of a map containing behaviour implementations and the number of the corresponding instances
	public BIPCompoundBuilder(Map<BehaviourImpl,Integer> componentTypeInstances, Module m, List<Connector> cl, String compoundType, String compoundName) {
		this.ct = BIPTransform.createCompound(m, compoundType, compoundName);
		this.module = m; 
		this.componentsAndInstances = componentTypeInstances;
		this.cl=cl;
		buildComponents();
		Convert(this.cl);
		m.getBipType().add(ct);
	}
	
	private void buildComponents()
	{
		bipComponents = new LinkedList<Component>();
		for(BehaviourImpl behav: componentsAndInstances.keySet()) {
			BIPAtomBuilder atomBuilder = new BIPAtomBuilder(behav, module);

			int nbOfComponent = componentsAndInstances.get(behav); 
			for(int i = 0; i < nbOfComponent; i++) {
				Component c = BIPTransform.createComponent(atomBuilder.getAtomType(), atomBuilder.getAtomType().getName()+"_"+i);
				buildPortMap(c);
				
				ct.getSubcomponent().add(c);
				
			}		
		}

	}
	
	public Module getModule()
	{
		return module;	
	}
	public CompoundType getCompoundType()
	{
		return ct;
	}
	
	private void buildPortMap(Component c)
	{
		for (ujf.verimag.bip.Core.Behaviors.Port p: c.getType().getPort())
		{
			 InnerPortReference ipr = InteractionsFactory.eINSTANCE.createInnerPortReference(); 
			 PartElementReference PE = InteractionsFactory.eINSTANCE.createPartElementReference();
			 PE.setTargetPart(c);
			 ipr.setTargetPort(p);
			 ipr.setTargetInstance(PE);
			 portRefMap.put(c.getType().getName()+"."+p.getName(), ipr);
		}
	}
	
	private void Convert(List<Connector> cl) {
		 for (int i=0;i<cl.size();i++)
		 {
			if (cl.get(i).root.data.snd() ==null) {
				
				ConnectorType ct = BIPTransform.createConnectorType("connectorType_"+i,GeneralBuilder.getSyncPortType(module),cl.get(i).root.children.get(0).data.snd().size());
				
				List<Integer> trigg = new ArrayList<Integer>();
				for (int j =0; j < cl.get(j).root.children.size(); j++) 
					if (cl.get(j).root.children.get(j).data.fst()) {trigg.add(j);}
				BIPTransform.setConnectorTypeDefinition(ct, trigg);
				module.getBipType().add(ct);
				ujf.verimag.bip.Core.Interactions.Connector c = BIPTransform.createConnector(ct, "connector_"+i);
				for (String t : cl.get(i).root.children.get(0).data.snd())
					c.getActualPort().add(portRefMap.get(t));
				
				
	
				this.ct.getConnector().add(c);
			/*	for(int i =0; i < cn.children.size(); i++)
					Convert(cn.children.get(i),m);*/
			}
			else {
				
				//createConnectorType(cn.data.snd().size());
			}
		 }
		}
}
