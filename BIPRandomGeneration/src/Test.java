

import java.util.*;

import ujf.verimag.bip.Core.Behaviors.*;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Modules.Module;
import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.*;

import java.util.Random;

import BIPTransformation.TransformationFunction;

public class Test {
	protected static ExpressionsFactory expressionFactory = ExpressionsFactory.eINSTANCE ; 
	
	static PortType ptFT;
	static PortType ptST;
	static PortType ptTT;
	static BIPAtomBuilder Client;
	static BIPAtomBuilder Server;
	static BIPAtomBuilder ClientCon;
	static BIPAtomBuilder ServerCon;
	static ConnectorType ctCtoCC;
	static ConnectorType ctCCtoSC;
	static ConnectorType ctSCtoS;
	static ConnectorType ctStoSC;
	static ConnectorType ctSCtoCC;
	static ConnectorType ctCCtoC;
	static ConnectorType Singleton;
	public static void main(String[] args) throws Exception{
		
		// TODO Auto-generated method stub
		//String testFile = "/home/aaron/workspace/java/BIPRandomGeneration/example/Quorum22.bip";
		//CompoundType ct = TransformationFunction.ParseBIPFile(testFile);
		
		//Module m = ct.getModule();
		//PortType pt = (PortType) m.getBipType().get(1);
		//AtomType at = new AtomType();
		
		//PetriNet bipBehaviour  = BIPTransform.createPetriNet(TransformationFunction.getAtomType(m).get(0));
		
		//System.out.println("The Module name is "+ct.getModule().getName());
		
		//System.out.println(ct.getName());
		
		//ct.setName("test");
		//for(int i = 0; i < ct.getSubcomponent().size(); i++)
		//AtomType at = TransformationFunction.getAtomType(m).get(0);
		//List<String> l = new ArrayList<String>();
		//l.add("id");
		//TransformationFunction.getComponent(ct, "");
		//System.out.println(bipBehaviour.getInitialState());
		
		//Module tm = BIPTransform.createModule("1");
		//BIPTransform.createAtomType(atomTypeName)
		//TransformationFunction.CreateBIPFile("/home/aaron/workspace/java/BIPRandomGeneration/example/out.bip", m);
		
		String inputFile = "/home/aaron/workspace/java/BIPRandomGeneration/example/Quorum22.bip";
		CompoundType ct = TransformationFunction.ParseBIPFile(inputFile);
		Module m = BIPTransform.createSystem(ct.getModule().getName());
		

		ptFT = BIPTransform.createPortType("FirstType");
		
		
		ArrayList<String> ptSTParType = new ArrayList<String>();
		ptSTParType.add("int");
		ArrayList<String> ptSTParName = new ArrayList<String>();
		ptSTParName.add("x");
		ptST = BIPTransform.createParameterisedPortType("SecondType", ptSTParType,ptSTParName);
		
		ArrayList<String> ptTTParType = new ArrayList<String>();
		ptTTParType.add("int");	ptTTParType.add("int");
		ArrayList<String> ptTTParName = new ArrayList<String>();
		ptTTParName.add("x");		ptTTParName.add("y");
		ptTT = BIPTransform.createParameterisedPortType("ThirdType", ptTTParType,ptTTParName);
		
		m.getBipType().add(ptFT);
		m.getBipType().add(ptST);
		m.getBipType().add(ptTT);
		Random randomGenerator = new Random();
		
		int numCli = randomGenerator.nextInt(4)+1;
		int numSrv = randomGenerator.nextInt(4)+1;
		
		Client = Test.createClient(m);
		Server = Test.createServer(m);
		ClientCon = Test.createClientCon(m, numSrv);
		ServerCon = Test.createServerCon(m);
		Test.createctCtoCC(m);
		Test.createctCCtoSC(m);
		Test.createctSCtoS(m);
		Test.createctStoSC(m);
		Test.createctSCtoCC(m);
		Test.createctCCtoC(m);
		Test.createSingleton(m);
		Test.createRoot(m, numCli, numSrv);
		
		
		
		
		
		
		BIPTransform.createBIPFile("/home/aaron/workspace/java/BIPRandomGeneration/example/out1.bip", m);
		
		
		
	}
	
	static BIPAtomBuilder createClient(Module m){
		


		
		AtomType at = BIPTransform.createAtomType("CustomerPlugQuorum");
		BIPTransform.addAtomDataParameter(at, "int", "id");
		
		at.getVariable().add(BIPTransform.createVariable("proposedValue", "int"));
		at.getVariable().add(BIPTransform.createVariable("decidedValueS", "int"));
		at.getVariable().add(BIPTransform.createVariable("decidedValue", "int"));
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(0));		
		pl.add(new insPort(1, "sendToChannel", ptST, eVar1));
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(1));
		pl.add(new insPort(1, "recieveFromServer", ptST, eVar2));
		pl.add(new insPort(1, "sendToClient", ptFT, new ArrayList<Variable>()));
		pl.add(new insPort(1, "switchB", ptFT, new ArrayList<Variable>()));
		pl.add(new insPort(1, "ticker", ptFT, new ArrayList<Variable>()));
		
		List<String> sl = new ArrayList<String>();
		sl.add("S1"); sl.add("S2");sl.add("S3");sl.add("S4");sl.add("S5");sl.add("S6");
		sl.add("S7");sl.add("S8");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();t1.add(at.getVariable().get(0)); t1.add(at.getVariable().get(1)); t1.add(at.getVariable().get(2));
		List<Object> v1 = new ArrayList<Object>();
		v1.add(at.getDataParameter().get(0));
		IntegerLiteral il1 = expressionFactory.createIntegerLiteral();
		il1.setIValue(0);
		v1.add(il1);
		IntegerLiteral il2 = expressionFactory.createIntegerLiteral();
		il2.setIValue(0);
		v1.add(il2);
		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		//tl.add(new ExecutorTransition("", "initial", "S1",  BIPTransform.createCompositeAction(tt1Cus, vt1Cus)));
		
		tl.add(new ExecutorTransition("sendToChannel", "S1", "S2"));
		
		List<Object> t2 = new ArrayList<Object>();t2.add(at.getVariable().get(2));
		List<Object> v2 = new ArrayList<Object>();v2.add(at.getVariable().get(1));
		tl.add(new ExecutorTransition("recieveFromServer", "S2", "S3", BIPTransform.createCompositeAction(t2, v2)));
		
		tl.add(new ExecutorTransition("ticker", "S2", "S4"));
		
		List<Object> t3 = new ArrayList<Object>();t3.add(at.getVariable().get(2));
		List<Object> v3 = new ArrayList<Object>();v3.add(at.getVariable().get(1));
		tl.add(new ExecutorTransition("recieveFromServer", "S4", "S6", BIPTransform.createCompositeAction(t3, v3)));
		
		tl.add(new ExecutorTransition("recieveFromServer", "S3", "S5"));
		
		tl.add(new ExecutorTransition("ticker", "S3", "S6"));
		
		tl.add(new ExecutorTransition("switchB", "S6", "S8"));
		
		tl.add(new ExecutorTransition("ticker", "S7", "S7"));
		
		tl.add(new ExecutorTransition("ticker", "S8", "S8"));
		
		tl.add(new ExecutorTransition("ticker", "S4", "S4"));
		
		List<Object> t4 = new ArrayList<Object>();t4.add(at.getVariable().get(2));
		List<Object> v4 = new ArrayList<Object>();v4.add(at.getVariable().get(0));
		BinaryExpression g1 = BIPTransform.createBinaryExpression(BIPTransform.getExpression(at.getVariable().get(2)), BinaryOperator.INEQUALITY, BIPTransform.getExpression(at.getVariable().get(1)));
		tl.add(new ExecutorTransition("switchB", "S5", "S8", g1, BIPTransform.createCompositeAction(t4, v4)));
		
		BinaryExpression g2 = BIPTransform.createBinaryExpression(BIPTransform.getExpression(at.getVariable().get(2)), BinaryOperator.EQUALITY, BIPTransform.getExpression(at.getVariable().get(1)));
		tl.add(new ExecutorTransition("sendToClient", "S5", "S7", g2));
		
		
		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "S1", initialAction);
		BIPAtomBuilder CustomerPlugQuorum = new BIPAtomBuilder(behav, m);
		return CustomerPlugQuorum;
	}
	static BIPAtomBuilder createServer(Module m){
		AtomType at = BIPTransform.createAtomType("Server");
		BIPTransform.addAtomDataParameter(at, "int", "id");
		
		at.getVariable().add(BIPTransform.createVariable("decidedValue", "int"));
		at.getVariable().add(BIPTransform.createVariable("proposedValue", "int"));
		at.getVariable().add(BIPTransform.createVariable("serverId", "int"));
		at.getVariable().add(BIPTransform.createVariable("ClientId", "int"));
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(3));
		eVar1.add(at.getVariable().get(1));
		pl.add(new insPort(1, "RcvFromChannel", ptTT, eVar1));
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(3));
		eVar2.add(at.getVariable().get(0));
		pl.add(new insPort(1, "SendToServerC", ptTT, eVar2));
		
		List<String> sl = new ArrayList<String>();
		sl.add("RECEIVE"); sl.add("SEND");sl.add("START");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();t1.add(at.getVariable().get(0)); t1.add(at.getVariable().get(2));
		List<Object> v1 = new ArrayList<Object>();
		v1.add(at.getDataParameter().get(0));
		IntegerLiteral il1 = expressionFactory.createIntegerLiteral();
		il1.setIValue(0);
		v1.add(il1);
		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		
		tl.add(new ExecutorTransition("SendToServerC", "SEND", "RECEIVE"));
		
		tl.add(new ExecutorTransition("RcvFromChannel", "RECEIVE", "SEND"));
		
		List<Object> t2 = new ArrayList<Object>();t2.add(at.getVariable().get(0));
		List<Object> v2 = new ArrayList<Object>();v2.add(at.getVariable().get(1));
		tl.add(new ExecutorTransition("RcvFromChannel", "START", "SEND", BIPTransform.createCompositeAction(t2, v2)));
	
		
		
		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "START", initialAction);
		BIPAtomBuilder Server = new BIPAtomBuilder(behav, m);
		return Server;
	}
	static BIPAtomBuilder createClientCon(Module m, int numSrv){
		AtomType at = BIPTransform.createAtomType("plugConnector");
		BIPTransform.addAtomDataParameter(at, "int", "id");
		
		at.getVariable().add(BIPTransform.createVariable("clientId", "int"));
		at.getVariable().add(BIPTransform.createVariable("proposedValue", "int"));
		at.getVariable().add(BIPTransform.createVariable("decidedValue", "int"));
		for(int i = 1; i <= numSrv; i++){
			at.getVariable().add(BIPTransform.createVariable("server"+i, "bool"));
		}
		
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(1));
		pl.add(new insPort(1, "rcvPlug", ptST, eVar1));
		
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(2));
		pl.add(new insPort(1, "sendClient", ptST, eVar2));
		
		List<Variable> eVar3 = new ArrayList<Variable>();
		eVar3.add(at.getVariable().get(0));
		eVar3.add(at.getVariable().get(2));
		pl.add(new insPort(1, "rcvServer", ptTT, eVar3));
		
		List<Variable> eVar;
		for(int i = 1; i <= numSrv; i++){
			eVar = new ArrayList<Variable>();
			eVar.add(at.getVariable().get(0));
			eVar.add(at.getVariable().get(1));
			pl.add(new insPort(1, "SendS"+i, ptTT, eVar));
		}
		
		List<String> sl = new ArrayList<String>();
		sl.add("start_rcvFServer"); sl.add("sendServer_rcvFServer");sl.add("start_sendTClient");sl.add("sendServer_sendTClient");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		
		List<Object> t1 = new ArrayList<Object>();
		t1.add(at.getVariable().get(0));
		for(int i = 1; i <= numSrv; i++){
			t1.add(at.getVariable().get(2+i));
		}
		List<Object> v1 = new ArrayList<Object>();
		v1.add(at.getDataParameter().get(0));
		BooleanLiteral bl1;
		for(int i = 1; i <= numSrv; i++){
			bl1 = expressionFactory.createBooleanLiteral();
			bl1.setBValue(false);
			v1.add(bl1);
		}

		Action initialAction = BIPTransform.createCompositeAction(t1, v1);
		
		
		tl.add(new ExecutorTransition("rcvPlug", "start_rcvFServer", "sendServer_rcvFServer"));
		
		tl.add(new ExecutorTransition("rcvPlug", "start_sendTClient", "sendServer_sendTClient"));
		
		for(int i = 1; i <= numSrv; i++){
			
			UnaryExpression g1 = BIPTransform.createUnaryExpression(UnaryOperator.LOGICAL_NOT, BIPTransform.getExpression(at.getVariable().get(2+i)));
			List<Object> t2 = new ArrayList<Object>();t2.add(at.getVariable().get(2+i));
			List<Object> v2 = new ArrayList<Object>();
			BooleanLiteral bl2 = expressionFactory.createBooleanLiteral();
			bl2.setBValue(true);
			v2.add(bl2);
			tl.add(new ExecutorTransition("SendS"+i, "sendServer_rcvFServer", "sendServer_rcvFServer", g1, BIPTransform.createCompositeAction(t2, v2)));
			
			UnaryExpression g2 = BIPTransform.createUnaryExpression(UnaryOperator.LOGICAL_NOT, BIPTransform.getExpression(at.getVariable().get(2+i)));
			List<Object> t3 = new ArrayList<Object>();t3.add(at.getVariable().get(2+i));
			List<Object> v3 = new ArrayList<Object>();
			BooleanLiteral bl3 = expressionFactory.createBooleanLiteral();
			bl3.setBValue(true);
			v3.add(bl3);
			tl.add(new ExecutorTransition("SendS"+i, "sendServer_sendTClient", "sendServer_sendTClient", g2, BIPTransform.createCompositeAction(t3, v3)));
		}
		
		tl.add(new ExecutorTransition("rcvServer", "start_rcvFServer", "start_sendTClient"));
		
		tl.add(new ExecutorTransition("rcvServer", "sendServer_rcvFServer", "sendServer_sendTClient"));
		
		tl.add(new ExecutorTransition("sendClient", "start_sendTClient", "start_rcvFServer"));
		
		tl.add(new ExecutorTransition("sendClient", "sendServer_sendTClient", "sendServer_rcvFServer"));
		
		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "start_rcvFServer", initialAction);
		BIPAtomBuilder ClientCon = new BIPAtomBuilder(behav, m);
		return ClientCon;
	}
	static BIPAtomBuilder createServerCon(Module m){
		AtomType at = BIPTransform.createAtomType("ServerConnector");
		BIPTransform.addAtomDataParameter(at, "int", "id");
		
		at.getVariable().add(BIPTransform.createVariable("proposed", "int"));
		at.getVariable().add(BIPTransform.createVariable("decided", "int"));
		at.getVariable().add(BIPTransform.createVariable("clientIdSend", "int"));
		at.getVariable().add(BIPTransform.createVariable("clientIdRcv", "int"));
		
		
		
		
		
		List<insPort> pl = new ArrayList<insPort>();
		List<Variable> eVar1 = new ArrayList<Variable>();
		eVar1.add(at.getVariable().get(3));
		eVar1.add(at.getVariable().get(0));
		pl.add(new insPort(1, "rcvPlugC", ptTT, eVar1));
		
		List<Variable> eVar2 = new ArrayList<Variable>();
		eVar2.add(at.getVariable().get(3));
		eVar2.add(at.getVariable().get(0));
		pl.add(new insPort(1, "sendServer", ptTT, eVar2));
		
		List<Variable> eVar3 = new ArrayList<Variable>();
		eVar3.add(at.getVariable().get(2));
		eVar3.add(at.getVariable().get(1));
		pl.add(new insPort(1, "rcvServer", ptTT, eVar3));
		
		List<Variable> eVar4 = new ArrayList<Variable>();
		eVar4.add(at.getVariable().get(2));
		eVar4.add(at.getVariable().get(1));
		pl.add(new insPort(1, "sendPlug", ptTT, eVar4));
		
		pl.add(new insPort(1, "loose", ptFT, new ArrayList<Variable>()));
		
		pl.add(new insPort(1, "msglost", ptFT, new ArrayList<Variable>()));
		
		
		List<String> sl = new ArrayList<String>();
		sl.add("rcvPlug_rcvFServer"); 
		sl.add("sendTServer_rcvFServer");
		sl.add("crash_rcvFServer");
		sl.add("rcvPlug_sendPlugC");
		sl.add("sendTServer_sendPlugC");
		sl.add("crash_sendPlugC");
		
		List<ExecutorTransition> tl = new ArrayList<ExecutorTransition>();
		


		Action initialAction = BIPTransform.createCompositeAction(new ArrayList<Object>(), new ArrayList<Object>());
		
		
		tl.add(new ExecutorTransition("rcvPlugC", "rcvPlug_rcvFServer", "sendTServer_rcvFServer"));
		
		tl.add(new ExecutorTransition("rcvPlugC", "rcvPlug_sendPlugC", "sendTServer_sendPlugC"));

		tl.add(new ExecutorTransition("loose", "sendTServer_rcvFServer", "crash_rcvFServer"));
		
		tl.add(new ExecutorTransition("loose", "sendTServer_sendPlugC", "crash_sendPlugC"));
		
		tl.add(new ExecutorTransition("rcvPlugC", "crash_rcvFServer", "crash_rcvFServer"));
		
		tl.add(new ExecutorTransition("rcvPlugC", "crash_sendPlugC", "crash_sendPlugC"));
		
		tl.add(new ExecutorTransition("msglost", "sendTServer_rcvFServer", "rcvPlug_rcvFServer"));
		
		tl.add(new ExecutorTransition("msglost", "sendTServer_sendPlugC", "rcvPlug_sendPlugC"));
		
		tl.add(new ExecutorTransition("sendServer", "sendTServer_rcvFServer", "rcvPlug_rcvFServer"));
		
		tl.add(new ExecutorTransition("sendServer", "sendTServer_sendPlugC", "rcvPlug_sendPlugC"));
		
		tl.add(new ExecutorTransition("rcvServer", "rcvPlug_rcvFServer", "rcvPlug_sendPlugC"));
		
		tl.add(new ExecutorTransition("rcvServer", "sendTServer_rcvFServer", "sendTServer_sendPlugC"));
		
		tl.add(new ExecutorTransition("rcvServer", "crash_rcvFServer", "crash_sendPlugC"));
		

		tl.add(new ExecutorTransition("sendPlug", "rcvPlug_sendPlugC", "rcvPlug_rcvFServer"));
		
		tl.add(new ExecutorTransition("sendPlug", "sendTServer_sendPlugC", "sendTServer_rcvFServer"));
		
		tl.add(new ExecutorTransition("sendPlug", "crash_sendPlugC", "crash_rcvFServer"));

		BehaviourImpl behav = new BehaviourImpl(at,  pl, sl, tl, "rcvPlug_rcvFServer", initialAction);
		BIPAtomBuilder ServerCon = new BIPAtomBuilder(behav, m);
		return ServerCon;
	}
	static void createctCtoCC(Module m){
		ctCtoCC = BIPTransform.createConnectorType("SendFromPlugToPlugConnector",ptST,2);
		
		BIPTransform.setConnectorTypeDefinition(ctCtoCC, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctCtoCC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctCtoCC.getPortParameter().get(0), 0);
		
		
		
		
		List<Object> t1 = new ArrayList<Object>();t1.add(rdpr1);
		List<Object> v1 = new ArrayList<Object>();v1.add(rdpr2);
		Action aCtoCC = BIPTransform.createCompositeAction(t1, v1);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification isCtoCC = BIPTransform.createInteractionSpecification(ctCtoCC, isTrigg);
		ctCtoCC.getInteractionSpecification().add(isCtoCC);
		ctCtoCC.getInteractionSpecification().get(0).setDownAction(aCtoCC);
		m.getBipType().add(ctCtoCC);
	}
	static void createctCCtoSC(Module m){
		ctCCtoSC = BIPTransform.createConnectorType("SendFromPlugConToServerCon",ptTT,2);
		
		BIPTransform.setConnectorTypeDefinition(ctCCtoSC, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctCCtoSC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctCCtoSC.getPortParameter().get(0), 0);
		
		RequiredDataParameterReference  rdpr3 = BIPTransform.getExpression(ctCCtoSC.getPortParameter().get(1), 1);
		
		RequiredDataParameterReference  rdpr4 = BIPTransform.getExpression(ctCCtoSC.getPortParameter().get(0), 1);
		
		List<Object> tl = new ArrayList<Object>();tl.add(rdpr1);tl.add(rdpr3);
		List<Object> vl = new ArrayList<Object>();vl.add(rdpr2);vl.add(rdpr4);
		Action ca = BIPTransform.createCompositeAction(tl, vl);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(ctCCtoSC, isTrigg);
		ctCCtoSC.getInteractionSpecification().add(is);
		ctCCtoSC.getInteractionSpecification().get(0).setDownAction(ca);
		m.getBipType().add(ctCCtoSC);
	}
	static void createctSCtoS(Module m){

		ctSCtoS = BIPTransform.createConnectorType("SendFromServerConnToServer",ptTT,2);
		
		BIPTransform.setConnectorTypeDefinition(ctSCtoS, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctSCtoS.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctSCtoS.getPortParameter().get(0), 0);
		
		RequiredDataParameterReference  rdpr3 = BIPTransform.getExpression(ctSCtoS.getPortParameter().get(1), 1);
		
		RequiredDataParameterReference  rdpr4 = BIPTransform.getExpression(ctSCtoS.getPortParameter().get(0), 1);
		
		List<Object> tl = new ArrayList<Object>();tl.add(rdpr1);tl.add(rdpr3);
		List<Object> vl = new ArrayList<Object>();vl.add(rdpr2);vl.add(rdpr4);
		Action ca = BIPTransform.createCompositeAction(tl, vl);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(ctSCtoS, isTrigg);
		ctSCtoS.getInteractionSpecification().add(is);
		ctSCtoS.getInteractionSpecification().get(0).setDownAction(ca);
		m.getBipType().add(ctSCtoS);
	}
	static void createctStoSC(Module m){

		ctStoSC = BIPTransform.createConnectorType("SendFromServerToServerC",ptTT,2);
		
		BIPTransform.setConnectorTypeDefinition(ctStoSC, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctStoSC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctStoSC.getPortParameter().get(0), 0);
		
		RequiredDataParameterReference  rdpr3 = BIPTransform.getExpression(ctStoSC.getPortParameter().get(1), 1);
		
		RequiredDataParameterReference  rdpr4 = BIPTransform.getExpression(ctStoSC.getPortParameter().get(0), 1);
		
		List<Object> tl = new ArrayList<Object>();tl.add(rdpr1);tl.add(rdpr3);
		List<Object> vl = new ArrayList<Object>();vl.add(rdpr2);vl.add(rdpr4);
		Action ca = BIPTransform.createCompositeAction(tl, vl);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(ctStoSC, isTrigg);
		ctStoSC.getInteractionSpecification().add(is);
		ctStoSC.getInteractionSpecification().get(0).setDownAction(ca);
		m.getBipType().add(ctStoSC);
	}
	static void createctSCtoCC(Module m){
		ctSCtoCC = BIPTransform.createConnectorType("ServerCPlugC",ptTT,2);
		
		BIPTransform.setConnectorTypeDefinition(ctSCtoCC, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(0), 0);
		
		RequiredDataParameterReference  rdpr3 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(1), 1);
		
		RequiredDataParameterReference  rdpr4 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(0), 1);
		
		List<Object> tl = new ArrayList<Object>();tl.add(rdpr1);tl.add(rdpr3);
		List<Object> vl = new ArrayList<Object>();vl.add(rdpr2);vl.add(rdpr4);
		Action ca = BIPTransform.createCompositeAction(tl, vl);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(ctSCtoCC, isTrigg);
		ctSCtoCC.getInteractionSpecification().add(is);
		
		ctSCtoCC.getInteractionSpecification().get(0).setDownAction(ca);

		RequiredDataParameterReference  rdpr5 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr6 = BIPTransform.getExpression(ctSCtoCC.getPortParameter().get(0), 0);
		BinaryExpression g = BIPTransform.createBinaryExpression(rdpr5, BinaryOperator.EQUALITY, rdpr6);
		ctSCtoCC.getInteractionSpecification().get(0).setGuard(g);
		m.getBipType().add(ctSCtoCC);
	}
	static void createctCCtoC(Module m){
		ctCCtoC = BIPTransform.createConnectorType("PlugCToPlug",ptST,2);
		
		BIPTransform.setConnectorTypeDefinition(ctCCtoC, new ArrayList<Integer>());
		
		RequiredDataParameterReference  rdpr1 = BIPTransform.getExpression(ctCCtoC.getPortParameter().get(1), 0);
		
		RequiredDataParameterReference  rdpr2 = BIPTransform.getExpression(ctCCtoC.getPortParameter().get(0), 0);
		
		
		
		
		List<Object> t1 = new ArrayList<Object>();t1.add(rdpr1);
		List<Object> v1 = new ArrayList<Object>();v1.add(rdpr2);
		Action aCtoCC = BIPTransform.createCompositeAction(t1, v1);
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);isTrigg.add(1);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(ctCCtoC, isTrigg);
		ctCCtoC.getInteractionSpecification().add(is);
		ctCCtoC.getInteractionSpecification().get(0).setDownAction(aCtoCC);
		m.getBipType().add(ctCCtoC);
	}
	static void createSingleton(Module m){
		Singleton = BIPTransform.createConnectorType("Singleton",ptFT,1);
		
		BIPTransform.setConnectorTypeDefinition(Singleton, new ArrayList<Integer>());
		
		
		List<Integer> isTrigg = new ArrayList<Integer>();
		isTrigg.add(0);
		InteractionSpecification is = BIPTransform.createInteractionSpecification(Singleton, isTrigg);
		Singleton.getInteractionSpecification().add(is);
		//Singleton.getInteractionSpecification().get(0).setDownAction(ActionsFactory.eINSTANCE.createCompositeAction());
		//Singleton.getInteractionSpecification().get(0).setUpAction(ActionsFactory.eINSTANCE.createCompositeAction());
		m.getBipType().add(Singleton);
	}
	static void createRoot(Module m, int numCli, int numSrv){
		List<insComponent> cmpl = new ArrayList<insComponent>();
		for(int i = 1; i <= numCli; i++){
			cmpl.add(new insComponent(Client.getAtomType(), "plug"+i, i));
		}
		
		for(int i = 1; i <= numCli; i++){
			cmpl.add(new insComponent(ClientCon.getAtomType(), "plugC"+i, i));
		}
		
		for(int i = 1; i <= numSrv; i++){
			cmpl.add(new insComponent(ServerCon.getAtomType(), "serverC"+i, i));
		}
		
		for(int i = 1; i <= numSrv; i++){
			cmpl.add(new insComponent(Server.getAtomType(), "s"+i, i));
		}
		
		List<insConnector> cntl = new ArrayList<insConnector>();
		
		for(int i = 1; i <= numCli; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("plug"+i+".sendToClient");
			cntl.add(new insConnector(Singleton, "sendToClient"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("plug"+i+".switchB");
			cntl.add(new insConnector(Singleton, "switchB"+i, s2));
			
			List<String> s3 = new ArrayList<String>();
			s3.add("plug"+i+".ticker");
			cntl.add(new insConnector(Singleton, "ticker"+i, s3));			
		}
		
		for(int i = 1; i <= numSrv; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("serverC"+i+".loose");
			cntl.add(new insConnector(Singleton, "loose"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("serverC"+i+".msglost");
			cntl.add(new insConnector(Singleton, "msglost"+i, s2));
					
		}
		for(int i = 1; i <= numCli; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("plug"+i+".sendToChannel");
			s1.add("plugC"+i+".rcvPlug");
			cntl.add(new insConnector(ctCtoCC, "CtoCC"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("plugC"+i+".sendClient");
			s2.add("plug"+i+".recieveFromServer");
			cntl.add(new insConnector(ctCCtoC, "CCtoC"+i, s2));
			
			for(int j = 1; j <= numSrv; j++){
				List<String> s3 = new ArrayList<String>();
				s3.add("plugC"+i+".SendS"+j);
				s3.add("serverC"+j+".rcvPlugC");
				//System.out.println(s3);
				cntl.add(new insConnector(ctCCtoSC, "CC"+i+"SC"+j, s3));
			}
		}
		
		for(int i = 1; i <= numSrv; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("s"+i+".SendToServerC");
			s1.add("serverC"+i+".rcvServer");
			cntl.add(new insConnector(ctStoSC, "StoSC"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("serverC"+i+".sendServer");
			s2.add("s"+i+".RcvFromChannel");
			cntl.add(new insConnector(ctSCtoS, "SCtoS"+i, s2));
			
			for(int j = 1; j <= numCli; j++){
				List<String> s3 = new ArrayList<String>();
				s3.add("serverC"+i+".sendPlug");
				s3.add("plugC"+j+".rcvServer");
				cntl.add(new insConnector(ctSCtoCC, "SC"+i+"CC"+j, s3));
			}
		}
		
		new BIPCompoundBuilder(m, cmpl, cntl, "Root", "root");
	}
}

