import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import BIPTransformation.TransformationFunction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.DataParameter;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Modules.Module;


public class Quorum {

	public static void main(String[] args) throws Exception {
		String testFile =  "/home/aaron/workspace/java/BIPRandomGeneration/example/Quorum22.bip";
		String instuctionFile = "/home/aaron/workspace/java/BIPRandomGeneration/example/Quorum22";		
		String output = "/home/aaron/workspace/java/BIPRandomGeneration/example/out3.bip";
		BIPRandomBuilder.createRandomCompound(testFile, instuctionFile, output, "Root");
	}

}

/*class plugConnector extends RandomAtomDescription{
	public plugConnector(String instuctionFile, String atomName) throws IOException{
		super(instuctionFile, atomName);
	}
	public void createAtomRandomPart(Module m, AtomType at,
			AtomType rat, Map<String, PortType> ptl,
			Map<String, Object> parOrVar, List<insPort> pl, List<String> sl,
			List<Action> rinitialAction, List<ExecutorTransition> tl){
		for(int i = 1; i <= root.numOfServe; i++){
			Variable rvar = BIPTransform.createVariable("server"+i, "bool");
			rat.getVariable().add(rvar);
			parOrVar.put(rvar.getName(), rvar);
		}
		
		List<Variable> eVar;
		for(int i = 1; i <= root.numOfServe; i++){
			eVar = new ArrayList<Variable>();
			eVar.add((Variable)parOrVar.get("clientId"));
			eVar.add((Variable)parOrVar.get("proposedValue"));
			pl.add(new insPort(1, "SendS"+i, ptl.get("ThirdType"), eVar));
		}
		
		List<Object> t1 = new ArrayList<Object>();
		t1.add((Variable)parOrVar.get("clientId"));
		for(int i = 1; i <= root.numOfServe; i++){
			t1.add((Variable)parOrVar.get("server"+i));
		}
		List<Object> v1 = new ArrayList<Object>();
		v1.add((DataParameter)parOrVar.get("id"));
		BooleanLiteral bl1;
		for(int i = 1; i <= root.numOfServe; i++){
			bl1 = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
			bl1.setBValue(false);
			v1.add(bl1);
		}

		rinitialAction.add(BIPTransform.createCompositeAction(t1, v1));
		
		for(int i = 1; i <= root.numOfServe; i++){
			UnaryExpression g1 = BIPTransform.createUnaryExpression(UnaryOperator.LOGICAL_NOT, BIPTransform.getExpression((Variable)parOrVar.get("server"+i)));
			List<Object> t2 = new ArrayList<Object>();t2.add((Variable)parOrVar.get("server"+i));
			List<Object> v2 = new ArrayList<Object>();
			BooleanLiteral bl2 = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
			bl2.setBValue(true);
			v2.add(bl2);
			tl.add(new ExecutorTransition("SendS"+i, "sendServer_rcvFServer", "sendServer_rcvFServer", g1, BIPTransform.createCompositeAction(t2, v2)));
			
			UnaryExpression g2 = BIPTransform.createUnaryExpression(UnaryOperator.LOGICAL_NOT, BIPTransform.getExpression((Variable)parOrVar.get("server"+i)));
			List<Object> t3 = new ArrayList<Object>();t3.add((Variable)parOrVar.get("server"+i));
			List<Object> v3 = new ArrayList<Object>();
			BooleanLiteral bl3 = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
			bl3.setBValue(true);
			v3.add(bl3);
			tl.add(new ExecutorTransition("SendS"+i, "sendServer_sendTClient", "sendServer_sendTClient", g2, BIPTransform.createCompositeAction(t3, v3)));
		}
		
		
		
	}
}

class root extends RandomCompoundDescription{
	static int numOfServe;
	static int numOfClient;
	public void createRandomCompound() throws Exception{
		
		
		Random randomGenerator = new Random();	
		numOfServe = randomGenerator.nextInt(4)+1;
		numOfClient = randomGenerator.nextInt(4)+1;
		
		CompoundType ct = TransformationFunction.ParseBIPFile(testFile);
		Module m = BIPTransform.createSystem(ct.getModule().getName());
		AtomType Client = BIPRandomBuilder.createRandomAtom(m, (AtomType)ct.getSubcomponent().get(0).getType(), null);
		AtomType ClientCon = BIPRandomBuilder.createRandomAtom(m, (AtomType)ct.getSubcomponent().get(2).getType(), new plugConnector(instuctionFile, ((AtomType)ct.getSubcomponent().get(2).getType()).getName()));
		AtomType ServerCon = BIPRandomBuilder.createRandomAtom(m, (AtomType)ct.getSubcomponent().get(4).getType(),null);
		AtomType Server = BIPRandomBuilder.createRandomAtom(m, (AtomType)ct.getSubcomponent().get(6).getType(),null);
		ConnectorType Singleton = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(0).getType(), null);
		ConnectorType ctCtoCC = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(10).getType(), null);
		ConnectorType ctCCtoSC = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(11).getType(), null);
		ConnectorType ctSCtoS = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(13).getType(), null);
		ConnectorType ctStoSC = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(14).getType(), null);
		ConnectorType ctSCtoCC = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(15).getType(), null);
		ConnectorType ctCCtoC = BIPRandomBuilder.createRandomConnector(m, (ConnectorType)ct.getConnector().get(17).getType(), null);
		
		List<insComponent> cmpl = new ArrayList<insComponent>();
		for(int i = 1; i <= numOfClient; i++){
			cmpl.add(new insComponent(Client, "plug"+i, i));
		}
		
		for(int i = 1; i <= numOfClient; i++){
			cmpl.add(new insComponent(ClientCon, "plugC"+i, i));
		}
		
		for(int i = 1; i <= numOfServe; i++){
			cmpl.add(new insComponent(ServerCon, "serverC"+i, i));
		}
		
		for(int i = 1; i <= numOfServe; i++){
			cmpl.add(new insComponent(Server, "s"+i, i));
		}
		
		List<insConnector> cntl = new ArrayList<insConnector>();
		
		for(int i = 1; i <= numOfClient; i++){
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
		
		for(int i = 1; i <= numOfServe; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("serverC"+i+".loose");
			cntl.add(new insConnector(Singleton, "loose"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("serverC"+i+".msglost");
			cntl.add(new insConnector(Singleton, "msglost"+i, s2));
					
		}
		for(int i = 1; i <= numOfClient; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("plug"+i+".sendToChannel");
			s1.add("plugC"+i+".rcvPlug");
			cntl.add(new insConnector(ctCtoCC, "CtoCC"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("plugC"+i+".sendClient");
			s2.add("plug"+i+".recieveFromServer");
			cntl.add(new insConnector(ctCCtoC, "CCtoC"+i, s2));
			
			for(int j = 1; j <= numOfServe; j++){
				List<String> s3 = new ArrayList<String>();
				s3.add("plugC"+i+".SendS"+j);
				s3.add("serverC"+j+".rcvPlugC");
				//System.out.println(s3);
				cntl.add(new insConnector(ctCCtoSC, "CC"+i+"SC"+j, s3));
			}
		}
		
		for(int i = 1; i <= numOfServe; i++){
			List<String> s1 = new ArrayList<String>();
			s1.add("s"+i+".SendToServerC");
			s1.add("serverC"+i+".rcvServer");
			cntl.add(new insConnector(ctStoSC, "StoSC"+i, s1));
			
			List<String> s2 = new ArrayList<String>();
			s2.add("serverC"+i+".sendServer");
			s2.add("s"+i+".RcvFromChannel");
			cntl.add(new insConnector(ctSCtoS, "SCtoS"+i, s2));
			
			for(int j = 1; j <= numOfClient; j++){
				List<String> s3 = new ArrayList<String>();
				s3.add("serverC"+i+".sendPlug");
				s3.add("plugC"+j+".rcvServer");
				cntl.add(new insConnector(ctSCtoCC, "SC"+i+"CC"+j, s3));
			}
		}
		
		new BIPCompoundBuilder(m, cmpl, cntl, "Root", "root");
		BIPTransform.createBIPFile("/home/aaron/workspace/java/BIPRandomGeneration/example/out1.bip", m);
	}
}*/