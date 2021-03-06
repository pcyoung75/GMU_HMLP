/*
 * HML Core
 * Copyright (C) 2017 Cheol Young Park
 * 
 * This file is part of HML Core.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mebn_rm.MEBN.MTheory;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import mebn_rm.MEBN.CLD.CLD;
import mebn_rm.MEBN.CLD.Categorical;
import mebn_rm.MEBN.CLD.ConditionalGaussian;
import mebn_rm.MEBN.MFrag.MFrag;
import mebn_rm.MEBN.MNode.MCNode;
import mebn_rm.MEBN.MNode.MDNode;
import mebn_rm.MEBN.MNode.MIsANode;
import mebn_rm.MEBN.MNode.MNode;
import mebn_rm.MEBN.MTheory.OVariable;
import mebn_rm.RDB.RDB; 
import mebn_rm.util.StringUtil;
import util.ListMgr;
import util.SortableValueMap;
  
/**
 * MTheory is the class for a structure of MTheory.
 * <p>
 * 
 * @author      Cheol Young Park
 * @version     0.0.1
 * @since       1.5
 */

public class MTheory implements Comparable<MTheory> {
	static Logger logger = Logger.getLogger(MTheory.class);
	
    public SortableValueMap<MFrag, Double> mfrags = new SortableValueMap<MFrag, Double>();
    public ArrayList<String> entities = new ArrayList<String>();
    public String name;
    public RDB rdb = null;

    public MTheory(String n) {
        name = n;
    }

    public /* varargs */ void setMFrags(MFrag ... fs) {
        MFrag[] arrmFrag = fs;
        int n = arrmFrag.length;
        int n2 = 0;
        while (n2 < n) {
            MFrag f = arrmFrag[n2];
            f.mTheory = this;
            mfrags.put(f, 0.0);
            ++n2;
        }
    }

    public MFrag getMFrag(String s) {
        for (MFrag f : mfrags.keySet()) {
            if (!s.equalsIgnoreCase(f.name)) continue;
            return f;
        }
        return null;
    }

    public boolean removeMFrag(MFrag f) {
        mfrags.remove(f);
        return mfrags.isEmpty();
    }

    public MNode getMNode(String s) {
        String mFrag = StringUtil.This().getLeft(s);
        String mNode = StringUtil.This().getRight(s);
        MFrag f = getMFrag(mFrag);
        MNode childMNode = f.getMNode(mNode);
        return childMNode;
    }
 
    public void addParents(String c, List<String> ps) {
        String mFrag = StringUtil.This().getLeft(c);
        String mNode = StringUtil.This().getRight(c);
        String combParents = "";
        boolean bOtherMFrag = false;
          
        for (String p : ps) {
            String mFragP = StringUtil.This().getLeft(p); 
            if (!mFrag.equalsIgnoreCase(mFragP) && !bOtherMFrag) { 
                bOtherMFrag = true;
            } 
        }
        
        combParents = mNode;
        
        MFrag f = getMFrag(mFrag);
        MNode childMNode = f.getMNode(mNode);  
        
        if (bOtherMFrag) {
            String sql = "SELECT\r\n";
            String sqlFrom = "";
            
            sql = sql + f.getTableName() + "." + childMNode.getAttributeName() + " as " + mNode + ",\r\n";
            sqlFrom = sqlFrom + f.getTableName() + ", ";
            
            MFrag newMFrag = new MFrag(this, combParents);
            newMFrag.setTableName(f.getTableName());
            
            MNode newChild = null;
            if (childMNode.isContinuous()) {
                newChild = new MCNode(childMNode);
            } else if (childMNode.isDiscrete()) {
                newChild = new MDNode(childMNode);
            }
            newMFrag.setMNodes(newChild);
            
            List<String> keys = newMFrag.getRDBKeys();
            
            // Add Isa Context Nodes for the new MFrag from the current MFrag
            for (String key : keys) {
            	String originEntity = rdb.getOriginFromKey(newMFrag.table, key);
                OVariable ov = new OVariable(f.getTableName(), key, originEntity);
        		new MIsANode(newMFrag, ov);
            }

            // Add parent nodes
            List<MFrag> parentMFrags = new ArrayList<MFrag>();
            for (String p2 : ps) {
                String mFragP = new StringUtil().getLeft(p2);
                String mNodeP = new StringUtil().getRight(p2);

                MFrag fp = getMFrag(mFragP);
                MNode parentMNode = fp.getMNode(mNodeP);
                if (!parentMFrags.contains(fp)) {
                	parentMFrags.add(fp);
                }

                // create a joining sql for child and parent nodes
                if (!fp.isTimedMFrag()){
	                sql += fp.getTableName() + "." + parentMNode.getAttributeName() + " as " + mNodeP + ",\r\n";
//	                sqlFrom += fp.getTableName() + ", ";
                } else { // is a TimedMFrag 
                	sql += fp.getTableName() + "." + mNodeP + " as " + mNodeP + ",\r\n";
//	                sqlFrom += " ( " +  fp.joiningSQL + " ) " + fp.getTableName();  
//	                sqlFrom += ", ";
                }
                
                List<String> keys2 = fp.getRDBKeys();
                List<OVariable> listOV = new ArrayList<OVariable>(); 
                
                // Add Isa Context Nodes for the new MFrag from the parent MFrags
                for (String key2 : keys2) {
                    String originEntity = rdb.getOriginFromKey(fp.getTableName(), key2);
                    OVariable ov = new OVariable(fp.getTableName(), key2, originEntity);
                    new MIsANode(newMFrag, ov);
                    listOV.add(ov);
                }
                
                // Create a new parent input MNode from a current parent MNode
                // Both have different OVs
                MNode ip = null;
                if (parentMNode.isContinuous()) {
                	ip = new MCNode(fp, parentMNode, listOV);
                } else if (parentMNode.isDiscrete()) {
                	ip = new MDNode(fp, parentMNode, listOV);
                }
                
                // The new column name for the training csv data is set 
	            ip.columnName = ip.name;
	                
                newChild.setInputParents(ip);
            }
             
            sql = sql.substring(0, sql.length() - 3);
            sql = sql + "\r\nFROM\r\n";
            
            for (MFrag fp : parentMFrags){
            	if (f != fp) { 
            		// If the MFrag for the child node is already used, don't add it to the joining SQL.
//	                if (fp.joiningSQL == null){
	                if (f.joiningSQL == null){
		                sqlFrom += fp.getTableName() + ", ";
		            } 
            	}
            }
            
            sqlFrom = sqlFrom.substring(0, sqlFrom.length() - 2);
            sql = sql + (String)sqlFrom + "\r\n";
            
            // set a where clause
             
            newMFrag.joiningSQL = sql;
            
            // If there is no more node, then delete this MFrag 
            if (f.removeMNode(childMNode)) {
                removeMFrag(f);
            }
        } else { // If added parents are in the same MFrag
            for (String p3 : ps) {
                String mFragP = new StringUtil().getLeft(p3);
                String mNodeP = new StringUtil().getRight(p3);
                MNode parentMNode = f.getMNode(mNodeP);
                childMNode.setParents(parentMNode);
                
                parentMNode.columnName = parentMNode.name;
            }
        }
    }
    

    public void addContexts(String mFrag, String sql) {
        MFrag f = getMFrag(mFrag);
    }

    public void updateParentNodesOVs(MFrag f, List<MIsANode> removeList, Map<String, String> surviveMap) {
    	for (MNode n : f.arrayResidentNodes) {
    		for (MNode ip : n.inputParentMNodes) {
    			for (MIsANode isa : removeList) {
    				OVariable ov = (OVariable)isa.ovs.get(0);
    				for (OVariable ov_ip: ip.ovs) {
    					if (ov.entityType.equalsIgnoreCase(ov_ip.entityType)) { 	
    						String ovName = surviveMap.get(ov_ip.entityType);
    						ov_ip.name = ovName; 
    					}
    				}
    			}
    		}    		
    	}    	
    }
    
    public void updateContexts() {
        for (MFrag f : mfrags.keySet()) {
            ArrayList<MIsANode> removeList;
            Map<String, String> surviveMap;
            OVariable ov;
            OVariable ov2;
            
            if (f.name.equalsIgnoreCase("heater_item_temperature")) {
            	  logger.debug(f.joiningSQL);
            } 
            
            if (f.joiningSQL != null) { 
            	// Step 1. Add "where" clause to the SQL script of this MFrag
                // e.g.,)
                // quality_result_SLAB_NO  quality_result
                // rm_pass_PASS_NO  rm_pass
                // rm_pass_SLAB_NO  rm_pass
                // rm_pass_PASS_NO  rm_pass
                // rm_pass_SLAB_NO  rm_pass
                // -> where quality_result.SLAB_NO == rm_pass.SLAB_NO
                // If there are tables and they have same keys, then create the "where" clause 
                //
                List<String> dubList = new ArrayList<String>(); 
                String sql = "WHERE \r\n";
                for (int i = 0; i < f.arrayIsaContextNodes.size(); i++) {
                	MIsANode isa = f.arrayIsaContextNodes.get(i);
                	ov = (OVariable)isa.ovs.get(0);
                	MFrag orgF = getMFrag(ov.originMFrag);
                	
                	// timedPrimaryKey will be skipped.
         			if (orgF != null &&orgF.timedPrimaryKey != null && orgF.timedPrimaryKey.equalsIgnoreCase(ov.originKey)){
        				continue;
        			}
        			
                	for (int j = 0; j < i+1; j++) {
                		MIsANode isa2 = f.arrayIsaContextNodes.get(j);
                    	ov2 = (OVariable)isa2.ovs.get(0);
                    	if (!ov.originMFrag.equalsIgnoreCase(ov2.originMFrag)){
                    		if (ov.originKey.equalsIgnoreCase(ov2.originKey)){
//                    		if (ov.entityType.equalsIgnoreCase(ov2.entityType)){
//	                    		logger.debug(ov + ":" + ov.originMFrag + "   <->   " + ov2 + ":" + ov2.originMFrag);
	                    		String curKey = ov.originMFrag + "." + ov.originKey;
	                    		String otherKey = ov2.originMFrag + "." + ov2.originKey;
	                    		String wh = curKey + " = " + otherKey;
	                    		 
	                    		if (!dubList.contains(wh)) {
		                    		dubList.add(wh);
		                    		sql += wh + " &&\r\n";
	                    		}
                			}
                    	}
                    }
                } 
                
                if (dubList.size() > 0) {
		            sql = sql.substring(0, sql.length() - 5);
		            f.joiningSQL = f.joiningSQL + sql;
                }
                // Step 2. remove redundant OVs, if they are same OVs
	            // e.g.) IsA(t1, TIME), IsA(t2, TIME)
	            // => IsA(t1, TIME)
	            // Also, resident nodes using these OVs changes to not having redundant OVs
                surviveMap = new HashMap<String, String>();
                removeList = new ArrayList<MIsANode>();
                
                for (MIsANode isa : f.arrayIsaContextNodes) {
                    ov = (OVariable)isa.ovs.get(0);
//                    logger.debug(ov.name + "  " + ov.originMFrag);
                    if (surviveMap.containsKey(ov.entityType)) {
                        removeList.add(isa);
                        continue;
                    }
                    surviveMap.put(ov.entityType, ov.name);
                }
                
                updateParentNodesOVs(f, removeList, surviveMap);
                
                if (removeList.size() > 0) {
                    f.arrayIsaContextNodes.removeAll(removeList);
                } 
                
            } else if (f.joiningSQL == null) {
            	/*
            	 * TO DO: This should be updated for necessary OVs.
            	 * e.g., ) predecessor (pret, t); IsA(pret, Time); IsA(t, Time);
            	 * We should not remove one of time OVs.
            	 */
            	
//	            logger.debug(f.arrayContextNodes);
	            
	            // remove redundant OVs, if they are same OVs
	            // e.g.) IsA(t1, TIME), IsA(t2, TIME)
	            // => IsA(t1, TIME)
	            // Also, resident nodes using these OVs changes to not having redundant OVs
                surviveMap = new HashMap<String, String>();
                removeList = new ArrayList<MIsANode>();
                
                for (MIsANode isa : f.arrayIsaContextNodes) {
                    ov = (OVariable)isa.ovs.get(0);
                    if (surviveMap.containsKey(ov.entityType)) {
                        removeList.add(isa);
                        continue;
                    }
                    surviveMap.put(ov.entityType, ov.name);
                }
                
                updateParentNodesOVs(f, removeList, surviveMap);
                
                if (removeList.size() > 0) {
                    f.arrayIsaContextNodes.removeAll(removeList);
                }
            }
        }
    }

    public void addCLDType(String c, CLD cldType) {
        String mFrag = StringUtil.This().getLeft(c);
        String mNode = StringUtil.This().getRight(c);
        MFrag f = getMFrag(mFrag);
        MNode childMNode = f.getMNode(mNode);
        childMNode.setCLDs(cldType);
    }

    public void updateCLDs() {
        for (MFrag f : mfrags.keySet()) {
        	f.updateCLDs();
        }
    }

    public String toString() {
        String s = "[M: " + name + "\r\n";
        for (MFrag m : mfrags.keySet()) {
            s = s + "\t" + m.toString() + "\r\n";
        }
        s = s + "]";
        return s;
    }

    public String toString(String ... inclusion) {
        ArrayList<String> inclusions = new ArrayList<String>();
        int i = 0;
        while (i < inclusion.length) {
            inclusions.add(inclusion[i]);
            ++i;
        }
        String s = "[M: " + name + "\r\n";
        for (MFrag m : mfrags.keySet()) {
            s = s + "\t" + m.toString(inclusions) + "\r\n";
        }
        s = s + "]";
        return s;
    }

    public Double getSumMFragLogScores() {
        Double logSCs = 0.0;
        for (MFrag f : mfrags.keySet()) {
            Double logSC = f.getSumMNodeLogScores();
            logSCs = logSCs + logSC;
            logger.debug(f.toString() + " : " + logSC);
        }
        return logSCs;
    }

    public Double getAvgLogMFragScore() {
        Double logSC = 0.0;
        for (MFrag f : mfrags.keySet()) {
            logSC = logSC + f.getAvglogMNodeScore();
            mfrags.put(f, f.getAvglogMNodeScore());
        }
        return logSC;
    }
 
    public int compareTo(MTheory o) {
        return 0;
    }
}

