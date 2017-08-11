
import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction;
import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ArrayNavigationExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.FunctionCallExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.StringLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.VariableReference;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.BehaviorsFactory;
import ujf.verimag.bip.Core.Behaviors.DataParameter;
import ujf.verimag.bip.Core.Behaviors.DefinitionBinding;
import ujf.verimag.bip.Core.Behaviors.Expression;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Behaviors.Port;
import ujf.verimag.bip.Core.Behaviors.PortDefinition;
import ujf.verimag.bip.Core.Behaviors.PortDefinitionReference;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.State;
import ujf.verimag.bip.Core.Behaviors.Transition;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.Interaction;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.PortParameter;
import ujf.verimag.bip.Core.Interactions.PortParameterReference;
import ujf.verimag.bip.Core.Modules.Module;
import ujf.verimag.bip.Core.Modules.ModulesFactory;
import ujf.verimag.bip.Core.Modules.OpaqueElement;
import ujf.verimag.bip.Core.Modules.Root;
import ujf.verimag.bip.Core.PortExpressions.ACFusion;
import ujf.verimag.bip.Core.PortExpressions.ACTyping;
import ujf.verimag.bip.Core.PortExpressions.ACTypingKind;
import ujf.verimag.bip.Core.PortExpressions.PortExpressionsFactory;
import ujf.verimag.bip.Core.PortExpressions.PortReference;
import ujf.verimag.bip.bip2src.Reverse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;


public class BIPTransform {
	protected static BehaviorsFactory behavFactory = BehaviorsFactory.eINSTANCE ;
    protected static InteractionsFactory interFactory = InteractionsFactory.eINSTANCE ;
    protected static ActionsFactory actionFactory = ActionsFactory.eINSTANCE; 
    protected static ExpressionsFactory expressionFactory = ExpressionsFactory.eINSTANCE ; 
    protected static PortExpressionsFactory portexpressionFactory = PortExpressionsFactory.eINSTANCE ;
    protected static ModulesFactory moduleFactory = ModulesFactory.eINSTANCE ; 


	public static State createState(String name) {
		 State s  = BehaviorsFactory.eINSTANCE.createState();
		 s.setName(name);
		 return s; 
	}
	
	public static Module createModule(String name){
		Module m = moduleFactory.createPackage();
		m.setName(name);
		return m;			
	}
	
	public static Module createSystem (String name){
		Module m = moduleFactory.createSystem();
		m.setName(name);
		return m;
	}
	
	public static Variable createVariable(String name, String type)
	{
		OpaqueElement oe = moduleFactory.createOpaqueElement();
		oe.setBody(type);
		Variable v = behavFactory.createVariable();
		v.setName(name); 
		v.setType(oe); 
		return v;	
	}
	
	public static PortDefinition exportPort(AtomType a, Port p, Variable v)
	{
		a.getPort().add(p);
		PortDefinition pd = behavFactory.createPortDefinition();
		pd.setName(p.getName());
		pd.setType(p.getType());
		a.getPortDefinition().add(pd);
		pd.getExposedVariable().add(v);
		DefinitionBinding db2 = behavFactory.createDefinitionBinding();
		db2.setDefinition(pd);
		p.setBinding(db2);
		return pd;
	}
	
	public static void createAtomPort (AtomType at, String portName,PortType pt, Boolean exported, List<Variable> v)
	{
		
		PortDefinition pd = behavFactory.createPortDefinition();
		pd.setName(portName);
		pd.setType(pt);
		at.getPortDefinition().add(pd);
		DefinitionBinding db = behavFactory.createDefinitionBinding();
		db.setDefinition(pd);
		
		if (exported == true){
			for(Variable var: v)
				pd.getExposedVariable().add(var);
			Port p = behavFactory.createPort();
			p.setName(portName);
			p.setType(pt);
			at.getPort().add(p);	
			p.setBinding(db);
		}
		PortDefinitionReference pdr = behavFactory.createPortDefinitionReference();
		pdr.setTarget(pd);
	
	}
	

	public static PortType createParameterisedPortType (String portTypeName, List<String> parType, List<String> parName)
	{
		PortType p1 = behavFactory.createPortType();
		p1.setName(portTypeName);
		DataParameter dp; 
		for(int i = 0; i < parType.size(); i++){
			dp = behavFactory.createDataParameter();
			dp.setName(parName.get(i));
			OpaqueElement oe = moduleFactory.createOpaqueElement();
			oe.setBody(parType.get(i));
			dp.setType(oe);
			p1.getDataParameter().add(dp);	
			
		}
		return p1;
	}
	
	public static PortType createPortType (String portTypeName)
	{
		PortType p = behavFactory.createPortType();
		p.setName(portTypeName);
		//p.getDataParameter().add(behavFactory.createDataParameter());
		return p;

	}
	
	public static PortType createPortType (String portTypeName, List<String> parType, List<String> parName)
	{
		PortType p1 = behavFactory.createPortType();
		p1.setName(portTypeName);
		DataParameter dp; 
		for(int i = 0; i < parType.size(); i++){
			dp = behavFactory.createDataParameter();
			dp.setName(parName.get(i));
			OpaqueElement oe = moduleFactory.createOpaqueElement();
			oe.setBody(parType.get(i));
			dp.setType(oe);
			p1.getDataParameter().add(dp);	
			
		}
		return p1;
	}
	
	public static AtomType createAtomType (String atomTypeName)
	{
		AtomType a = behavFactory.createAtomType();
		a.setName(atomTypeName);
		return a;
	}
	
	public static DataParameter addAtomDataParameter (AtomType at, String parameterType, String parameterName)
	{
		OpaqueElement oe = moduleFactory.createOpaqueElement();
		oe.setBody(parameterType);
		DataParameter sdp = behavFactory.createDataParameter();
		sdp.setName(parameterName);
		sdp.setType(oe);
		at.getDataParameter().add(sdp);
		return sdp;
		
		
	}
	
	public static void addCompoundDataParameter (CompoundType ct, String parameterType, String parameterName)
	{
		OpaqueElement oe = moduleFactory.createOpaqueElement();
		oe.setBody(parameterType);
		DataParameter sdp = behavFactory.createDataParameter();
		sdp.setName(parameterName);
		sdp.setType(oe);
		ct.getDataParameter().add(sdp);
		
		
	}
	
	public static void addAtomDataVariable(AtomType at, List<String> varType, List<String> varName){
		Variable var;
		for(int i = 0; i < varType.size(); i++){
			var = BIPTransform.createVariable(varName.get(i), varType.get(i));
			at.getVariable().add(var);
		}
	}
	
	public static PetriNet createPetriNet (AtomType at) {
		PetriNet pn = behavFactory.createPetriNet();
		pn.setAtomType(at);
		at.setBehavior(pn);
		return pn;	
	}
	

	public static StringLiteral createStringGuard (Transition t, String guard)
	{
		StringLiteral sl = expressionFactory.createStringLiteral();
		sl.setSValue(guard);
		t.setGuard(sl);
		return sl;
	}
	
	public static Transition createTransition (State origin, State destination, PortDefinition trigger){
		Transition t = behavFactory.createTransition();
		t.getOrigin().add(origin);
		t.getDestination().add(destination);
		PortDefinitionReference pdr = behavFactory.createPortDefinitionReference();
		pdr.setTarget(trigger);
		t.setTrigger(pdr);	
		
		return t;
	}
	
	public static ConnectorType createConnectorType (String connectorName, PortType pt, int NumberOfPorts){
		
		ConnectorType ct = interFactory.createConnectorType();
		for (int i = 0; i<NumberOfPorts; i=i+1){
		PortParameter pp = interFactory.createPortParameter();
		pp.setType(pt);
		pp.setName("p"+""+ (i+1));
		ct.getPortParameter().add(pp);
		}
		ct.setName(connectorName);
		return ct;
	}
	
	
	//triggers should be passed as a list of integers determining the indices of port references 
	public static void setConnectorTypeDefinition (ConnectorType ct, List<Integer> triggers)
	{
		
		List <PortParameterReference> RefList = new ArrayList <PortParameterReference>();
		for (int i=0;i<ct.getPortParameter().size();i=i+1){
			RefList.add(interFactory.createPortParameterReference());
			RefList.get(i).setTarget(ct.getPortParameter().get(i));
		}
		
		ACFusion portdef = portexpressionFactory.createACFusion();		
	
		for (int pp : triggers)
		{
			ACTyping prottrigfrom = portexpressionFactory.createACTyping();
			prottrigfrom.setOperand(RefList.get(pp));
			prottrigfrom.setType(ACTypingKind.TRIG);
			
			portdef.getOperand().add(prottrigfrom);
		}
		for (int i=0;i<RefList.size();i++)
		{
			if (triggers.contains(i)==false){
				portdef.getOperand().add(RefList.get(i));
			}					
		}
		
		ct.setDefinition(portdef);
		
		
			
	}
	
	public static Connector createConnector(ConnectorType type, String name)
	{
		Connector cc = interFactory.createConnector();
		cc.setName(name);
		cc.setType(type);
		
		return cc;
	}
	
	public static AssignmentAction createAssignmentAction(Object target, Object value){
		AssignmentAction aa = actionFactory.createAssignmentAction();	
		
		
		if (target instanceof Variable)
		{
			
			VariableReference vr = expressionFactory.createVariableReference();
			vr.setTargetVariable((Variable)target);
			aa.setAssignedTarget(vr);	
		}
		
		if (value instanceof Variable)
		{
			VariableReference vr = expressionFactory.createVariableReference();
			vr.setTargetVariable((Variable)value);
			aa.setAssignedValue(vr);	
		}
		
		if (target instanceof DataParameter)
		{
			DataParameterReference dpr = expressionFactory.createDataParameterReference();
			dpr.setTargetParameter((DataParameter)target);
			aa.setAssignedTarget(dpr);
			
			
		}
		if (value instanceof DataParameter)
		{
			DataParameterReference dpr = expressionFactory.createDataParameterReference();
			dpr.setTargetParameter((DataParameter)value);
			aa.setAssignedValue(dpr);	
		}
		if(value instanceof StringLiteral)
		{
			aa.setAssignedValue((StringLiteral)value);
		}
		
		if(value instanceof IntegerLiteral)
		{
			aa.setAssignedValue((IntegerLiteral)value);
		}
		
		if(value instanceof BooleanLiteral){
			aa.setAssignedValue((BooleanLiteral)value);
		}
		if(value instanceof RequiredDataParameterReference){
			aa.setAssignedValue((RequiredDataParameterReference)value);
		}
		if(target instanceof RequiredDataParameterReference){
			aa.setAssignedTarget((RequiredDataParameterReference)target);
		}
		return aa;
	}
	
	public static CompositeAction createCompositeAction(List<Object> targets, List<Object> values){
		CompositeAction ca = actionFactory.createCompositeAction();	
		for (int i=0; i<targets.size(); i++){
			AssignmentAction aa = BIPTransform.createAssignmentAction(targets.get(i), values.get(i));
			ca.getContent().add(aa);
		}
		return ca;
	}
	
	//assumes ports have only one parameter
	//targets and values are port parameters that are passed in the form of a list of their indices
	public static CompositeAction createConnectorAssignmentAction(ConnectorType ct, List<Integer> targets, List<Integer> values)
	{
		CompositeAction ca = actionFactory.createCompositeAction();	
		for (int i=0; i<targets.size(); i++)
		{
			AssignmentAction aa = actionFactory.createAssignmentAction();	
			aa.setAssignedTarget(((DataReference) EcoreUtil.copy(BIPTransform.getPortDataParameterReference(ct.getPortParameter().get(targets.get(i)), ct.getPortParameter().get(targets.get(i)).getType().getDataParameter().get(0)))));
			aa.setAssignedValue(((Expression) EcoreUtil.copy(BIPTransform.getPortDataParameterReference(ct.getPortParameter().get(values.get(i)), ct.getPortParameter().get(targets.get(i)).getType().getDataParameter().get(0)))));
			ca.getContent().add(aa);
		}
	
		return ca;
	}
	
	
	public static RequiredDataParameterReference getPortDataParameterReference (PortParameter s, DataParameter DP)
	{
		RequiredDataParameterReference rdpr = expressionFactory.createRequiredDataParameterReference();
		PortParameterReference ppreffrom =  interFactory.createPortParameterReference();
		ppreffrom.setTarget(s);
		rdpr.setTargetParameter(DP);
		rdpr.setPortReference(ppreffrom);
		
		return rdpr;	
	}
		

	
	public static BinaryExpression createBinaryExpression(Expression left, BinaryOperator operator, Expression right )
	{
		BinaryExpression be = expressionFactory.createBinaryExpression();
		
		be.setOperator(operator);
		be.setLeftOperand(left);
		be.setRightOperand(right);	
		return be;
		
	}
	
	public static  UnaryExpression createUnaryExpression(UnaryOperator operator, Expression operand){
		UnaryExpression ue = expressionFactory.createUnaryExpression();
		ue.setOperator(operator);
		ue.setOperand(operand);
		return ue;
	}
	public static FunctionCallExpression createFunctionCallExpression (String functionName)
	{
		FunctionCallExpression fc = expressionFactory.createFunctionCallExpression();
		fc.setFunctionName(functionName);	
		return fc;
		
		
	}
	
	public static IntegerLiteral createIntegerLiteral(Integer value) 
	{
		IntegerLiteral IL = expressionFactory.createIntegerLiteral() ; 
		IL.setIValue(value) ; 
		return IL ; 
	}
	
	public static InteractionSpecification createInteractionSpecification (ConnectorType ct, List<Integer> pps)
	{
		InteractionSpecification is1 = interFactory.createInteractionSpecification();
		Interaction i1 = interFactory.createInteraction();
		for (int i=0; i< ct.getPortParameter().size();i++){
			if (pps.contains(i)){
				PortParameterReference ppref = interFactory.createPortParameterReference();
				ppref.setTarget(ct.getPortParameter().get(i));
				i1.getPort().add((PortReference) EcoreUtil.copy(ppref));
			}	
			
		}
		is1.setInteraction(i1);
		return is1;
	}
	
	public static Component createComponent (AtomType at, String name)
	{
		Component c = interFactory.createComponent();
		c.setType(at);
		c.setName(name);
		return c;
	}
	
	public static VariableReference getExpression(Variable var){
		VariableReference vr = expressionFactory.createVariableReference();
		vr.setTargetVariable(var);
		return vr;
		
	}
	public static DataParameterReference getExpression(DataParameter dp){
		DataParameterReference vr = expressionFactory.createDataParameterReference();
		vr.setTargetParameter(dp);
		return vr;
		
	}
	
	public static RequiredDataParameterReference getExpression(PortParameter pp, int i){
		RequiredDataParameterReference  rdpr = expressionFactory.createRequiredDataParameterReference();
		PortParameterReference ppr = interFactory.createPortParameterReference();
		ppr.setTarget(pp);
		rdpr.setPortReference(ppr);
		rdpr.setTargetParameter(pp.getType().getDataParameter().get(i));
		return rdpr;
	}
	
	
	
	static Action createAction(Action act, Map<String, Object> parOrVar) {
		if(act instanceof AssignmentAction)
		{
			AssignmentAction rassignAct = ActionsFactory.eINSTANCE.createAssignmentAction();
			AssignmentAction assignAct = (AssignmentAction) act;
			DataReference dataRef = assignAct.getAssignedTarget();
			if(dataRef instanceof VariableReference)
			{
				VariableReference varRef = (VariableReference) dataRef;
				rassignAct.setAssignedTarget((VariableReference)getExpression(varRef, parOrVar));
			}
			else if(dataRef instanceof RequiredDataParameterReference){
				RequiredDataParameterReference rdpr = (RequiredDataParameterReference) dataRef;
				rassignAct.setAssignedTarget((RequiredDataParameterReference)getExpression(rdpr, parOrVar));
			}
			else if(dataRef instanceof ArrayNavigationExpression)
			{
				ArrayNavigationExpression arrayNavExp = (ArrayNavigationExpression) dataRef;
				rassignAct.setAssignedTarget((ArrayNavigationExpression)getExpression(arrayNavExp, parOrVar));
			}
			else
			{
				System.out.println("ERROR: " + dataRef.toString());
			}
			
			
			Expression exp = assignAct.getAssignedValue();
			rassignAct.setAssignedValue(getExpression(exp, parOrVar));
			return rassignAct;
		}
		else if(act instanceof FunctionCallExpression)
		{
			FunctionCallExpression funCallExp = (FunctionCallExpression) act;
			return getExpression(funCallExp, parOrVar);
		}
		else
		{
			//System.out.print("ERROR: " + act.toString());
			return null;
		}
	}
	public static Expression getExpression(Expression exp, Map<String, Object> parOrVar) {
		Expression rexp = null;
		if(exp == null) return rexp;
		else if(exp instanceof BinaryExpression)
		{
			BinaryExpression binaryExp = (BinaryExpression) exp;
			BinaryExpression rbinaryExp = ExpressionsFactory.eINSTANCE.createBinaryExpression();
			Expression leftExp = binaryExp.getLeftOperand();
			Expression rightExp = binaryExp.getRightOperand();
			BinaryOperator operator = binaryExp.getOperator();
			rbinaryExp.setLeftOperand(getExpression(leftExp, parOrVar));
			rbinaryExp.setRightOperand(getExpression(rightExp, parOrVar));
			rbinaryExp.setOperator(operator);
			rexp = rbinaryExp;
		}
		else if(exp instanceof UnaryExpression)
		{
			UnaryExpression unaryExp = (UnaryExpression) exp;
			UnaryExpression runaryExp = ExpressionsFactory.eINSTANCE.createUnaryExpression();
			Expression operand = unaryExp.getOperand();
			UnaryOperator operator = unaryExp.getOperator();
			runaryExp.setOperator(operator);
			runaryExp.setOperand(getExpression(operand, parOrVar));
			rexp = runaryExp;
		}
		else if(exp instanceof VariableReference)
		{
			VariableReference varRef = (VariableReference) exp;
			VariableReference rvarRef = ExpressionsFactory.eINSTANCE.createVariableReference();
			Variable var = varRef.getTargetVariable();
			rvarRef.setTargetVariable((Variable)parOrVar.get(var.getName()));
			rexp = rvarRef;
		}
		else if(exp instanceof DataParameterReference)
		{
			DataParameterReference dataParamRef = (DataParameterReference) exp;
			DataParameterReference rdataParamRef = ExpressionsFactory.eINSTANCE.createDataParameterReference();
			DataParameter dataParam = dataParamRef.getTargetParameter();
			rdataParamRef.setTargetParameter((DataParameter)parOrVar.get(dataParam.getName()));
			rexp = rdataParamRef;
		}
		else if(exp instanceof IntegerLiteral)
		{
			IntegerLiteral intLit = (IntegerLiteral) exp;
			IntegerLiteral rintLit = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
			rintLit.setIValue(intLit.getIValue());
			rexp = rintLit;
		}
		else if(exp instanceof BooleanLiteral)
		{
			
			BooleanLiteral boolLit = (BooleanLiteral) exp;
			BooleanLiteral rboolLit = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
			rboolLit.setBValue(boolLit.isBValue());
			rexp = rboolLit;
		}
		else if(exp instanceof ArrayNavigationExpression)
		{
			ArrayNavigationExpression arrayNavExp = (ArrayNavigationExpression) exp;
			ArrayNavigationExpression rarrayNavExp = ExpressionsFactory.eINSTANCE.createArrayNavigationExpression();
			Expression index = arrayNavExp.getIndex();
			VariableReference navigated = (VariableReference) arrayNavExp.getNavigated();
			//Variable arrayName = navigated.getTargetVariable();
			rarrayNavExp.setNavigated((VariableReference)getExpression(navigated, parOrVar));
			rarrayNavExp.setIndex(getExpression(index, parOrVar));
			rexp = rarrayNavExp;
		}
		else if(exp instanceof FunctionCallExpression)
		{
			FunctionCallExpression funCallExp = (FunctionCallExpression) exp;
			FunctionCallExpression rfunCallExp = ExpressionsFactory.eINSTANCE.createFunctionCallExpression();
			rfunCallExp.setFunctionName(funCallExp.getFunctionName());
			
			int numOfParams = funCallExp.getActualData().size();
			for(int i = 0; i < numOfParams; i++)
			{
				Expression param = funCallExp.getActualData().get(i);
				rfunCallExp.getActualData().add((getExpression(param, parOrVar)));
			}
		}
		else if(exp instanceof RequiredDataParameterReference){
			RequiredDataParameterReference  rdpr = (RequiredDataParameterReference)exp;
			RequiredDataParameterReference  rrdpr = ExpressionsFactory.eINSTANCE.createRequiredDataParameterReference();
			String name = rdpr.getPortReference().getTarget().getName()+"."+rdpr.getTargetParameter().getName();
			RequiredDataParameterReference t = (RequiredDataParameterReference) parOrVar.get(name);
			
			PortParameterReference ppr = InteractionsFactory.eINSTANCE.createPortParameterReference();
			ppr.setTarget(t.getPortReference().getTarget());
			rrdpr.setPortReference(ppr);
			rrdpr.setTargetParameter(t.getTargetParameter());
			rexp =  rrdpr;
		}
		return rexp;
	}

	public static CompoundType createCompound (Module m, String compoundTypeName, String compoundName)
	{
	 CompoundType c = InteractionsFactory.eINSTANCE.createCompoundType();
	 c.setName(compoundTypeName);
	 m.getBipType().add(c); 
	 Root r = ModulesFactory.eINSTANCE.createRoot();
	 r.setName(compoundName);
	 r.setType(c);
	 ((ujf.verimag.bip.Core.Modules.System)m).setRoot(r);
	 return c;
	}

	public static void createBIPFile(String fileName, Module bipSystem) throws FileNotFoundException {
	        FileOutputStream out = new FileOutputStream(fileName);
	        PrintStream x = new PrintStream(out);
	        Reverse a= new Reverse(x);
	        a.decompile(bipSystem);
	}
}
	

	
	

