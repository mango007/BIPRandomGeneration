import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
//import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Interactions.PortParameter;
import ujf.verimag.bip.Core.Modules.Module;


public class RandomConnector {
	static PortType ptFT;
	static PortType ptST;
	static BIPAtomBuilder Source;
	static BIPAtomBuilder Buffer;
	static BIPAtomBuilder Filter;
	static ConnectorType Singleton;
	static int MAX;
	static int STEPS;
	
	public static void main(String[] args) throws Exception{
		
		MAX = 4;
		STEPS = 4;
		Module m = BIPTransform.createSystem("TEST");
		
		ptFT = BIPTransform.createPortType("FirstType");
		
		ArrayList<String> ptSTParType = new ArrayList<String>();
		ptSTParType.add("int");
		ArrayList<String> ptSTParName = new ArrayList<String>();
		ptSTParName.add("x");
		ptST = BIPTransform.createParameterisedPortType("SecondType", ptSTParType,ptSTParName);
		
		m.getBipType().add(ptFT);
		m.getBipType().add(ptST);
		
		
		
		RandomConnector.createSource(m);
		RandomConnector.createBuffer(m);
		RandomConnector.createFilter(m);
		RandomConnector.createSingleton(m);
		RandomConnector.createRoot(m);
		
		BIPTransform.createBIPFile("/home/aaron/workspace/java/BIPRandomGeneration/example/out2.bip", m);
	}
	static void createSource(Module m){
		AtomType at = BIPTransform.createAtomType("Source");
		BIPTransform.addAtomDataParameter(at, "int", "seed");
		
		at.getVariable().add(BIPTransform.createVariable("x", "int"));
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(0));
		pl.add(new insPort(1, "Write", ptST, eVar1));
		
		pl.add(new insPort(1, "Loop", ptFT, new ArrayList<Variable>()));
		
		List<String> sl = new ArrayList<String>();
		sl.add("READY"); sl.add("DONE");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();t1.add(at.getVariable().get(0));
		List<Object> v1 = new ArrayList<Object>();v1.add(at.getDataParameter().get(0));

		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		
		tl.add(new ExecutorTransition("Write", "READY", "DONE"));
		
		tl.add(new ExecutorTransition("Loop", "DONE", "DONE"));

		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "READY", initialAction);
		Source = new BIPAtomBuilder(behav, m);
	}
	static void createBuffer(Module m){
		AtomType at = BIPTransform.createAtomType("Buffer");
		//BIPTransform.addAtomDataParameter(at, "int", "x");
		
		at.getVariable().add(BIPTransform.createVariable("x", "int"));
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(0));
		pl.add(new insPort(1, "Read", ptST, eVar1));
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(0));
		pl.add(new insPort(1, "Write", ptST, eVar2));
		
		List<String> sl = new ArrayList<String>();
		sl.add("EMPTY"); sl.add("FULL");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();
		List<Object> v1 = new ArrayList<Object>();
		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		
		tl.add(new ExecutorTransition("Read", "EMPTY", "FULL"));
		
		tl.add(new ExecutorTransition("Write", "FULL", "EMPTY"));

		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "EMPTY", initialAction);
		Buffer = new BIPAtomBuilder(behav, m);
	}
	static void createFilter(Module m){
		AtomType at = BIPTransform.createAtomType("Filter");
		//BIPTransform.addAtomDataParameter(at, "int", "x");
		
		at.getVariable().add(BIPTransform.createVariable("x", "int"));
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(0));
		pl.add(new insPort(1, "Read", ptST, eVar1));
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(0));
		pl.add(new insPort(1, "Write", ptST, eVar2));
		pl.add(new insPort(1, "Temp", ptFT, new ArrayList<Variable>()));
		
		List<String> sl = new ArrayList<String>();
		sl.add("EMPTY"); sl.add("FILTER");sl.add("FULL");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();
		List<Object> v1 = new ArrayList<Object>();
		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		
		tl.add(new ExecutorTransition("Read", "EMPTY", "FILTER"));
		
		IntegerLiteral il1 = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
		il1.setIValue(7);
		BinaryExpression g1 = BIPTransform.createBinaryExpression(BIPTransform.getExpression(at.getVariable().get(0)), BinaryOperator.LESS_THAN, il1);
		tl.add(new ExecutorTransition("Temp", "FILTER", "FULL", g1));
		
		IntegerLiteral il2 = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
		il2.setIValue(7);
		BinaryExpression g2 = BIPTransform.createBinaryExpression(BIPTransform.getExpression(at.getVariable().get(0)), BinaryOperator.GREATER_THAN_OR_EQUAL, il2);
		tl.add(new ExecutorTransition("Temp", "FILTER", "EMPTY", g2));
		
		tl.add(new ExecutorTransition("Write", "FULL", "EMPTY"));

		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "EMPTY", initialAction);
		Filter = new BIPAtomBuilder(behav, m);
	}
	
	//inNum/outNum is the number of in-degree/out-degree of a connector.
	//isIn indicates whether the connection is for receiving or sending data.
	//If isIn == true, outNum must be 1. If isIn = false, inNum must be 1.
	static ConnectorType createConnectorType(Module m, int inNum, int outNum, boolean isIn){
		ConnectorType ct;
		if(isIn){
			ct = BIPTransform.createConnectorType("in"+inNum+"out"+outNum+"in",ptST,inNum+outNum);
			//List<Integer> triger = new ArrayList<Integer>();
			//triger.add(inNum);
			List<Integer> triger = new ArrayList<Integer>();
			triger.add(inNum);
			BIPTransform.setConnectorTypeDefinition(ct,triger);
			//BIPTransform.setConnectorTypeDefinition(ct,new ArrayList<Integer>());			
			for(int i = 1; i <= inNum; i++){
				List<Integer> result = new ArrayList<Integer>();
				for(int j = 1; j <= i; j++){
					result.add(1);
				}
				RandomConnector.createInTransitions(ct, ct.getPortParameter(), inNum, 0, result, i, i, inNum);
			}
			
		}
		else{
			ct = BIPTransform.createConnectorType("in"+inNum+"out"+outNum+"out",ptST,inNum+outNum);
			List<Integer> triger = new ArrayList<Integer>();
			triger.add(0);
			BIPTransform.setConnectorTypeDefinition(ct,triger);
			for(int i = 1; i <= outNum; i++){
				List<Integer> result = new ArrayList<Integer>();
				for(int j = 1; j <= i; j++){
					result.add(1);
				}
				RandomConnector.createOutTransitions(ct, ct.getPortParameter(), 0, 1, result, i, i, outNum);
			}
			
		}
		m.getBipType().add(ct);
		return ct;
	}
	
	static void createInTransitions(ConnectorType ct, List<PortParameter> arr,int mustHave, int start, List<Integer> result, int count, int NUM, int arr_len){
		
		int i;
		for (i = start; i < arr_len + 1 - count; i++)
		{
			result.set(count - 1,i);
			if (count - 1 == 0)
			{
				List<Object> t1 = new ArrayList<Object>();
				List<Object> v1 = new ArrayList<Object>();
				t1.add(BIPTransform.getExpression(arr.get(mustHave), 0));
					
				v1.add(BIPTransform.getExpression(arr.get(result.get(0)), 0));
				
				Action a = BIPTransform.createCompositeAction(t1, v1);
				
				List<Integer> isTrigg = new ArrayList<Integer>();
				isTrigg.add(mustHave);
				for (int j = NUM - 1; j >= 0; j--){
					isTrigg.add(result.get(j));
				}
				InteractionSpecification is = BIPTransform.createInteractionSpecification(ct, isTrigg);
				ct.getInteractionSpecification().add(is);
				is.setDownAction(a);
			}
			else
				RandomConnector.createInTransitions(ct, arr, mustHave, i + 1, result, count - 1, NUM, arr_len);
		}
		
	}
	static void createOutTransitions(ConnectorType ct, List<PortParameter> arr,int mustHave, int start, List<Integer> result, int count, int NUM, int arr_len){
			//arr is the original array
			//start is the position to start traversal
			//result保存结果，为一维数组
			//count为result数组的索引值，起辅助作用
			//NUM为要选取的元素个数
			//arr_len为原始数组的长度，为定值
		for (int i = start; i < arr_len + 2 - count; i++)
		{
			result.set(count - 1,i);
			if (count - 1 == 0)
			{
				List<Object> t1 = new ArrayList<Object>();
				List<Object> v1 = new ArrayList<Object>();
				for (int j = NUM - 1; j >= 0; j--){
					v1.add(BIPTransform.getExpression(arr.get(mustHave), 0));
					
					t1.add(BIPTransform.getExpression(arr.get(result.get(j)), 0));
				}
				
				Action a = BIPTransform.createCompositeAction(t1, v1);
				
				List<Integer> isTrigg = new ArrayList<Integer>();
				isTrigg.add(mustHave);
				for (int j = NUM - 1; j >= 0; j--){
					isTrigg.add(result.get(j));
				}
				InteractionSpecification is = BIPTransform.createInteractionSpecification(ct, isTrigg);
				ct.getInteractionSpecification().add(is);
				is.setDownAction(a);
			}
			else
				RandomConnector.createOutTransitions(ct, arr, mustHave, i + 1, result, count - 1, NUM, arr_len);
		}
			
	}
		
	static void createSingleton(Module m){
		Singleton = BIPTransform.createConnectorType("Singleton",ptFT,1);
		
		BIPTransform.setConnectorTypeDefinition(Singleton, new ArrayList<Integer>());
		
		
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(Singleton, isTrigg);
		Singleton.getInteractionSpecification().add(is);
		Singleton.getInteractionSpecification().get(0).setDownAction(ActionsFactory.eINSTANCE.createCompositeAction());
		Singleton.getInteractionSpecification().get(0).setUpAction(ActionsFactory.eINSTANCE.createCompositeAction());
		m.getBipType().add(Singleton);
	}
	static void createRoot(Module m){
		Random randomGenerator = new Random();
		int bufNum = 0;
		int filNum = 0;
		int conNum = 0;
		Map<String, ConnectorType> ctl = new HashMap<String, ConnectorType>();
		List<insComponent> unconnectedBuffer = new ArrayList<insComponent>();
		
		List<insComponent> cmpl = new ArrayList<insComponent>();
		List<insConnector> cntl = new ArrayList<insConnector>();
		cmpl.add(new insComponent(Source.getAtomType(), "Source", randomGenerator.nextInt(10)+1));
	
		int outDegree = randomGenerator.nextInt(MAX)+1;
		ConnectorType ct = RandomConnector.createConnectorType(m, 1, outDegree, false);
		ctl.put(ct.getName(), ct);
		
		//List<String> s1;
		conNum++;
		List<String> s1 = new ArrayList<String>();
		s1.add("Source"+".Loop");
		cntl.add(new insConnector(Singleton, "C"+conNum, s1));
		
		conNum++;
		List<String> s2 = new ArrayList<String>();
		s2.add("Source"+".Write");
		for(int i = 0; i < outDegree; i++){
			bufNum++;
			insComponent t = new insComponent(Buffer.getAtomType(), "B"+bufNum);
			unconnectedBuffer.add(t);
			cmpl.add(t);
			s2.add(t.name+".Read");
		}
		cntl.add(new insConnector(ct, "C"+conNum, s2));
		
		for(int steps = 0; steps < STEPS; steps++){
			if(unconnectedBuffer.size() == 0){
				break;
			}
			filNum++;
			insComponent f = new insComponent(Filter.getAtomType(), "F"+filNum);
			cmpl.add(f);
			
			conNum++;
			s1 = new ArrayList<String>();
			s1.add(f.name+".Temp");
			cntl.add(new insConnector(Singleton, "C"+conNum, s1));
			
			List<String> sin = new ArrayList<String>();
			int j = randomGenerator.nextInt(unconnectedBuffer.size());//pick one unconnected buffer
			insComponent t = unconnectedBuffer.get(j);
			sin.add(t.name+".Write");
			unconnectedBuffer.remove(j);
			
			
			outDegree = randomGenerator.nextInt(MAX)+1;
			if(ctl.containsKey("in1out"+outDegree+"out")){
				ct = ctl.get("in1out"+outDegree+"out");
			}
			else{
				ct = RandomConnector.createConnectorType(m, 1, outDegree, false);
				ctl.put(ct.getName(), ct);
			}
			
			conNum++;
			s2 = new ArrayList<String>();
			s2.add(f.name+".Write");
			for(int i = 0; i < outDegree; i++){
				bufNum++;
				t = new insComponent(Buffer.getAtomType(), "B"+bufNum);
				unconnectedBuffer.add(t);
				cmpl.add(t);
				s2.add(t.name+".Read");
			}
			cntl.add(new insConnector(ct, "C"+conNum, s2));
			
			int inDegree = randomGenerator.nextInt(unconnectedBuffer.size())+2;
			if(ctl.containsKey("in"+inDegree+"out1in")){
				ct = ctl.get("in"+inDegree+"out1in");
			}
			else{
				ct = RandomConnector.createConnectorType(m, inDegree, 1, true);
				ctl.put(ct.getName(), ct);
			}
			
			conNum++;
			
			
			for(int i = 1; i < inDegree; i++){
				j = randomGenerator.nextInt(unconnectedBuffer.size());//pick one unconnected buffer
				t = unconnectedBuffer.get(j);
				sin.add(t.name+".Write");
				unconnectedBuffer.remove(j);
			}
			sin.add(f.name+".Read");
			cntl.add(new insConnector(ct, "C"+conNum, sin));
			
			
		}
		new BIPCompoundBuilder(m, cmpl, cntl, "Root", "root");
	}
}
