package org.bip.builder;

import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Modules.Module;

public class GeneralBuilder {
	private static PortType syncPortType = null; 
	


	
	public static PortType getSyncPortType(Module m) {
		if (syncPortType == null) {
			syncPortType = BIPTransform.createPortType("Sync"); 
		}
		m.getBipType().add(syncPortType);
		return syncPortType; 
	}
	
	

}
