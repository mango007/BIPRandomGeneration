package org.bip.builder;
import java.util.HashMap;
import java.util.Map;
import org.bip.behaviour.BehaviourImpl;
import org.bip.behaviour.Port;

import ujf.verimag.bip.Core.ActionLanguage.Expressions.FunctionCallExpression;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Behaviors.PortDefinition;
import ujf.verimag.bip.Core.Behaviors.State;
import ujf.verimag.bip.Core.Behaviors.Transition;
import ujf.verimag.bip.Core.Modules.Module;


public class BIPAtomBuilder {
	
	private BehaviourImpl behaviourInput; 
	private AtomType bipAtomType; 
	private PetriNet bipBehaviour; 
	private Module module; 
	private Map<String, State> mapNameState = new HashMap<String, State>();
	

	
	public BIPAtomBuilder(BehaviourImpl behaviour, Module m) {
		this.module = m; 
		this.behaviourInput = behaviour;
		this.bipAtomType = BIPTransform.createAtomType(behaviour.getComponentType());
		this.bipBehaviour = BIPTransform.createPetriNet(bipAtomType);
		buildPorts();
		buildStates();
		buildTransitions();
		module.getBipType().add(bipAtomType);
	}
	

	private void buildPorts() {
		
		for(Port port: behaviourInput.getAllPorts()) {
			//System.out.println(port.id);
			if(port.type == Port.Type.internal)
				BIPTransform.createAtomPort(bipAtomType, port.id, GeneralBuilder.getSyncPortType(module) , false);
			else
				BIPTransform.createAtomPort(bipAtomType, port.id, GeneralBuilder.getSyncPortType(module) , true);
		
		}
	}
	
	private PortDefinition getPortDefinition(org.bip.behaviour.ExecutorTransition executorTransition) {
		for(PortDefinition pd: bipAtomType.getPortDefinition()) {
			if(pd.getName().equals(executorTransition.name()))
				return pd;
		}
		return null;
	}

	private void buildTransitions() {
	
		for(org.bip.behaviour.Transition transition: behaviourInput.getAllTransitions()) {
			if(transition instanceof org.bip.behaviour.ExecutorTransition) {
				org.bip.behaviour.ExecutorTransition executorTransition = (org.bip.behaviour.ExecutorTransition) transition;
				PortDefinition pd = getPortDefinition(executorTransition);
				Transition bipTransition = BIPTransform.createTransition(mapNameState.get(executorTransition.source()), mapNameState.get(executorTransition.target()), pd);

				//set guards
				if (!((org.bip.behaviour.ExecutorTransition) executorTransition).guard().isEmpty()){
				FunctionCallExpression fc = BIPTransform.createFunctionCallExpression(((org.bip.behaviour.ExecutorTransition) executorTransition).guard());
				bipTransition.setGuard(fc);
				}
				bipBehaviour.getTransition().add(bipTransition);
			}								
		}	
	}


	private void buildStates() {
		bipBehaviour.getInitialState().add(BIPTransform.createState(behaviourInput.getCurrentState()));
		for(String location: behaviourInput.getStates()) {
			State state = BIPTransform.createState(location);
			bipBehaviour.getState().add(state);
			mapNameState.put(location, state);
		}
	}

	 public AtomType getAtomType() {
		 return bipAtomType;
	 }
	



}
