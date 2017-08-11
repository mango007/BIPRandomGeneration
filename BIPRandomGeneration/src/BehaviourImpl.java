import java.util.*;

import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
//import ujf.verimag.bip.Core.Behaviors.State;




public class BehaviourImpl {
	AtomType componentType;
	List<insPort> ports = new ArrayList<insPort>();
	List<String> states = new ArrayList<String>();
	//List<Variable> vars = new ArrayList<Variable>();
	List<ExecutorTransition> trans = new ArrayList<ExecutorTransition>();
	String currentState;
	Action initialAction;
	
	BehaviourImpl(AtomType icomponentType, List<insPort> iports, List<String> istates, List<ExecutorTransition> itrans, String icurrentState, Action iinitialAction){
		componentType = icomponentType;
	
		ports = iports;
		trans = itrans;
		states = istates;
		currentState = icurrentState;
		initialAction = iinitialAction;
	}
	BehaviourImpl(AtomType icomponentType, List<insPort> iports, List<String> istates, List<ExecutorTransition> itrans, String icurrentState){
		componentType = icomponentType;
	
		ports = iports;
		trans = itrans;
		states = istates;
		currentState = icurrentState;
		initialAction = null;
	}
	
	
	void addPort(insPort iport){
		ports.add(iport);
	}
	
	List<insPort> getAllPorts(){
		return ports;
	}
	
	String getCurrentState(){
		return currentState;
	}
	
	List<String> getStates(){
		return states;
	}
	
	List<ExecutorTransition> getAllTransitions(){
		return trans;
	}
}
