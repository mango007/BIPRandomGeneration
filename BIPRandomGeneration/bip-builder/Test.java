package org.bip.builder;



import helpers.Helpers;

import java.io.File;
import connectorsTransformers.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.bip.behaviour.BehaviourImpl;
import org.bip.executor.OSGiExecutor;
import org.bip.glue.BIPGlue;
import org.bip.spec.RouteOnOffMonitor;
import org.bip.spec.SwitchableRoute;
import org.bip.util.BIPGlueParser;

import types.ConnectorNode;
import types.Tuple;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Modules.Module;

public class Test {

	 private static Map<BehaviourImpl,Integer> cti = new HashMap<BehaviourImpl, Integer>();
	 public static BehaviourImpl createExample() {
		 org.bip.behaviour.Transition t1 = new org.bip.behaviour.Transition ("START", "END" , "TransitionNAME");
		 org.bip.behaviour.Transition t2 = new org.bip.behaviour.Transition ("START", "MIDDLE" , "TransitionNAME2");
		 ArrayList<org.bip.behaviour.Transition> transList = new ArrayList<org.bip.behaviour.Transition>();
		 transList.add(t1);
		 transList.add(t2);
		 org.bip.behaviour.Port p1 = new org.bip.behaviour.Port ("myPort");
		 org.bip.behaviour.Port p2 = new org.bip.behaviour.Port ("myPort2");
		 ArrayList<org.bip.behaviour.Port> portList = new ArrayList<org.bip.behaviour.Port>();
		 portList.add(p1);
		 portList.add(p2);
		 ArrayList<String> stateList = new ArrayList<String>();
		 stateList.add("START");
		 stateList.add("MIDDLE");
		 stateList.add("END");
		 org.bip.behaviour.Guard g1 = new org.bip.behaviour.Guard("myGuard", "gurad1");
		 ArrayList<org.bip.behaviour.Guard> guardList = new ArrayList<org.bip.behaviour.Guard>();
		 guardList.add(g1);		 		 
		 BehaviourImpl b1 = new BehaviourImpl("Atom","START", transList, portList,stateList,guardList);
		 
		 return b1;
	 }
	 
	 
	 public static void main(String[] args) throws FileNotFoundException {
		 SwitchableRoute route1 = new SwitchableRoute("1");
		 RouteOnOffMonitor monitor = new RouteOnOffMonitor(2);
		 OSGiExecutor executor1 = new OSGiExecutor(route1);
		 OSGiExecutor executor2 = new OSGiExecutor(monitor);
		 Module m = BIPTransform.createSystem("myModule"); 
		 cti.put((BehaviourImpl)executor1.behaviour, 3);
		 cti.put((BehaviourImpl)executor2.behaviour, 1); 
		 ConstraintsParser pc = new ConstraintsParser("src/test/resources/bipGlue.xml");
		 List<Connector> connectorList = Main.returnNodeList(pc.getBooleanFunction());
		 BIPCompoundBuilder bcb = new BIPCompoundBuilder(cti,m,connectorList,"typeName","compoundName");
		
		 
		 BIPTransform.createBIPFile("output.bip", bcb.getModule());
	 }
	 
	 
		

}
