import java.util.List;

import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.Variable;


public class insPort {
	public static final class Type{
		public static final int internal = 0;
		public static final int export = 1;
	}
	
	int type;
	String id;
	PortType portType;
	List<Variable> exposedVariable;
	insPort(int itype, String iid, PortType iportType, List<Variable> iexposedVariable){
		type = itype;
		id = iid;
		portType = iportType;
		exposedVariable = iexposedVariable;
	}
	
}
