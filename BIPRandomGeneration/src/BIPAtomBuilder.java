
import java.util.HashMap;
import java.util.Map;


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
		this.bipAtomType = behaviourInput.componentType;
		
		this.bipBehaviour = BIPTransform.createPetriNet(bipAtomType);
		//buildVariables();
		buildPorts();
		buildStates();
		if(behaviourInput.initialAction != null)
			bipBehaviour.setInitialization(behaviourInput.initialAction);
		buildTransitions();
		module.getBipType().add(bipAtomType);
	}
	
	private void buildPorts() {
		
		for(insPort port: behaviourInput.getAllPorts()) {
			//System.out.println(port.id);
			
			if(port.type == insPort.Type.internal)
				BIPTransform.createAtomPort(bipAtomType, port.id, port.portType , false, port.exposedVariable);
			else
				BIPTransform.createAtomPort(bipAtomType, port.id, port.portType , true, port.exposedVariable);
		
		}
	}
	
	private PortDefinition getPortDefinition(ExecutorTransition executorTransition) {
		for(PortDefinition pd: bipAtomType.getPortDefinition()) {
			if(pd.getName().equals(executorTransition.name))
				return pd;
		}
		return null;
	}

	private void buildTransitions() {
		Transition bipTransition;
		
		for(ExecutorTransition transition: behaviourInput.getAllTransitions()) {
			
			
			//ExecutorTransition executorTransition = (ExecutorTransition) transition;
			PortDefinition pd = getPortDefinition(transition);
			
			bipTransition = BIPTransform.createTransition(mapNameState.get(transition.source), mapNameState.get(transition.target), pd);

			//set guards
			if (transition.guard != null){
				//BinaryExpression b = BIPTransform.createBinaryExpression(executorTransition.guard());
				bipTransition.setGuard(transition.guard);
			}
			if (transition.action != null){
				//System.out.println(transition.action);
				bipTransition.setAction(transition.action);
				//System.out.println(((CompositeAction)bipTransition.getAction()).getContent().get(0));
			}
			bipBehaviour.getTransition().add(bipTransition);
											
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
