

//import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.Expression;



public class ExecutorTransition {
	String name;
	String source;
	String target;
	Expression guard = null;
	Action action = null;
	Boolean hasGuard;
	Boolean hasAction;
	
	ExecutorTransition(String iname, String isource, String itarget, Expression iguard, Action iaction){
		name = iname;
		source = isource;
		target = itarget;
		guard = iguard;
		action = iaction;
		hasGuard = true;
		hasAction = true;
	}
	
	ExecutorTransition(String iname, String isource, String itarget, Action iaction){
		name = iname;
		source = isource;
		target = itarget;
		action = iaction;
		hasGuard = false;
		hasAction = true;
	}
	
	ExecutorTransition(String iname, String isource, String itarget, Expression iguard){
		name = iname;
		source = isource;
		target = itarget;
		guard = iguard;
		hasGuard = true;
		hasAction = false;
	}
	
	ExecutorTransition(String iname, String isource, String itarget){
		name = iname;
		source = isource;
		target = itarget;
		hasGuard = false;
		hasAction = false;
	}
	
	
}
