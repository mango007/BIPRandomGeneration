
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ujf.verimag.bip.Core.Interactions.Connector;

//import org.bip.behaviour.BehaviourImpl;

//import connectorsTransformers.Connector;



import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.Behaviors.Port;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.PartElementReference;
import ujf.verimag.bip.Core.Modules.Module;

public class BIPCompoundBuilder {
	
	private class pair{
		Component c;
		Port p;
		pair(Component ic, Port ip){
			c = ic;
			p = ip;
		}
	}
	private Module module; 
	//private List<Component> bipComponents; 
	private CompoundType ct;
	private List<insComponent> components;
	private Map<String, pair> portRefMap = new HashMap<String, pair>();
	private List<insConnector> cl;

	
	public BIPCompoundBuilder(Module m, List<insComponent> components, List<insConnector> cl, String compoundType, String compoundName) {
		this.ct = BIPTransform.createCompound(m, compoundType, compoundName);
		//BIPTransform.addCompoundDataParameter(this.ct, "", "");
		this.module = m; 
		
		this.components = components;
		this.cl=cl;
		buildComponents();
		Convert(this.cl);
		
		m.getBipType().add(ct);
	}
	
	private void buildComponents()
	{

		for(insComponent ic: components) {
			//BIPAtomBuilder atomBuilder = new BIPAtomBuilder(behav, module);
 
				Component c = BIPTransform.createComponent(ic.at, ic.name);
				
				for(Object ob: ic.ParValue){
					if(ob instanceof Integer){
						IntegerLiteral il1 = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
						il1.setIValue((Integer)ob);
						c.getActualData().add(il1);
					}
					else if(ob instanceof Boolean){
						BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
						bl.setBValue((Boolean)ob);
						c.getActualData().add(bl);
					}
				}
				
				
				buildPortMap(c);
				
				ct.getSubcomponent().add(c);
				
				
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
		for (Port p: c.getType().getPort())
		{
			 portRefMap.put(c.getName()+"."+p.getName(), new pair(c, p));
			 
		}
	}
	
	private InnerPortReference getInnerPortReference(pair pa){
		InnerPortReference ipr = InteractionsFactory.eINSTANCE.createInnerPortReference(); 
		 PartElementReference PE = InteractionsFactory.eINSTANCE.createPartElementReference();
		 PE.setTargetPart(pa.c);
		 ipr.setTargetPort(pa.p);
		 ipr.setTargetInstance(PE);
		 return ipr;
	}
	private void Convert(List<insConnector> cl) {
		 for (insConnector ic: cl){
			Connector c = BIPTransform.createConnector(ic.cont, ic.name);
			for (String t : ic.ports){
				
				c.getActualPort().add(getInnerPortReference(portRefMap.get(t)));
			}
			this.ct.getConnector().add(c);

		}

	}
}
