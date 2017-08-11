import java.util.List;

import ujf.verimag.bip.Core.Interactions.ConnectorType;


public class insConnector {
	ConnectorType cont;
	String name;
	List<String> ports;
	public insConnector(ConnectorType icont, String iname, List<String> iports){
		cont = icont;
		name = iname;
		ports = iports;
	}
}