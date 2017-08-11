import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ArrayNavigationExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.VariableReference;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.DataParameter;
import ujf.verimag.bip.Core.Behaviors.Expression;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Behaviors.PortDefinition;
import ujf.verimag.bip.Core.Behaviors.PortDefinitionReference;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.Transition;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Modules.OpaqueElement;
class RandomParameterDescription{
	List<Integer> randomPar = new ArrayList<Integer>();
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
}

class RandomVariableDescription{
	List<Integer> randomVar = new ArrayList<Integer>();
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
}

class RandomPortVariable{
	List<Integer> randomPortVar = new ArrayList<Integer>();
	String varName = null;
	List<String> group = new ArrayList<String>();
	RandomPortVariable(){}
	RandomPortVariable(RandomPortVariable rpv){
		this.varName = rpv.varName;
		for(Integer i: rpv.randomPortVar)
			this.randomPortVar.add(i);
		for(String s: rpv.group)
			this.group.add(s);
	}
}
class RandomPortDescription{
	List<Integer> randomPort = new ArrayList<Integer>();
	String name = null;
	String type = null;
	Boolean isExport = null;
	List<RandomPortVariable> var = new ArrayList<RandomPortVariable>();
	List<String> group = new ArrayList<String>();
	RandomPortDescription(){}
	RandomPortDescription(RandomPortDescription rpd){
		for(String s: rpd.group)
			this.group.add(s);
		this.name = rpd.name;
		this.type = rpd.type;
		this.isExport = rpd.isExport;
		for(Integer i: rpd.randomPort)
			this.randomPort.add(i);
		for(RandomPortVariable rpv: rpd.var){
			RandomPortVariable rrpd = new RandomPortVariable();
			rrpd.varName = rpv.varName;
			for(Integer i: rpv.randomPortVar)
				rrpd.randomPortVar.add(i);
			this.var.add(rrpd);
		}
	}
}

class RandomStateDescription{
	List<Integer> randomState = new ArrayList<Integer>();
	String name = null;
	List<String> group = new ArrayList<String>();
}

class RandomGuardDescription{
	String Type = null;
}
class UnaryExpression extends RandomGuardDescription{
	String oprandName = null;
	UnaryOperator op = null;
}
class BinaryExpression extends RandomGuardDescription{
	String left = null;
	String right = null;
	BinaryOperator op = null;
}

class RandomActionDescription{
	List<Integer> random = new ArrayList<Integer>();
	String Type = null;
	List<String> group = new ArrayList<String>();
	
}
class AssignmentAction extends RandomActionDescription{
	String target = null;
	String value = null;
	AssignmentAction(){}
	AssignmentAction(AssignmentAction aa){
		this.target = aa.target;
		this.value = aa.value;
		this.Type = aa.Type;
		for(Integer i: aa.random)
			this.random.add(i);
		for(String s: aa.group)
			this.group.add(s);
	}
}

class RandomTransitionDescription{
	List<Integer> randomTransition = new ArrayList<Integer>();
	String port = null;
	String sourceState = null;
	String targetState = null;
	RandomGuardDescription guard = null;
	List<RandomActionDescription> action = new ArrayList<RandomActionDescription>();
	List<String> group = new ArrayList<String>();
	RandomTransitionDescription(){}
	RandomTransitionDescription(RandomTransitionDescription rtd){
		for(Integer i: rtd.randomTransition){
			this.randomTransition.add(i);
		}
		for(String s: rtd.group)
			this.group.add(s);
		this.port = rtd.port;
		this.sourceState = rtd.sourceState;
		this.targetState = rtd.targetState;
		
		if(rtd.guard != null && rtd.guard.Type.equals("UnaryExpression")){
			UnaryExpression ue = new UnaryExpression();
			ue.Type = "UnaryExpression";
			ue.oprandName = ((UnaryExpression)rtd.guard).oprandName;
			ue.op = ((UnaryExpression)rtd.guard).op;
			this.guard = ue;
		}
		else if(rtd.guard != null && rtd.guard.Type.equals("BinaryExpression")){
			BinaryExpression be = new BinaryExpression();
			be.Type = "BinaryExpression";
			be.left = ((BinaryExpression)rtd.guard).left;
			be.right = ((BinaryExpression)rtd.guard).right;
			be.op = ((BinaryExpression)rtd.guard).op;
			this.guard = be;
		}	
		
		for(RandomActionDescription rad: rtd.action){
			if(rad.Type.equals("AssignmentAction")){
				AssignmentAction aa = new AssignmentAction();
				aa.Type = "AssignmentAction";
				aa.target = ((AssignmentAction)rad).target;
				aa.value = ((AssignmentAction)rad).value;
				for(Integer i: rad.random)
					aa.random.add(i);
				for(String s: rad.group)
					aa.group.add(s);
				this.action.add(aa);
			}
		}
	}
}

class RandomInitialTransitionDescription{
	List<RandomActionDescription> action = new ArrayList<RandomActionDescription>();
	RandomInitialTransitionDescription(){}
	RandomInitialTransitionDescription(RandomInitialTransitionDescription ritd){
		for(RandomActionDescription rad: ritd.action)
			if(rad instanceof AssignmentAction){
				AssignmentAction rrad = new AssignmentAction();
				rrad.Type = ((AssignmentAction)rad).Type;
				rrad.target = ((AssignmentAction)rad).target;
				rrad.value = ((AssignmentAction)rad).value;
				for(Integer i: ((AssignmentAction)rad).random){
					rrad.random.add(i);
				}
				for(String s: ((AssignmentAction)rad).group)
					rrad.group.add(s);
				this.action.add(rrad);
			}
	}
}
class RandomAtomDescription {
	boolean isRandomType = false;
	String originalAtomType = null;
	
	List<Integer> randomPar = new ArrayList<Integer>();
	List<Integer> randomVar = new ArrayList<Integer>();
	List<Integer> randomPort = new ArrayList<Integer>();
	List<Integer> randomState = new ArrayList<Integer>();
	boolean isRandomInitAct = false;	
	List<Integer> randomTransition = new ArrayList<Integer>();
	
	List<RandomParameterDescription> randomParDes= new ArrayList<RandomParameterDescription>();
	List<RandomVariableDescription> randomVarDes = new ArrayList<RandomVariableDescription>();
	List<RandomPortDescription> randomPortDes = new ArrayList<RandomPortDescription>();
	List<RandomStateDescription> randomStateDes = new ArrayList<RandomStateDescription>();
	RandomInitialTransitionDescription randomInitDes = null;
	List<RandomTransitionDescription> randomTransDes = new ArrayList<RandomTransitionDescription>();
	String initState = null;
	
	
	public RandomAtomDescription(String instuctionFile, String atomName) throws IOException{
		Reader r = new BufferedReader(new FileReader(instuctionFile));
		String sCurrentLine;
		while ((sCurrentLine = ((BufferedReader)r).readLine()) != null) {
			String[] parser0 = sCurrentLine.split(" ");
			if(parser0.length > 0 && parser0[0].equals("Atomic:"+atomName)){
				if(parser0.length > 2)
					originalAtomType = parser0[2];
				isRandomType = true;
				while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("end")){
					String[] parser = sCurrentLine.split(":");
					if(parser[0].equals("	RandomParameter")){
						RandomParameterDescription rpd = new RandomParameterDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomPar.add(Integer.parseInt(parser1[i]));
								rpd.randomPar.add(Integer.parseInt(parser1[i]));
							}
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group") && parser2.length > 1){
								String[] parser3 = parser2[1].split("#");
								for(int i = 0; 2*i+1 < parser3.length; i++){
									rpd.group.add(parser3[2*i+1]);
								}
							}
							else if(parser2[0].equals("		name") && parser2.length > 1){
								rpd.name = parser2[1];
							}		
							else if(parser2[0].equals("		type") && parser2.length > 1){
								rpd.type = parser2[1];
							}
						}
						randomParDes.add(rpd);
					}
					else if(parser[0].equals("	RandomVariable")){
						RandomVariableDescription rvd = new RandomVariableDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomVar.add(Integer.parseInt(parser1[i]));
								rvd.randomVar.add(Integer.parseInt(parser1[i]));
							}
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group") && parser2.length > 1){
								String[] parser3 = parser2[1].split("#");
								for(int i = 0; 2*i+1 < parser3.length; i++){
									rvd.group.add(parser3[2*i+1]);
								}
							}
							else if(parser2[0].equals("		name") && parser2.length > 1){
								rvd.name = parser2[1];
							}	
							else if(parser2[0].equals("		type") && parser2.length > 1){
								rvd.type = parser2[1];
							}
						}
						randomVarDes.add(rvd);
					}
					else if(parser[0].equals("	RandomPort") && parser.length > 1){
						RandomPortDescription rpd = new RandomPortDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomPort.add(Integer.parseInt(parser1[i]));
								rpd.randomPort.add(Integer.parseInt(parser1[i]));
							}
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rpd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		name")){
								if(parser2.length > 1)
									rpd.name = parser2[1];
							}
							else if(parser2[0].equals("		type")){
								if(parser2.length > 1)
									rpd.type = parser2[1];
							}
							else if(parser2[0].equals("		isExport")){
								if(parser2.length > 1)
									rpd.isExport = Boolean.parseBoolean(parser2[1]);
							}
							else if(parser2[0].equals("		variable")){
								RandomPortVariable rpv = new RandomPortVariable();
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split(",");
									for(int i = 0; i < parser3.length; i++)
										rpv.randomPortVar.add(Integer.parseInt(parser3[i]));
								}
								while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			name")){
										if(parser3.length > 1)
											rpv.varName = parser3[1];
									}
									else if(parser3[0].equals("			group")){
										if(parser3.length > 1){
											String[] parser4 = parser3[1].split("#");
											for(int i = 0; 2*i+1 < parser4.length; i++)
												rpv.group.add(parser4[2*i+1]);
										}
									}
									
								}
								rpd.var.add(rpv);
							}
							
						
						}
						randomPortDes.add(rpd);
					}
					else if(parser[0].equals("	RandomState")){
						RandomStateDescription rsd = new RandomStateDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomState.add(Integer.parseInt(parser1[i]));
								rsd.randomState.add(Integer.parseInt(parser1[i]));
							}
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rsd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		name")){
								if(parser2.length > 1)
									rsd.name = parser2[1];
							}				
						}
						randomStateDes.add(rsd);
					}
					else if(parser[0].equals("	RandomTransition")){
						RandomTransitionDescription rtd = new RandomTransitionDescription();
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++){
								randomTransition.add(Integer.parseInt(parser1[i]));
								rtd.randomTransition.add(Integer.parseInt(parser1[i]));
							}
						}
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rtd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		port")){
								if(parser2.length > 1)
									rtd.port = parser2[1];
							}
							else if(parser2[0].equals("		sourceState")){
								if(parser2.length > 1)
									rtd.sourceState = parser2[1];
							}
							else if(parser2[0].equals("		targetState")){
								if(parser2.length > 1)
									rtd.targetState = parser2[1];
							}
							else if(parser2[0].equals("		guard")){
								RandomGuardDescription rgd = new RandomGuardDescription();
								if((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			type"))
										if(parser3.length > 1)
											if(parser3[1].equals("UnaryExpression")){
												rgd = new UnaryExpression();
												rgd.Type = "UnaryExpression";
											}
											else if(parser3[1].equals("BinaryExpression")){
												rgd = new BinaryExpression();
												rgd.Type = "BinaryExpression";
											}
											
								}
								if(rgd instanceof UnaryExpression)
									while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
										String[] parser3 = sCurrentLine.split(":");
										if(parser3[0].equals("			operand")){
											if(parser3.length > 1)
												((UnaryExpression)rgd).oprandName = parser3[1];
										}
										else if(parser3[0].equals("			operator")){
											if(parser3.length > 1)
												if(parser3[1].equals("!")){
													((UnaryExpression)rgd).op = UnaryOperator.LOGICAL_NOT;
												}

											
										}
											
									}
								else if(rgd instanceof BinaryExpression){
									while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
										String[] parser3 = sCurrentLine.split(":");
										if(parser3[0].equals("			leftOperand")){
											if(parser3.length > 1)
												((BinaryExpression)rgd).left = parser3[1];	
										}
										else if(parser3[0].equals("			rightOperand")){
											if(parser3.length > 1)
												((BinaryExpression)rgd).right = parser3[1];	
										}
										else if(parser3[0].equals("			operator")){
											if(parser3.length > 1){
												if(parser3[1].equals("=="))
													((BinaryExpression)rgd).op = BinaryOperator.EQUALITY;
												else if(parser3[1].equals("!="))
													((BinaryExpression)rgd).op = BinaryOperator.INEQUALITY;
												else if(parser3[1].equals(">"))
													((BinaryExpression)rgd).op = BinaryOperator.GREATER_THAN;
												else if(parser3[1].equals(">="))
													((BinaryExpression)rgd).op = BinaryOperator.GREATER_THAN_OR_EQUAL;
												else if(parser3[1].equals("<"))
													((BinaryExpression)rgd).op = BinaryOperator.LESS_THAN;
												else if(parser3[1].equals("<="))
													((BinaryExpression)rgd).op = BinaryOperator.LESS_THAN_OR_EQUAL;
													
											}
										}
									}
								}
								if((rgd instanceof UnaryExpression) || (rgd instanceof BinaryExpression))
										rtd.guard = rgd;
							}
							else if(parser2[0].equals("		action")){
								RandomActionDescription rad = new RandomActionDescription();
								if((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			type")){
										if(parser3.length > 1)
											if(parser3[1].equals("AssignmentAction")){
												rad = new AssignmentAction();
												rad.Type = "AssignmentAction";
												
											}
									}
								}
								if(parser2.length > 1){
									String[] parser4 = parser2[1].split(",");
									for(int i = 0; i < parser4.length; i++){
										rad.random.add(Integer.parseInt(parser4[i]));
									}
								}
								if(rad instanceof AssignmentAction){
									while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
										String[] parser3 = sCurrentLine.split(":");
										if(parser3[0].equals("			group")){
											if(parser3.length > 1){
												String[] parser4 = parser3[1].split("#");
												for(int i = 0; 2*i+1 < parser4.length; i++)
													rad.group.add(parser4[2*i+1]);
											}
										}
										else if(parser3[0].equals("			target")){
											if(parser3.length > 1){
												((AssignmentAction)rad).target = parser3[1];
											}
										}
										else if(parser3[0].equals("			value")){
											if(parser3.length > 1){
												((AssignmentAction)rad).value = parser3[1];	
											}
										}
									}
								}
						
								rtd.action.add(rad);
							}
						}
						randomTransDes.add(rtd);
					}
					else if(parser[0].equals("	RandomInitialState")){
						if(parser.length > 1)
							initState = parser[1];
					}
					else if(parser[0].equals("	RandomInitialTransition")){
						RandomInitialTransitionDescription ritd = new RandomInitialTransitionDescription();
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		action")){
								isRandomInitAct = true;
								RandomActionDescription rad = new RandomActionDescription();
								if((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
									String[] parser3 = sCurrentLine.split(":");
									if(parser3[0].equals("			type")){
										if(parser3.length > 1)
											if(parser3[1].equals("AssignmentAction")){
												rad = new AssignmentAction();
												rad.Type = "AssignmentAction";
												
											}
									}
								}
								if(parser2.length > 1){
									String[] parser4 = parser2[1].split(",");
									for(int i = 0; i < parser4.length; i++){
										rad.random.add(Integer.parseInt(parser4[i]));
									}
								}
								if(rad instanceof AssignmentAction){
									while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("		end")){
										String[] parser3 = sCurrentLine.split(":");
										if(parser3[0].equals("			group")){
											if(parser3.length > 1){
												String[] parser4 = parser3[1].split("#");
												for(int i = 0; 2*i+1 < parser4.length; i++)
													rad.group.add(parser4[2*i+1]);
											}
										}
										else if(parser3[0].equals("			target")){
											if(parser3.length > 1){
												((AssignmentAction)rad).target = parser3[1];
											}
										}
										else if(parser3[0].equals("			value")){
											if(parser3.length > 1){
												((AssignmentAction)rad).value = parser3[1];	
											}
										}
									}
								}
						
								ritd.action.add(rad);
							}
						}
						randomInitDes = ritd;
					}
				}
			}
		}
		
		r.close();
		
	}
	
	public void createAtomRandomPart(AtomType at,
			AtomType rat, Map<String, PortType> ptl,Map<String, Integer> groupl,
			Map<String, Object> parOrVar, List<insPort> pl, List<String> sl,
			List<Action> rinitialAction, List<ExecutorTransition> tl){
		for(RandomParameterDescription rpd: randomParDes){
			if(rpd.name == null)
				continue;
			if(rpd.randomPar.isEmpty()){
				if(rpd.type != null)
					createRandomParameter(rpd.name, rpd.group, 0, groupl, rpd.type, rat, parOrVar);
			}
			else if(at!= null)
				createRandomParameter(rpd.name, rpd.group, 0, groupl, ((OpaqueElement)at.getDataParameter().get(rpd.randomPar.get(0)).getType()).getBody(), rat, parOrVar);
		}
		for(RandomVariableDescription rvd: randomVarDes){
			if(rvd.name == null)
				continue;
			if(rvd.randomVar.isEmpty()){
				if(rvd.type != null)
					createRandomVariable(rvd.name, rvd.group, 0, groupl, rvd.type, rat, parOrVar);
			}
			else if(at != null)
				createRandomVariable(rvd.name, rvd.group, 0, groupl, ((OpaqueElement)at.getVariable().get(rvd.randomVar.get(0)).getType()).getBody(), rat, parOrVar);
		}
		for(RandomPortDescription rpd: randomPortDes){
			if(rpd.name == null)
				continue;
			if(rpd.randomPort.isEmpty()){
				createRandomPort(rpd, 0, groupl, null, ptl, pl, parOrVar);
			}
			else if(at != null)
				createRandomPort(rpd, 0, groupl, at.getPortDefinition().get(rpd.randomPort.get(0)), ptl, pl, parOrVar);
		}
		for(RandomStateDescription rsd: randomStateDes){
			if(rsd.name == null)
				continue;
			
			createRandomState(rsd.name, rsd.group, 0, groupl, sl);
		}
		if(randomInitDes != null){
			Action ia = null;
			if(at != null){
				Action initialAction = ((PetriNet)at.getBehavior()).getInitialization();
				if(initialAction instanceof CompositeAction){
					CompositeAction ca = (CompositeAction) initialAction;
					for(int i = 0; i < ca.getContent().size(); i++){
						boolean isContain = false;
						for(RandomActionDescription rad: randomInitDes.action){
							if(rad.random.contains(i)){
								isContain = true;
								break;
							}
						}
						if(!isContain){
							Action act = ca.getContent().get(i);
							((CompositeAction)rinitialAction.get(0)).getContent().add(BIPTransform.createAction(act, parOrVar));
						}
							
					}
				}
				ia = ((PetriNet)at.getBehavior()).getInitialization();	
			}
			for(RandomActionDescription rad: randomInitDes.action){
				if(!rad.random.isEmpty()){
					if(ia instanceof ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction && rad.random.contains(0))
						addRandomAction(rad, 0, groupl, (ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction)ia, (CompositeAction)rinitialAction.get(0), parOrVar);
					else if(ia instanceof CompositeAction)
						addRandomAction(rad, 0, groupl, ((CompositeAction)ia).getContent().get(rad.random.get(0)), (CompositeAction)rinitialAction.get(0), parOrVar);
				}
				else
					addRandomAction(rad, 0, groupl, null, (CompositeAction)rinitialAction.get(0), parOrVar);
			}
				
				
		}
		

		for(RandomTransitionDescription rtd: randomTransDes){
			if(!rtd.randomTransition.isEmpty())
				createRandomTransition(rtd, 0, groupl, ((PetriNet)at.getBehavior()).getTransition().get(rtd.randomTransition.get(0)), tl, parOrVar);
			else
				createRandomTransition(rtd, 0, groupl, null, tl, parOrVar);
		}
	}
	
	void createRandomParameter(String name, List<String> group, int position, Map<String, Integer> groupl, String parType, AtomType rat, Map<String, Object> parOrVar){
		if(position == group.size()){
			DataParameter rpar = BIPTransform.addAtomDataParameter(rat, parType, name);
			rat.getDataParameter().add(rpar);
			parOrVar.put(name, rpar);
		}
		else{
			for(Integer i = 0; i < groupl.get(group.get(position)); i++) {
				
				createRandomParameter(name.replaceAll("#"+group.get(position)+"#", i.toString()), group, position+1, groupl, parType, rat, parOrVar);
			}
		}
	}
	
	void createRandomVariable(String name, List<String> group, int position, Map<String, Integer> groupl, String varType, AtomType rat, Map<String, Object> parOrVar){
		if(position == group.size()){
			Variable rvar = BIPTransform.createVariable(name, varType);
			rat.getVariable().add(rvar);
			parOrVar.put(name, rvar);
			//System.out.println(parOrVar.containsKey("server0"));
		}
		else{
			for(Integer i = 0; i < groupl.get(group.get(position)); i++) {
				createRandomVariable(name.replaceAll("#"+group.get(position)+"#", i.toString()), group, position+1, groupl, varType, rat, parOrVar);
			}
		}
	}
	void createRandomState(String name, List<String> group, int position, Map<String, Integer> groupl, List<String> sl){

		if(position == group.size()){
			sl.add(name);
		}
		else{
			for(Integer i = 0; i < groupl.get(group.get(position)); i++) {
				createRandomState(name.replaceAll("#"+group.get(position)+"#", i.toString()), group, position+1, groupl, sl);
			}
		}
	}
	
	/*void createRandomPortType(Map<String, PortType> ptl, RandomPortDescription rpd, String name, Map<String, Object> parOrVar){
		if(!ptl.containsKey(name)){
			List<String> ptParType = new ArrayList<String>();
			List<String> ptParName = new ArrayList<String>();
			for(DataParameter par: port.getType().getDataParameter()){
				ptParType.add(((OpaqueElement)par.getType()).getBody());
				ptParName.add(par.getName());
			}
			PortType portType = BIPTransform.createPortType(portTypeName, ptParType,ptParName);
			m.getBipType().add(portType);
			ptl.put(name, portType);
		}
	}*/
	void addRandomPortVariable(RandomPortVariable rpv, int position, List<Variable> exposedVar, Map<String, Integer> groupl, Map<String, Object> parOrVar){
		if(rpv.group.size() == position){
			exposedVar.add((Variable)parOrVar.get(rpv.varName));
		}
		else{
			for(Integer i = 0; i < groupl.get(rpv.group.get(position)); i++){
				RandomPortVariable rrpv = new RandomPortVariable(rpv);
				if(rrpv.varName != null)
					rrpv.varName = rrpv.varName.replaceAll("#"+rpv.group.get(position)+"#", i.toString());
				addRandomPortVariable(rrpv, position+1, exposedVar, groupl, parOrVar);
			}
		}
	}
	void createRandomPort(RandomPortDescription rpd, int position, Map<String, Integer> groupl, PortDefinition port, Map<String, PortType> ptl, List<insPort> pl, Map<String, Object> parOrVar){
		if(position == rpd.group.size()){
			// can only change the name of port and exposed variables,  cannot change the port type
			if(port != null){
				int isExport = 1;
				if(rpd.isExport != null){
					if(! rpd.isExport)
						isExport = 0;
				}
				else{
					if(port.getExposedVariable() == null){
						isExport = 0;
					}
				}
				
				List<Variable> exposedVar = new ArrayList<Variable>();
				int i = 0;
				for(Variable var: port.getExposedVariable()){
					boolean isContain = false;
					for(RandomPortVariable rpv: rpd.var){
						if(rpv.randomPortVar.contains(i)){
							if(rpv.randomPortVar.get(0) == i){
								addRandomPortVariable(rpv, 0, exposedVar, groupl, parOrVar);
								isContain = true;
								break;
							}
							else{
								isContain = true;
								break;
							}
						}
							
					}
					i++;
					if(!isContain)
						exposedVar.add((Variable)parOrVar.get(var.getName()));
				}
				insPort rport;
				if(rpd.type != null)
					rport = new insPort(isExport, rpd.name, ptl.get(rpd.type), exposedVar);
				else
					rport = new insPort(isExport, rpd.name, ptl.get(port.getType().getName()), exposedVar);
				pl.add(rport);
			}
			else{
				int isExport = 1;
				if(rpd.isExport != null){
					if(! rpd.isExport)
						isExport = 0;
				}
				
				List<Variable> exposedVar = new ArrayList<Variable>();
				for(RandomPortVariable rpv: rpd.var){
					addRandomPortVariable(rpv, 0, exposedVar, groupl, parOrVar);
				}
				insPort rport = new insPort(isExport, rpd.name, ptl.get(rpd.type), exposedVar);
				pl.add(rport);
			}

		}
		else{
			for(Integer i = 0; i < groupl.get(rpd.group.get(position)); i++){
				RandomPortDescription rrpd = new RandomPortDescription(rpd);
				rrpd.name = rrpd.name.replaceAll("#"+rpd.group.get(position)+"#", i.toString());
				for(RandomPortVariable rpp: rrpd.var){
					if(rpp.varName == null)
						continue;
					rpp.varName = rpp.varName.replaceAll("#"+rpd.group.get(position)+"#", i.toString());
				}
				createRandomPort(rrpd, position+1, groupl, port, ptl, pl, parOrVar);
			}
		}
	}
	static void addRandomAction(RandomActionDescription rad, int position, Map<String, Integer> groupl, Action action, CompositeAction rca, Map<String, Object> parOrVar){
		if(rad.group.size() == position){
			if(rad.Type != null && rad.Type.equals("AssignmentAction")){
				ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction rassignAct = ActionsFactory.eINSTANCE.createAssignmentAction();
				ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction assignAct;
				if(action != null)
					assignAct = (ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction)action;
				else assignAct = null;
				if(((AssignmentAction)rad).target != null){
					if(parOrVar.get(((AssignmentAction)rad).target) instanceof Variable)
						rassignAct.setAssignedTarget(BIPTransform.getExpression((Variable)parOrVar.get(((AssignmentAction)rad).target)));
					else if(parOrVar.get(((AssignmentAction)rad).target) instanceof DataParameter){
						rassignAct.setAssignedTarget(BIPTransform.getExpression((DataParameter)parOrVar.get(((AssignmentAction)rad).target)));
					}
					else if(parOrVar.get(((AssignmentAction)rad).target) instanceof RequiredDataParameterReference){
						RequiredDataParameterReference rdpr = (RequiredDataParameterReference)parOrVar.get(((AssignmentAction)rad).target);
						rassignAct.setAssignedTarget(BIPTransform.getPortDataParameterReference(rdpr.getPortReference().getTarget(), rdpr.getTargetParameter()));
					}
				}
				else if(assignAct != null){
					DataReference dataRef = assignAct.getAssignedTarget();
					if(dataRef instanceof VariableReference)
					{
						VariableReference varRef = (VariableReference) dataRef;
						rassignAct.setAssignedTarget((VariableReference)BIPTransform.getExpression(varRef, parOrVar));
					}
					else if(dataRef instanceof RequiredDataParameterReference){
						RequiredDataParameterReference rdpr = (RequiredDataParameterReference) dataRef;
						rassignAct.setAssignedTarget((RequiredDataParameterReference)BIPTransform.getExpression(rdpr, parOrVar));
					}
					else if(dataRef instanceof ArrayNavigationExpression)
					{
						ArrayNavigationExpression arrayNavExp = (ArrayNavigationExpression) dataRef;
						rassignAct.setAssignedTarget((ArrayNavigationExpression)BIPTransform.getExpression(arrayNavExp, parOrVar));
					}
				}
				if(((AssignmentAction)rad).value != null){
					
					if(parOrVar.get(((AssignmentAction)rad).value) instanceof Variable)
						rassignAct.setAssignedValue(BIPTransform.getExpression((Variable)parOrVar.get(((AssignmentAction)rad).value)));
					else if(parOrVar.get(((AssignmentAction)rad).value) instanceof DataParameter){
						rassignAct.setAssignedValue(BIPTransform.getExpression((DataParameter)parOrVar.get(((AssignmentAction)rad).value)));
					}
					else if(parOrVar.get(((AssignmentAction)rad).value) instanceof RequiredDataParameterReference){
						RequiredDataParameterReference rdpr = (RequiredDataParameterReference)parOrVar.get(((AssignmentAction)rad).value);
						rassignAct.setAssignedValue(BIPTransform.getPortDataParameterReference(rdpr.getPortReference().getTarget(), rdpr.getTargetParameter()));
					}
					else if(((AssignmentAction)rad).value.equals("true") || ((AssignmentAction)rad).value.equals("false")){
						BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
						bl.setBValue(Boolean.parseBoolean(((AssignmentAction)rad).value));
						rassignAct.setAssignedValue(bl);
					}
					else {
						IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
						il.setIValue(Integer.parseInt(((AssignmentAction)rad).value));
						rassignAct.setAssignedValue(il);
					}
				}
				else if(assignAct != null){
					rassignAct.setAssignedValue(BIPTransform.getExpression(assignAct.getAssignedValue(), parOrVar));
				}
				rca.getContent().add(rassignAct);
			}
		}
		else{
			for(Integer i = 0; i < groupl.get(rad.group.get(position)); i++){
				RandomActionDescription rrad = new RandomActionDescription();
				if(rad instanceof AssignmentAction)
					rrad = new AssignmentAction((AssignmentAction)rad);
				else{
					
				}
				
				if(rrad instanceof AssignmentAction){
					if(((AssignmentAction)rrad).target != null)
						((AssignmentAction)rrad).target = ((AssignmentAction)rrad).target.replaceAll("#"+rrad.group.get(position)+"#", i.toString());
					if(((AssignmentAction)rrad).value != null)
						((AssignmentAction)rrad).value = ((AssignmentAction)rrad).value.replaceAll("#"+rrad.group.get(position)+"#", i.toString());
				}
				addRandomAction(rrad, position+1, groupl, action, rca, parOrVar);
			}
		}
	}
	void createRandomTransition(RandomTransitionDescription rtd, int position, Map<String, Integer> groupl, Transition t, List<ExecutorTransition> tl, Map<String, Object> parOrVar){
		
		if(position == rtd.group.size()){
			if(t != null){
				if(rtd.port == null)
					rtd.port = ((PortDefinitionReference)t.getTrigger()).getTarget().getName();
				if(rtd.sourceState == null)
					rtd.sourceState = t.getOrigin().get(0).getName();
				if(rtd.targetState == null)
					rtd.targetState = t.getDestination().get(0).getName();
				Expression exp = null;
				if(rtd.guard == null)
					exp = t.getGuard();
				else{
					
					if(rtd.guard.Type.equals("UnaryExpression")){
						ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression unaryExp = (ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression)t.getGuard();
						ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression runaryExp = ExpressionsFactory.eINSTANCE.createUnaryExpression();
						if(((UnaryExpression)rtd.guard).op == null)
							runaryExp.setOperator(unaryExp.getOperator());
						else
							runaryExp.setOperator(((UnaryExpression)rtd.guard).op);
						if(((UnaryExpression)rtd.guard).oprandName != null){
							Object ob = parOrVar.get(((UnaryExpression)rtd.guard).oprandName);
							if(ob instanceof Variable)
								runaryExp.setOperand((VariableReference)BIPTransform.getExpression((Variable)ob));
							else if(ob instanceof DataParameter)
								runaryExp.setOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob));
							else if(((UnaryExpression)rtd.guard).oprandName.equals("true") || ((UnaryExpression)rtd.guard).oprandName.equals("false")){
								BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
								bl.setBValue(Boolean.parseBoolean(((UnaryExpression)rtd.guard).oprandName));
								runaryExp.setOperand(bl);
							}
							else {
								IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
								il.setIValue(Integer.parseInt(((UnaryExpression)rtd.guard).oprandName));
								runaryExp.setOperand(il);
							}
						}
						else{
							runaryExp.setOperand(BIPTransform.getExpression(unaryExp.getOperand(), parOrVar));
						}
						exp = runaryExp;
					}
					else if(rtd.guard.Type.equals("BinaryExpresion")){
						ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression binaryExp = (ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression)t.getGuard();
						ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression rbinaryExp = ExpressionsFactory.eINSTANCE.createBinaryExpression();
						if(((BinaryExpression)rtd.guard).op == null)
							rbinaryExp.setOperator(binaryExp.getOperator());
						else
							rbinaryExp.setOperator(((BinaryExpression)rtd.guard).op);
						if(((BinaryExpression)rtd.guard).left != null){
							Object ob1 = parOrVar.get(((BinaryExpression)rtd.guard).left);
							if(ob1 instanceof Variable)
								rbinaryExp.setLeftOperand((VariableReference)BIPTransform.getExpression((Variable)ob1));
							else if(ob1 instanceof DataParameter)
								rbinaryExp.setLeftOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob1));
							else if(((BinaryExpression)rtd.guard).left.equals("true") || ((BinaryExpression)rtd.guard).left.equals("false")){
								BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
								bl.setBValue(Boolean.parseBoolean(((BinaryExpression)rtd.guard).left));
								rbinaryExp.setLeftOperand(bl);
							}
							else {
								IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
								il.setIValue(Integer.parseInt(((BinaryExpression)rtd.guard).left));
								rbinaryExp.setLeftOperand(il);
							}
						}
						else{
							rbinaryExp.setLeftOperand(BIPTransform.getExpression(binaryExp.getLeftOperand(), parOrVar));
						}
						if(((BinaryExpression)rtd.guard).right != null){
							Object ob2 = parOrVar.get(((BinaryExpression)rtd.guard).right);
							if(ob2 instanceof Variable)
								rbinaryExp.setRightOperand((VariableReference)BIPTransform.getExpression((Variable)ob2));
							else if(ob2 instanceof DataParameter)
								rbinaryExp.setRightOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob2));
							else if(((BinaryExpression)rtd.guard).right.equals("true") || ((BinaryExpression)rtd.guard).right.equals("false")){
								BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
								bl.setBValue(Boolean.parseBoolean(((BinaryExpression)rtd.guard).right));
								rbinaryExp.setLeftOperand(bl);
							}
							else {
								IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
								il.setIValue(Integer.parseInt(((BinaryExpression)rtd.guard).right));
								rbinaryExp.setLeftOperand(il);
							}
						}
						else{
							rbinaryExp.setRightOperand(BIPTransform.getExpression(binaryExp.getRightOperand(), parOrVar));
						}
						exp = rbinaryExp;
					}
				}
				Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
				if(t.getAction() != null){
					Action taction = t.getAction();
					if(taction instanceof CompositeAction){
						CompositeAction ca = (CompositeAction) taction;
						for(int i = 0; i < ca.getContent().size(); i++){
							boolean isContain = false;
							for(RandomActionDescription rad: rtd.action){
								if(rad.random.contains(i)){
									isContain = true;
									break;
								}
							}
							if(!isContain){
								Action act = ca.getContent().get(i);
								((CompositeAction)rAction).getContent().add(BIPTransform.createAction(act, parOrVar));
							}
								
						}
					}
					
				}
				for(RandomActionDescription rad: rtd.action){
					if(!rad.random.isEmpty()){
						if(t.getAction() instanceof ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction && rad.random.contains(0))
							addRandomAction(rad, 0, groupl, (ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction)t.getAction(), (CompositeAction)rAction, parOrVar);
						else if(t.getAction() instanceof CompositeAction)
							addRandomAction(rad, 0, groupl, ((CompositeAction)t.getAction()).getContent().get(rad.random.get(0)), (CompositeAction)rAction, parOrVar);
					}
					else
						addRandomAction(rad, 0, groupl, null, (CompositeAction)rAction, parOrVar);	
				}
				tl.add(new ExecutorTransition(rtd.port, rtd.sourceState, rtd.targetState, exp, rAction));	
			}
			else{
				Expression exp = null;
				if(rtd.guard.Type.equals("UnaryExpression")){
					ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression runaryExp = ExpressionsFactory.eINSTANCE.createUnaryExpression();
					if(((UnaryExpression)rtd.guard).op != null)
						runaryExp.setOperator(((UnaryExpression)rtd.guard).op);
					if(((UnaryExpression)rtd.guard).oprandName != null){
						Object ob = parOrVar.get(((UnaryExpression)rtd.guard).oprandName);
						if(ob instanceof Variable)
							runaryExp.setOperand((VariableReference)BIPTransform.getExpression((Variable)ob));
						else if(ob instanceof DataParameter)
							runaryExp.setOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob));
					}
					exp = runaryExp;
				}
				else if(rtd.guard.Type.equals("BinaryExpresion")){
					ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression rbinaryExp = ExpressionsFactory.eINSTANCE.createBinaryExpression();
					if(((BinaryExpression)rtd.guard).op != null)
						rbinaryExp.setOperator(((BinaryExpression)rtd.guard).op);
					if(((BinaryExpression)rtd.guard).left != null){
						Object ob1 = parOrVar.get(((BinaryExpression)rtd.guard).left);
						if(ob1 instanceof Variable)
							rbinaryExp.setLeftOperand((VariableReference)BIPTransform.getExpression((Variable)ob1));
						else if(ob1 instanceof DataParameter)
							rbinaryExp.setLeftOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob1));
					}
					if(((BinaryExpression)rtd.guard).right != null){
						Object ob2 = parOrVar.get(((BinaryExpression)rtd.guard).right);
						if(ob2 instanceof Variable)
							rbinaryExp.setRightOperand((VariableReference)BIPTransform.getExpression((Variable)ob2));
						else if(ob2 instanceof DataParameter)
							rbinaryExp.setRightOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob2));
					}
					exp = rbinaryExp;
				}
				Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
				for(RandomActionDescription rad: rtd.action){
						addRandomAction(rad, 0, groupl, null, (CompositeAction)rAction, parOrVar);
				}
				tl.add(new ExecutorTransition(rtd.port, rtd.sourceState, rtd.targetState, exp, rAction));
			}

		}
		else{
			for(Integer i = 0; i < groupl.get(rtd.group.get(position)); i++){
				RandomTransitionDescription rrtd = new RandomTransitionDescription(rtd);
				if(rrtd.port != null)
					rrtd.port = rrtd.port.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
				if(rrtd.sourceState != null)
					rrtd.sourceState = rrtd.sourceState.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
				if(rrtd.targetState != null)
					rrtd.targetState = rrtd.targetState.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
				if(rrtd.guard != null){
					//RandomGuardDescription rgd =  rrtd.guard;
					if(rrtd.guard instanceof UnaryExpression){
						if(((UnaryExpression)rrtd.guard).oprandName != null)
							((UnaryExpression)rrtd.guard).oprandName = ((UnaryExpression)rrtd.guard).oprandName.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
					}
					else if(rrtd.guard instanceof BinaryExpression){
						if(((BinaryExpression)rrtd.guard).left != null)
							((BinaryExpression)rrtd.guard).left = ((BinaryExpression)rrtd.guard).left.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
						if(((BinaryExpression)rrtd.guard).right != null)
							((BinaryExpression)rrtd.guard).right = ((BinaryExpression)rrtd.guard).right.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
					}
				}
				for(RandomActionDescription rad: rrtd.action){
					if(rad instanceof AssignmentAction){
						if(((AssignmentAction)rad).target != null)
							((AssignmentAction)rad).target = ((AssignmentAction)rad).target.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
						if(((AssignmentAction)rad).value != null)
							((AssignmentAction)rad).value = ((AssignmentAction)rad).value.replaceAll("#"+rtd.group.get(position)+"#", i.toString());
					}
				}
				createRandomTransition(rrtd, position+1, groupl, t, tl, parOrVar);
				
				
			}
		}
	}
}
 