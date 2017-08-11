package org.bip.builder;

import org.bip.behaviour.BehaviourImpl;

import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PortType;

public class BIPPortBuilder {

	/**
	 * @param args
	 */
	private PortType bipPortType;
	private String portTypeName;
	
	
	
	public BIPPortBuilder(String typeName) {
		this.portTypeName = typeName;
		bipPortType = BIPTransform.createPortType(portTypeName);		
	
	}
	
	 public PortType getPortType() {
		 return bipPortType;
	 }


}
