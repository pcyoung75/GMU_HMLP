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
package mebn_rm.core;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import mebn_rm.MEBN.MFrag.MFrag;
import mebn_rm.MEBN.MNode.MCNode;
import mebn_rm.MEBN.MNode.MDNode;
import mebn_rm.MEBN.MNode.MNode;
import mebn_rm.MEBN.MTheory.MTheory;
import mebn_rm.MEBN.MTheory.OVariable;
import mebn_rm.RDB.RDB;
import mebn_rm.util.StringUtil; 

/**
 * RM_To_MEBN is the class to construct a partial MEBN from a relational schema. 
 * The current version of RM_To_MEBN works for MySQL. Thus, an input relational database
 * can be a database in MySQL. The specific mechanism can be found in "Park, C. Y. (2017). 
 * Multi-Entity Bayesian Networks Learning for Predictive Situation Awareness. 
 * PhD Dissertation. George Mason University.".  	
 * <p>
 * 
 * @author      Cheol Young Park
 * @version     0.0.1
 * @since       1.5
 */

public class RM_To_MEBN {
	static Logger logger = Logger.getLogger(RM_To_MEBN.class);
	RDB rdb = null;

	/**
	 * This method is a constructor of this class. 
	 * @param r		a relational database
	 */
	public RM_To_MEBN(RDB r) {
		rdb = r;
	}

	
	/**
	 * Used for executing MEBN-RM algorithm. 
	 * @return		an MTheory
	 */
	public MTheory run() {
		MFrag f;
		String resNodeName;
		List<String> keys;
		OVariable ov;
		String prefix;
		ArrayList<OVariable> ovs;
		Object origin;
		Integer numEntity = 0;
		Integer numRV = 0;
		Integer numMFrag = 0;
		Integer numOV = 0; 
		MTheory mTheory = null;
		String nameMTheory = rdb.schema;
		
		Long time1 = System.nanoTime(); 
		
		mTheory = new MTheory(nameMTheory);
		mTheory.rdb = rdb;
		
		ArrayList<String> entityTables = (ArrayList) rdb.mapTableTypesAndTables.get((Object) "EntityTable");
		
		for (String table : entityTables) {
			mTheory.entities.add(table);
			numEntity = numEntity + 1;
			List attrs = (List) rdb.mapTableAndAttributes.get((Object) table);
			if (attrs == null)
				continue;
			f = new MFrag(mTheory, table);
			numMFrag = numMFrag + 1;
			prefix = StringUtil.This().createAbbreviation(table);
			keys = (List) rdb.mapTableAndKeys.get((Object) table);
			ovs = new ArrayList();
			for (String key : keys) {  
				origin = rdb.getOriginFromKey(table, key);
				ov = new OVariable(f.getTableName(), key, (String) origin);
				ovs.add(ov);
				numOV += 1;
				new mebn_rm.MEBN.MNode.MIsANode(f, ov);
			}
			Iterator iterator = attrs.iterator();
			while (iterator.hasNext()) {
				String attr = (String) iterator.next();
				List<String> domains = rdb.mapDomainVaules.get(attr);
				//resNodeName = String.valueOf(prefix) + "_" + attr;
				resNodeName = attr  + "_In" + String.valueOf(prefix);
				if (domains != null) {
					MDNode md = new MDNode(f, resNodeName, ovs);
					md.setAttributeName(attr);
					f.arrayResidentNodes.add(md);
					numRV = numRV + 1;
					continue;
				}
				MCNode mc = new MCNode(f, resNodeName, ovs);
				mc.setAttributeName(attr);
				f.arrayResidentNodes.add(mc);
				numRV = numRV + 1;
			}
		}
		
		List<String> relationshipTables = (List) rdb.mapTableTypesAndTables.get((Object) "RelationshipTable");
		
		if (relationshipTables != null) {
			for (String table2 : relationshipTables) {
				f = new MFrag(mTheory, table2);
				numMFrag = numMFrag + 1;
				prefix = StringUtil.This().createAbbreviation(table2);
				keys = (List) rdb.mapTableAndKeys.get((Object) table2);
				ovs = new ArrayList<OVariable>();
				for (String key : keys) {
					origin = rdb.getOriginFromKey(table2, key);
					ov = new OVariable(f.name, key, (String) origin);
					ovs.add(ov);
					numOV += 1;
					new mebn_rm.MEBN.MNode.MIsANode(f, ov);
				}
				List<String> attrs = (List) rdb.mapTableAndAttributes.get((Object) table2);
				if (attrs != null) {
					for (String attr : attrs) {
						resNodeName = attr  + "_In" + String.valueOf(prefix);
						List<String> domains = rdb.mapDomainVaules.get(attr);
						if (domains != null) {
							MDNode md = new mebn_rm.MEBN.MNode.MDNode(f, resNodeName, ovs);
							md.setAttributeName(attr);
							f.arrayResidentNodes.add(md);
							numRV = numRV + 1;
							continue;
						}
						MCNode mc = new mebn_rm.MEBN.MNode.MCNode(f, resNodeName, ovs);
						mc.setAttributeName(attr);
						f.arrayResidentNodes.add(mc);
						numRV = numRV + 1;
					}
				}
				
				if (rdb.mapTableAndAttributes.get((Object) table2) != null)
					continue;
				MDNode md = new mebn_rm.MEBN.MNode.MDNode(f, table2, ovs);
				md.setAttributeName(table2);
				f.arrayResidentNodes.add(md);				
				numRV = numRV + 1;
				f.mFragType = MFrag.MFragType.REFERENCE;
			}
		}
		
		// Coverting time
        Double seconds = (double)(System.nanoTime()-time1) / 1000000000.0;
		
		logger.debug(mTheory);
		logger.debug("=========================================================================================");
		logger.debug("numEntity\tnumRV\tnumMFrag");
		logger.debug("# of Entity \t # of MFrag \t # of Resident Node \t # of IsA Nodes");
		logger.debug(numEntity + "\t" + numMFrag + "\t" + numRV + "\t" + numOV );
		logger.debug("Mapping time");
		logger.debug(seconds);
		logger.debug("=========================================================================================");
		  
		return mTheory;
	}
}
