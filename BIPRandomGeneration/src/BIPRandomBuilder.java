import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import BIPTransformation.TransformationFunction;
import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.Behaviors.*;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.Interaction;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.PortParameter;
import ujf.verimag.bip.Core.Interactions.PortParameterReference;
import ujf.verimag.bip.Core.Modules.Module;
import ujf.verimag.bip.Core.Modules.OpaqueElement;
import ujf.verimag.bip.Core.PortExpressions.ACExpression;
import ujf.verimag.bip.Core.PortExpressions.ACFusion;
import ujf.verimag.bip.Core.PortExpressions.ACTyping;
import ujf.verimag.bip.Core.PortExpressions.PortExpression;
import ujf.verimag.bip.Core.PortExpressions.PortReference;


public class BIPRandomBuilder {
	static Map<String, PortType> ptl = new HashMap<String, PortType>();
	static Map<String, AtomType> atl = new HashMap<String, AtomType>();
	static Map<String, ConnectorType> contl= new HashMap<String, ConnectorType>();
	static Map<String, Integer> groupl = new HashMap<String, Integer>();
	static Map<String, PortType> optl = new HashMap<String, PortType>();
	static Map<String, AtomType> oatl = new HashMap<String, AtomType>();
	static Map<String, ConnectorType> ocontl= new HashMap<String, ConnectorType>();
	static int portTypeCount = 0;
	
	static public PortType createRandomPortType(Module m, String ptName, String instuctionFile) throws Exception{
		List<String> ptParType = new ArrayList<String>();
		List<String> ptParName = new ArrayList<String>();
		
		RandomPortTypeDescription rPortDesp = new RandomPortTypeDescription(instuctionFile, ptName);
		

		if(! rPortDesp.isRandomType){
			PortType pt = optl.get(ptName);
			for(DataParameter par: pt.getDataParameter()){
				
				ptParType.add(((OpaqueElement)par.getType()).getBody());
				ptParName.add(par.getName());
			}
			
		}
		rPortDesp.createPortRandomPart(groupl, ptParType, ptParName);
		PortType portType = BIPTransform.createPortType(ptName, ptParType,ptParName);
		m.getBipType().add(portType);
		return portType;
	}
	
	static public AtomType createRandomAtom(Module m, String atName, String instuctionFile) throws Exception{
		
		AtomType rat = BIPTransform.createAtomType(atName);
		Map<String, Object> parOrVar = new HashMap<String, Object>();
		RandomAtomDescription rAtomDesp = new RandomAtomDescription(instuctionFile, atName);
		AtomType at = null;
		if(rAtomDesp.isRandomType && rAtomDesp.originalAtomType != null)
			at = oatl.get(rAtomDesp.originalAtomType);
		else if(!rAtomDesp.isRandomType)
			at = oatl.get(atName);
		
		if(at != null){
			for(PortDefinition pd: at.getPortDefinition()){
				optl.put(pd.getType().getName(), pd.getType());
			}
		}
		
		
		for(RandomPortDescription rpd:rAtomDesp.randomPortDes){
			if(rpd.type != null && !ptl.containsKey(rpd.type)){
				ptl.put(rpd.type, createRandomPortType(m, rpd.type, instuctionFile));
			}
		}
		List<Action> rinitialAction = new ArrayList<Action>();
		List<insPort> pl = new ArrayList<insPort>();
		List<String> sl = new ArrayList<String>();
		String initialState = null;
		if(rAtomDesp.initState != null)
			initialState = rAtomDesp.initState;
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		if(at != null){
			//add variables to atomic type
			for(int i = 0; i < at.getVariable().size(); i++){
				if(rAtomDesp != null && !rAtomDesp.randomVar.isEmpty() && rAtomDesp.randomVar.contains(i)){
					continue;
				}
				Variable var = at.getVariable().get(i);
				Variable rvar = BIPTransform.createVariable(var.getName(), ((OpaqueElement)var.getType()).getBody());
				rat.getVariable().add(rvar);
				parOrVar.put(rvar.getName(), rvar);
			}
			
			//add parameters to atomic type
			for(int i = 0; i < at.getDataParameter().size(); i++){
				if(rAtomDesp != null && !rAtomDesp.randomPar.isEmpty() && rAtomDesp.randomPar.contains(i))
					continue;
				DataParameter par = at.getDataParameter().get(i);
				DataParameter rpar = BIPTransform.addAtomDataParameter(rat, ((OpaqueElement)par.getType()).getBody(), par.getName());
				parOrVar.put(rpar.getName(), rpar);
				
			}
			
			//add ports to atomic type
			
			int numOfPorts = at.getPortDefinition().size();
			for(int i = 0; i < numOfPorts; i++)
			{

				PortDefinition port = at.getPortDefinition().get(i);
				String portTypeName = port.getType().getName();
				
				//add portType to the module
				if(!ptl.containsKey(portTypeName)){
					ptl.put(portTypeName, createRandomPortType(m, portTypeName, instuctionFile));
				}
				if(rAtomDesp != null && !rAtomDesp.randomPort.isEmpty() && rAtomDesp.randomPort.contains(i))
					continue;			
				int isExport = 1;
				if(port.getExposedVariable() == null){
					isExport = 0;
				}
				List<Variable> exposedVar = new ArrayList<Variable>();
				for(Variable var: port.getExposedVariable()){
					exposedVar.add((Variable)parOrVar.get(var.getName()));
				}				
				pl.add(new insPort(isExport, port.getName(), ptl.get(portTypeName), exposedVar));
			}
			
			PetriNet pn = (PetriNet)at.getBehavior();
			
			for(int i = 0; i < pn.getState().size(); i++){
				if(rAtomDesp != null && !rAtomDesp.randomState.isEmpty() && rAtomDesp.randomState.contains(i))
					continue;
				State state = pn.getState().get(i);
				sl.add(state.getName());
			}
				
			
			//set initial state
			initialState = pn.getInitialState().get(0).getName();
			
			
			//set initial action
			Action initialAction = pn.getInitialization();
			
			if(rAtomDesp != null && !rAtomDesp.isRandomInitAct){
				if(initialAction != null && initialAction instanceof CompositeAction){
					rinitialAction.add(ActionsFactory.eINSTANCE.createCompositeAction());
					CompositeAction ca = (CompositeAction) initialAction;
					int numOfActions = ca.getContent().size();
					for(int j = 0; j < numOfActions; j++)
					{
						Action act = ca.getContent().get(j);
						((CompositeAction)rinitialAction.get(0)).getContent().add(BIPTransform.createAction(act, parOrVar));
					}		
				}
			}
			else if(rAtomDesp != null && rAtomDesp.isRandomInitAct){
				rinitialAction.add(ActionsFactory.eINSTANCE.createCompositeAction());
			}
			
			
			//add transitions to atomic type
			int numOfTransitions = pn.getTransition().size();
			
			for(int i = 0; i < numOfTransitions; i++){
				if(rAtomDesp != null && !rAtomDesp.randomTransition.isEmpty() && rAtomDesp.randomTransition.contains(i))
					continue;
				Transition t = pn.getTransition().get(i);
				Action rAction = null;
				if(t.getAction() != null && t.getAction() instanceof CompositeAction){
					rAction = ActionsFactory.eINSTANCE.createCompositeAction();
					CompositeAction ca = (CompositeAction) t.getAction();
					int numOfActions = ca.getContent().size();
					for(int j = 0; j < numOfActions; j++)
					{
						Action act = ca.getContent().get(j);
						((CompositeAction)rAction).getContent().add(BIPTransform.createAction(act, parOrVar));
					}	
				}
				else if(t.getAction() != null){
					rAction = BIPTransform.createAction(t.getAction(), parOrVar);
				}
				PortDefinitionReference portDefRef = (PortDefinitionReference)t.getTrigger();
				tl.add(new ExecutorTransition(portDefRef.getTarget().getName(), t.getOrigin().get(0).getName(), t.getDestination().get(0).getName(), BIPTransform.getExpression(t.getGuard(),parOrVar), rAction));
			}
		}

		
		rAtomDesp.createAtomRandomPart(at, rat, ptl, groupl, parOrVar, pl, sl, rinitialAction, tl);
		BehaviourImpl behav;
		if(rinitialAction.size() > 0)
			behav = new BehaviourImpl(rat,  pl, sl, tl, initialState, rinitialAction.get(0));
		else
			behav = new BehaviourImpl(rat,  pl, sl, tl, initialState, null);
		new BIPAtomBuilder(behav, m);

		
		return rat;
		
		
		
	}
	
	static public ConnectorType createRandomConnector(Module m, String typeName, String instuctionFile) throws IOException{
		ConnectorType rcont = InteractionsFactory.eINSTANCE.createConnectorType();
		rcont.setName(typeName);
		
		RandomConnectorTypeDescription rConDesp = new RandomConnectorTypeDescription(instuctionFile, typeName);
		if(rConDesp.isRandomType){
			rConDesp.createConnectorRandomPart(rcont, ptl, groupl);
		}
		else{
			ConnectorType cont = ocontl.get(typeName);
			Map <String, PortParameter> RefList = new HashMap <String, PortParameter>();
			Map<String, Object> parOrVar = new HashMap<String, Object>();
			for(PortParameter pp: cont.getPortParameter()){
				PortParameter rpp = InteractionsFactory.eINSTANCE.createPortParameter();
				rpp.setType(ptl.get(pp.getType().getName()));
				rpp.setName(pp.getName());
				rcont.getPortParameter().add(rpp);
				RefList.put(pp.getName(), rpp);
				
				for(int i = 0; i < pp.getType().getDataParameter().size(); i++){
					DataParameter dp = pp.getType().getDataParameter().get(i);
					RequiredDataParameterReference  rdpr = ExpressionsFactory.eINSTANCE.createRequiredDataParameterReference();
					PortParameterReference ppr = InteractionsFactory.eINSTANCE.createPortParameterReference();
					ppr.setTarget(rpp);
					rdpr.setPortReference(ppr);
					rdpr.setTargetParameter(ptl.get(pp.getType().getName()).getDataParameter().get(i));
					parOrVar.put(pp.getName()+"."+dp.getName(), rdpr);
				}
			}
			
			//set ConnectorType definition
			PortExpression portdef = cont.getDefinition();
			List<Integer> trigger = new ArrayList<Integer>();
			if(portdef instanceof ACFusion){
				ACFusion acf = (ACFusion)portdef;
				for(int i = 0; i < acf.getOperand().size(); i++){
					ACExpression ace = acf.getOperand().get(i);
					if(ace instanceof ACTyping){
						trigger.add(i);
					}
				}
			}
			else if(portdef instanceof ACTyping){
				trigger.add(0);
			}
			BIPTransform.setConnectorTypeDefinition(rcont, trigger);
			
			
			for(InteractionSpecification is: cont.getInteractionSpecification()){
				InteractionSpecification ris = InteractionsFactory.eINSTANCE.createInteractionSpecification();
				Interaction ri = InteractionsFactory.eINSTANCE.createInteraction();
				for(PortReference pr: is.getInteraction().getPort()){
					PortParameterReference ppr = InteractionsFactory.eINSTANCE.createPortParameterReference();
					ppr.setTarget(RefList.get(((PortParameterReference) pr).getTarget().getName()));
					ri.getPort().add(ppr);
					
				}
				ris.setInteraction(ri);
				ris.setGuard(BIPTransform.getExpression(is.getGuard(), parOrVar));
				if(is.getDownAction() != null && is.getDownAction() instanceof CompositeAction){
					Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
					CompositeAction ca = (CompositeAction) is.getDownAction();
					int numOfActions = ca.getContent().size();
					for(int j = 0; j < numOfActions; j++)
					{
						Action act = ca.getContent().get(j);
						((CompositeAction)rAction).getContent().add(BIPTransform.createAction(act, parOrVar));
					}	
					ris.setDownAction(rAction);
				}
				else if(is.getDownAction() != null){
					Action rAction = BIPTransform.createAction(is.getDownAction(), parOrVar);
					ris.setDownAction(rAction);
				}
				if(is.getUpAction() != null && is.getUpAction() instanceof CompositeAction){
					Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
					CompositeAction ca = (CompositeAction) is.getUpAction();
					int numOfActions = ca.getContent().size();
					for(int j = 0; j < numOfActions; j++)
					{
						Action act = ca.getContent().get(j);
						((CompositeAction)rAction).getContent().add(BIPTransform.createAction(act, parOrVar));
					}	
					ris.setUpAction(rAction);
				}
				else if(is.getUpAction() != null){
					Action rAction = BIPTransform.createAction(is.getUpAction(), parOrVar);
					ris.setUpAction(rAction);
				}
				
				
				rcont.getInteractionSpecification().add(ris);
			}
			
		}
		m.getBipType().add(rcont);
		return rcont;
		
	}
	static public void createRandomCompound(String testFile, String instuctionFile, String output, String root) throws Exception{
		//rCompDesp.initial(testFile, instuctionFile);
		//rCompDesp.createRandomCompound();
		CompoundType ct;
		if(testFile == null)
			ct = null;
		else{
			ct = TransformationFunction.ParseBIPFile(testFile);
			for(Component c: ct.getSubcomponent()){
				oatl.put(c.getType().getName(), (AtomType)c.getType());
			}
			for(Connector c: ct.getConnector()){
				ocontl.put(c.getType().getName(), (ConnectorType)c.getType());
			}
		}
			
		Module m = BIPTransform.createSystem("test");
		List<insComponent> coml = new ArrayList<insComponent>();
		List<insConnector> cntl = new ArrayList<insConnector>();
		
		RandomCompoundDescription rComDesp = new RandomCompoundDescription(instuctionFile, root);
		
		for(RandomComponentDescription rcd: rComDesp.randomComponentDes){
			if(rcd.type != null && !atl.containsKey(rcd.type))
				atl.put(rcd.type, createRandomAtom(m, rcd.type, instuctionFile));
		}
		
		for(RandomConnectorDescription rcd: rComDesp.randomConnectorDes){
			if(rcd.type != null && !contl.containsKey(rcd.type))
				contl.put(rcd.type, createRandomConnector(m, rcd.type, instuctionFile));
		}
		if(ct != null){
			for(int i = 0; i < ct.getSubcomponent().size(); i++){

				if(rComDesp.randomComponent.contains(i))
					continue;
				Component c = ct.getSubcomponent().get(i);
				String atn = c.getType().getName();
				if(!atl.containsKey(atn)){
					atl.put(atn, createRandomAtom(m, atn, instuctionFile));
				}
				List<Object> para = new ArrayList<Object>();
				for(Expression exp: c.getActualData()){
					if(exp instanceof BooleanLiteral)
						para.add(((BooleanLiteral)exp).isBValue());
					else if(exp instanceof IntegerLiteral)
						para.add(((IntegerLiteral)exp).getIValue());
				}
				coml.add(new insComponent(atl.get(c.getType().getName()), c.getName(), para));
			}
			
			for(int i = 0; i < ct.getConnector().size(); i++){
				Connector c = ct.getConnector().get(i);
				String contn = c.getType().getName();
				
				if(!contl.containsKey(contn)){
					contl.put(contn, createRandomConnector(m, contn, instuctionFile));
				}
				if(rComDesp.randomConnector.contains(i))
					continue;
				List<String> para = new ArrayList<String>();
				for(int j = 0; j < c.getActualPort().size(); j++){
					InnerPortReference ipr = (InnerPortReference)c.getActualPort().get(j);
					StringBuffer sb = new StringBuffer();
					sb.append(ipr.getTargetInstance().getTargetPart().getName());
					sb.append(".");
					sb.append(ipr.getTargetPort().getName());
					para.add(sb.toString());
				}
				cntl.add(new insConnector(contl.get(c.getType().getName()), c.getName(), para));
				
			}
		}
		

		rComDesp.createCompoundRandomPart(ct, atl, contl, groupl, coml, cntl);
		
		new BIPCompoundBuilder(m, coml, cntl, "Root", "root");
		BIPTransform.createBIPFile(output, m);
		
	}
		
	
}
