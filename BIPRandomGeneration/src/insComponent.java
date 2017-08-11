

import java.util.ArrayList;
import java.util.List;

import ujf.verimag.bip.Core.Behaviors.AtomType;



public class insComponent {
	AtomType at = null;
	String name = null;
	List<Object> ParValue = new ArrayList<Object>();
	boolean hasPar;
	public insComponent(AtomType iat, String iname, Integer iPar){
		at = iat;
		name = iname;
		ParValue.add(iPar);
		hasPar = true;
	}
	
	public insComponent(AtomType iat, String iname){
		at = iat;
		name = iname;
		hasPar = false;
	}
	
	public insComponent(AtomType iat, String iname, Boolean iPar){
		at = iat;
		name = iname;
		ParValue.add(iPar);
		hasPar = true;
	}
	public insComponent(AtomType iat, String iname, List<Object> iPar){
		at = iat;
		name = iname;
		for(Object ob: iPar){
			ParValue.add(ob);
		}
		hasPar = true;
	}
}
