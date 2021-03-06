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
package test.text_mode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List; 
import mebn_ln.core.MTheory_Learning; 
import mebn_rm.MEBN.MTheory.MRoot;
import mebn_rm.MEBN.MTheory.MTheory;
import mebn_rm.RDB.RDB;
import mebn_rm.core.RM_To_MEBN; 

/**
 * text_mode_continuous_heater_model is the test class to test a simple hybrid heater 
 * model. This class requires a database test_heater. To get the database, use SQL scripts 
 * in "examples/db/heater_hybrid".
 * <p>
 * 
 * @author      Cheol Young Park
 * @version     0.0.1
 * @since       1.5
 */
 
public class text_mode_hybrid_heater_model {

	public text_mode_hybrid_heater_model() {
	}

	public void run() {
		
		//1. Initialize RDB
		RDB.This().connect("localhost", "root", "jesus");
		String database = "heater_hybrid"; 
		
		try {
			RDB.This().init(database);
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		
		//2. Perform MEBN-RM 
		MTheory	mTheory =  new RM_To_MEBN(RDB.This()).run(); 
		
		//3. Add parents
		String childMNode = "";
		List<String> parentMNodes = null; 
		
		childMNode = "heateractuator_item.energy_InHAI";
		parentMNodes = new ArrayList<String>();
		parentMNodes.add("slabinput_item.temperature_InSII");
		parentMNodes.add("slabinput_item.grade_InSII");		// Discrete
		parentMNodes.add("slabinput_item.volume_InSII");	// Discrete		
		mTheory.addParents(childMNode, parentMNodes);

 		System.out.println(mTheory.toString("MFrag", "MNode", "CLD" ));  
 		
		childMNode = "heater_item.temperature_InHI";
		parentMNodes = new ArrayList<String>();
		parentMNodes.add("slabinput_item.temperature_InSII"); 
		parentMNodes.add("energy_InHAI.energy_InHAI");
		mTheory.addParents(childMNode, parentMNodes);
		  
 		System.out.println(mTheory.toString("MFrag", "MNode", "CLD" ));  
 		
		//4. Update contexts 
		mTheory.updateContexts();
		 
		//5. Add CLD type  
		mTheory.updateCLDs(); 
		
		//6. Learn MEBN  
		MRoot mroot = new MRoot();
		mroot.setMTheories(mTheory);  
		new MTheory_Learning().run(mroot);  
		 
 		System.out.println(mTheory.toString("MFrag", "MNode", "CLD" ));  
	}
	
	public static void main(String[] args) {
		text_mode_hybrid_heater_model h = new text_mode_hybrid_heater_model();
		h.run();
	} 
}
