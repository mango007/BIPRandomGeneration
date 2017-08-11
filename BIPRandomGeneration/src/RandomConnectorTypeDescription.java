import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.VariableReference;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.DataParameter;
import ujf.verimag.bip.Core.Behaviors.Expression;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.Interaction;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.PortParameter;
import ujf.verimag.bip.Core.Interactions.PortParameterReference;


class RandomPortParameterDescription{
	String name = null;
	String type = null;
	List<String> group = new ArrayList<String>();
	RandomPortParameterDescription(){}
	RandomPortParameterDescription(RandomPortParameterDescription rppd){
		this.name = rppd.name;
		this.type = rppd.type;
		for(String s: rppd.group)
			this.group.add(s);
	}
	
}

class RandomInteractionDescription{
	List<String> group = new ArrayList<String>();
	List<String> triggers = new ArrayList<String>();
	RandomGuardDescription guard = null;
	List<RandomActionDescription> upAction = new ArrayList<RandomActionDescription>();
	List<RandomActionDescription> downAction = new ArrayList<RandomActionDescription>();
	RandomInteractionDescription(){}
	RandomInteractionDescription(RandomInteractionDescription rid){
		if(rid.guard != null && rid.guard.Type.equals("UnaryExpression")){
			UnaryExpression ue = new UnaryExpression();
			ue.Type = "UnaryExpression";
			ue.oprandName = ((UnaryExpression)rid.guard).oprandName;
			ue.op = ((UnaryExpression)rid.guard).op;
			this.guard = ue;
		}
		else if(rid.guard != null && rid.guard.Type.equals("BinaryExpression")){
			BinaryExpression be = new BinaryExpression();
			be.Type = "BinaryExpression";
			be.left = ((BinaryExpression)rid.guard).left;
			be.right = ((BinaryExpression)rid.guard).right;
			be.op = ((BinaryExpression)rid.guard).op;
			this.guard = be;
		}	
		for(String s: rid.group)
			this.group.add(s);
		for(String s: rid.triggers)
			this.triggers.add(s);
		for(RandomActionDescription rad: rid.upAction){
			if(rad.Type.equals("AssignmentAction")){
				AssignmentAction aa = new AssignmentAction();
				aa.Type = "AssignmentAction";
				aa.target = ((AssignmentAction)rad).target;
				aa.value = ((AssignmentAction)rad).value;
				for(Integer i: rad.random)
					aa.random.add(i);
				for(String s: rad.group)
					aa.group.add(s);
				this.upAction.add(aa);
			}
		}
		for(RandomActionDescription rad: rid.downAction){
			if(rad.Type.equals("AssignmentAction")){
				AssignmentAction aa = new AssignmentAction();
				aa.Type = "AssignmentAction";
				aa.target = ((AssignmentAction)rad).target;
				aa.value = ((AssignmentAction)rad).value;
				for(Integer i: rad.random)
					aa.random.add(i);
				for(String s: rad.group)
					aa.group.add(s);
				this.downAction.add(aa);
			}
		}
		
	}
	
}
public class RandomConnectorTypeDescription {
	boolean isRandomType = false;
	
	List<RandomPortParameterDescription> rPortDes = new ArrayList<RandomPortParameterDescription>();
	List<RandomInteractionDescription> rIntDes = new ArrayList<RandomInteractionDescription>();
	List<Integer> triggers = new ArrayList<Integer>();
	
	RandomConnectorTypeDescription(String instuctionFile, String typeName) throws IOException{
		Reader r = new BufferedReader(new FileReader(instuctionFile));
		String sCurrentLine;
		while ((sCurrentLine = ((BufferedReader)r).readLine()) != null) {
			if(sCurrentLine.equals("Connector:"+typeName)){
				isRandomType = true;
				while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("end")){
					String[] parser = sCurrentLine.split(":");
					if(parser[0].equals("	RandomPort")){
						RandomPortParameterDescription rppd = new RandomPortParameterDescription();
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rppd.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		name")){
								if(parser2.length > 1)
									rppd.name = parser2[1];
							}
							else if(parser2[0].equals("		type")){
								if(parser2.length > 1)
									rppd.type = parser2[1];
							}
								
								
								
								
						}
						rPortDes.add(rppd);
					}
					else if(parser[0].equals("	Triggers")){
						if(parser.length > 1){
							String[] parser1 = parser[1].split(",");
							for(int i = 0; i < parser1.length; i++)
								triggers.add(Integer.parseInt(parser1[i]));
						}
					}
					else if(parser[0].equals("	Interaction")){
						RandomInteractionDescription rid = new RandomInteractionDescription();
						while((sCurrentLine = ((BufferedReader)r).readLine()) != null && !sCurrentLine.equals("	end")){
							String[] parser2 = sCurrentLine.split(":");
							if(parser2[0].equals("		group")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split("#");
									for(int i = 0; 2*i+1 < parser3.length; i++){
										rid.group.add(parser3[2*i+1]);
									}
								}
							}
							else if(parser2[0].equals("		triggers")){
								if(parser2.length > 1){
									String[] parser3 = parser2[1].split(",");
									for(int i = 0; i < parser3.length; i++){
										rid.triggers.add(parser3[i]);
									}
								}
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
								if((rgd instanceof UnaryExpression) || (rgd instanceof BinaryExpression)){
									
									rid.guard = rgd;
								}
							}
							else if(parser2[0].equals("		upAction")){
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
						
								rid.upAction.add(rad);
							}
							else if(parser2[0].equals("		downAction")){
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
						
								rid.downAction.add(rad);
							}
							
						}
						
						rIntDes.add(rid);
					}	
				}
			}
		}
		r.close();
	}

	void createConnectorRandomPart(ConnectorType rcont, Map<String, PortType> ptl,Map<String, Integer> groupl){
		
		Map <String, PortParameter> RefList = new HashMap <String, PortParameter>();
		Map<String, Object> parOrVar = new HashMap<String, Object>();
		
		List<Integer> counter = new ArrayList<Integer>();
		for(RandomPortParameterDescription rppd: rPortDes){	
			
			if(rppd.name == null)
				continue;
			int i = 1;
			for(String s: rppd.group)
				i = i*groupl.get(s);
			counter.add(i);
			if(rppd.type != null)
				addPortParameter(rppd, 0, groupl, ptl, rcont, RefList, parOrVar);
			
		}
		
		List<Integer> tri = new ArrayList<Integer>();
		for(Integer i: triggers){
			int start = 0;
			for(int j = 0; j < i; j++)
				start = start+counter.get(j);
			for(int j = start; j < start+counter.get(i); j++)
				tri.add(j);
		}
		BIPTransform.setConnectorTypeDefinition(rcont, tri);
		
		for(RandomInteractionDescription rid: rIntDes){
			createRandomInteraction(rid, 0, groupl, rcont, RefList, parOrVar);
		}
		
	}
	
	void addPortParameter(RandomPortParameterDescription rppd, int position , Map<String, Integer> groupl,  Map<String, PortType> ptl, ConnectorType rcont, Map <String, PortParameter> RefList, Map<String, Object> parOrVar){
		if(rppd.group.size() == position){
			PortParameter rpp = InteractionsFactory.eINSTANCE.createPortParameter();
			rpp.setType(ptl.get(rppd.type));
			rpp.setName(rppd.name);
			rcont.getPortParameter().add(rpp);
			RefList.put(rppd.name, rpp);
			for(int i = 0; i < ptl.get(rppd.type).getDataParameter().size(); i++){
				DataParameter dp = ptl.get(rppd.type).getDataParameter().get(i);
				RequiredDataParameterReference  rdpr = ExpressionsFactory.eINSTANCE.createRequiredDataParameterReference();
				PortParameterReference ppr = InteractionsFactory.eINSTANCE.createPortParameterReference();
				ppr.setTarget(rpp);
				rdpr.setPortReference(ppr);
				rdpr.setTargetParameter(dp);
				parOrVar.put(rppd.name+"."+dp.getName(), rdpr);
			}
		}
		else{
			for(Integer i = 0; i < groupl.get(rppd.group.get(position)); i++){
				RandomPortParameterDescription rrppd = new RandomPortParameterDescription(rppd);
				if(rrppd.name != null)
					rrppd.name = rrppd.name.replaceAll("#"+rrppd.group.get(position)+"#", i.toString());
				addPortParameter(rrppd, position+1, groupl, ptl, rcont, RefList, parOrVar);
			}
		}
	}
	void createRandomInteraction(RandomInteractionDescription rid, int position, Map<String, Integer> groupl, ConnectorType rcont, Map <String, PortParameter> RefList, Map<String, Object> parOrVar){
		if(rid.group.size() == position){
			InteractionSpecification ris = InteractionsFactory.eINSTANCE.createInteractionSpecification();
			Interaction ri = InteractionsFactory.eINSTANCE.createInteraction();
			for(String s: rid.triggers){
				PortParameterReference ppr = InteractionsFactory.eINSTANCE.createPortParameterReference();
				ppr.setTarget(RefList.get(s));
				ri.getPort().add(ppr);
			}
			ris.setInteraction(ri);
			
			if(rid.guard != null){
				Expression exp = null;
				if(rid.guard.Type.equals("UnaryExpression")){
					ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression runaryExp = ExpressionsFactory.eINSTANCE.createUnaryExpression();
					if(((UnaryExpression)rid.guard).op != null)
						runaryExp.setOperator(((UnaryExpression)rid.guard).op);
					if(((UnaryExpression)rid.guard).oprandName != null){
						Object ob = parOrVar.get(((UnaryExpression)rid.guard).oprandName);
						if(ob instanceof Variable)
							runaryExp.setOperand((VariableReference)BIPTransform.getExpression((Variable)ob));
						else if(ob instanceof DataParameter)
							runaryExp.setOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob));
						else if(ob instanceof RequiredDataParameterReference){
							RequiredDataParameterReference rdpr = (RequiredDataParameterReference)ob;
							runaryExp.setOperand(BIPTransform.getPortDataParameterReference(rdpr.getPortReference().getTarget(), rdpr.getTargetParameter()));
						}
						else if(((UnaryExpression)rid.guard).oprandName.equals("true") || ((UnaryExpression)rid.guard).oprandName.equals("false")){
							BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
							bl.setBValue(Boolean.parseBoolean(((UnaryExpression)rid.guard).oprandName));
							runaryExp.setOperand(bl);
						}
						else {
							IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
							il.setIValue(Integer.parseInt(((UnaryExpression)rid.guard).oprandName));
							runaryExp.setOperand(il);
						}
							
					}
					exp = runaryExp;
				}
				else if(rid.guard.Type.equals("BinaryExpresion")){
					ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression rbinaryExp = ExpressionsFactory.eINSTANCE.createBinaryExpression();
					if(((BinaryExpression)rid.guard).op != null)
						rbinaryExp.setOperator(((BinaryExpression)rid.guard).op);
					if(((BinaryExpression)rid.guard).left != null){
						Object ob1 = parOrVar.get(((BinaryExpression)rid.guard).left);
						if(ob1 instanceof Variable)
							rbinaryExp.setLeftOperand((VariableReference)BIPTransform.getExpression((Variable)ob1));
						else if(ob1 instanceof DataParameter)
							rbinaryExp.setLeftOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob1));
						else if(ob1 instanceof RequiredDataParameterReference){
							RequiredDataParameterReference rdpr = (RequiredDataParameterReference)ob1;
							rbinaryExp.setLeftOperand(BIPTransform.getPortDataParameterReference(rdpr.getPortReference().getTarget(), rdpr.getTargetParameter()));
						}
						else if(((BinaryExpression)rid.guard).left.equals("true") || ((BinaryExpression)rid.guard).left.equals("false")){
							BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
							bl.setBValue(Boolean.parseBoolean(((BinaryExpression)rid.guard).left));
							rbinaryExp.setLeftOperand(bl);
						}
						else {
							IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
							il.setIValue(Integer.parseInt(((BinaryExpression)rid.guard).left));
							rbinaryExp.setLeftOperand(il);
						}
					}
					if(((BinaryExpression)rid.guard).right != null){
						Object ob2 = parOrVar.get(((BinaryExpression)rid.guard).right);
						if(ob2 instanceof Variable)
							rbinaryExp.setRightOperand((VariableReference)BIPTransform.getExpression((Variable)ob2));
						else if(ob2 instanceof DataParameter)
							rbinaryExp.setRightOperand((DataParameterReference)BIPTransform.getExpression((DataParameter)ob2));
						else if(ob2 instanceof RequiredDataParameterReference){
							RequiredDataParameterReference rdpr = (RequiredDataParameterReference)ob2;
							rbinaryExp.setLeftOperand(BIPTransform.getPortDataParameterReference(rdpr.getPortReference().getTarget(), rdpr.getTargetParameter()));
						}
						else if(((BinaryExpression)rid.guard).right.equals("true") || ((BinaryExpression)rid.guard).right.equals("false")){
							BooleanLiteral bl = ExpressionsFactory.eINSTANCE.createBooleanLiteral();
							bl.setBValue(Boolean.parseBoolean(((BinaryExpression)rid.guard).right));
							rbinaryExp.setLeftOperand(bl);
						}
						else {
							IntegerLiteral il = ExpressionsFactory.eINSTANCE.createIntegerLiteral();
							il.setIValue(Integer.parseInt(((BinaryExpression)rid.guard).right));
							rbinaryExp.setLeftOperand(il);
						}
					}
					exp = rbinaryExp;
				}
				ris.setGuard(exp);
				
			}
			if(!rid.upAction.isEmpty()){
				Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
				for(RandomActionDescription rad: rid.upAction){
					RandomAtomDescription.addRandomAction(rad, 0, groupl, null, (CompositeAction)rAction, parOrVar);
				}
				ris.setUpAction(rAction);
			}
			if(!rid.downAction.isEmpty()){
				Action rAction = ActionsFactory.eINSTANCE.createCompositeAction();
				for(RandomActionDescription rad: rid.downAction){
					RandomAtomDescription.addRandomAction(rad, 0, groupl, null, (CompositeAction)rAction, parOrVar);
				}
				ris.setDownAction(rAction);
			}
			
			rcont.getInteractionSpecification().add(ris);		
		}
		else{
			for(Integer i = 0; i < groupl.get(rid.group.get(position)); i++){
				RandomInteractionDescription rrid = new RandomInteractionDescription(rid);
				for(int j = 0; j < rrid.triggers.size(); j++){
					rrid.triggers.set(j, rrid.triggers.get(j).replaceAll("#"+rrid.group.get(position)+"#", i.toString()));
				}
				if(rrid.guard != null){
					//RandomGuardDescription rgd =  rrtd.guard;
					if(rrid.guard instanceof UnaryExpression){
						if(((UnaryExpression)rrid.guard).oprandName != null)
							((UnaryExpression)rrid.guard).oprandName = ((UnaryExpression)rrid.guard).oprandName.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
					}
					else if(rrid.guard instanceof BinaryExpression){
						if(((BinaryExpression)rrid.guard).left != null)
							((BinaryExpression)rrid.guard).left = ((BinaryExpression)rrid.guard).left.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
						if(((BinaryExpression)rrid.guard).right != null)
							((BinaryExpression)rrid.guard).right = ((BinaryExpression)rrid.guard).right.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
					}
				}
				for(RandomActionDescription rad: rrid.upAction){
					if(rad instanceof AssignmentAction){
						if(((AssignmentAction)rad).target != null)
							((AssignmentAction)rad).target = ((AssignmentAction)rad).target.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
						if(((AssignmentAction)rad).value != null)
							((AssignmentAction)rad).value = ((AssignmentAction)rad).value.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
					}
				}
				
				for(RandomActionDescription rad: rrid.downAction){
					if(rad instanceof AssignmentAction){
						if(((AssignmentAction)rad).target != null)
							((AssignmentAction)rad).target = ((AssignmentAction)rad).target.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
						if(((AssignmentAction)rad).value != null)
							((AssignmentAction)rad).value = ((AssignmentAction)rad).value.replaceAll("#"+rrid.group.get(position)+"#", i.toString());
					}
				}
				
				createRandomInteraction(rrid, position+1, groupl, rcont, RefList, parOrVar);
				
			}
		}
	}
}
