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
package hml_tool; 
import java.awt.BorderLayout; 
import java.awt.event.ActionEvent;   
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton; 
import javax.swing.JPanel;

import hml_tool.HML_Console.windowMode;
import util.gui.MyPanel; 
  
/**
 * TreePanel_Container is the class containing several tree panels. 
 * <p>
 * 
 * @author      Cheol Young Park
 * @version     0.0.1
 * @since       1.5
 */

public class TreePanel_Container extends MyPanel {  
	private static final long serialVersionUID = 134803263526752867L;

	HML_Console console = null;  
 
	TreePanel_Left leftTree = null;
	TreePanel_Right rightTree = null; 
	Action setRun1;
	Action setRun2; 
	Action setRun3;
	Action setRun4;  
	JButton btn1;
	JButton btn2;
	JButton btn3;
	JButton btn4; 
	
	public TreePanel_Container(HML_Console con, TreePanel_Left l, TreePanel_Right r){
		console = con; 
		leftTree = l;
		rightTree = r;
		
		JPanel center = createPaneH(leftTree, rightTree);
		treeFrame.add(center, BorderLayout.NORTH);  
		
		JPanel btnPane = createButtonPane();  
		treeFrame.add(btnPane, BorderLayout.SOUTH);
	} 
	
	public void init(){  
		if (console.wMode == HML_Console.windowMode.CONNECT_DB){
			setVisible(false);
			btn1.setVisible(false);
			btn2.setVisible(false);
			btn3.setVisible(false);
			btn4.setVisible(false);
		} else if (console.wMode == HML_Console.windowMode.SELECT_DB){  
			setVisible(true);
			changeName("Select Database");
			btn1.setVisible(false);
			btn2.setVisible(false);
			btn3.setVisible(false);
			btn4.setText("Select"); btn4.setVisible(true);
		} else if (console.wMode == HML_Console.windowMode.EDIT_DB){
			changeName("Create World Model"); 
			btn1.setText("Remove"); btn1.setVisible(true);
			btn2.setText("Create Table"); btn2.setVisible(true);
			btn3.setText("Create Attribute"); btn3.setVisible(true); 
			btn4.setText("Next[Add Parents] >>"); btn4.setVisible(true);
		} else if (console.wMode == HML_Console.windowMode.ADD_PARENTS){
			changeName("Select Parents"); 
			btn1.setText("Clear"); btn1.setVisible(true);
			btn2.setText("Select Parent"); btn2.setVisible(true);
			btn3.setVisible(false); 
			btn4.setText("Next[Add Contexts] >>"); btn4.setVisible(true);
		} else if (console.wMode == windowMode.JOIN_RELATIONS) {
			changeName("Set Joining Condition"); 
			btn1.setText("Clear"); btn1.setVisible(true);
			btn2.setText("Select Conditions"); btn2.setVisible(true);
			btn3.setVisible(false); 
			btn4.setText("Next[Add CLDs] >>"); btn4.setVisible(true);
		} else if (console.wMode == HML_Console.windowMode.ADD_CLD){
			changeName("Class Local Distribution");
			btn1.setText("<<Prev"); btn1.setVisible(true);
			btn2.setText("Select CLD"); btn2.setVisible(true);
			btn3.setVisible(false);  
			btn4.setText("Next[MEBN Learning] >>"); btn4.setVisible(true);
		} else if (console.wMode == windowMode.LEARNING) { 
		} else if (console.wMode == windowMode.EVALUATION) { 
		}
		
		leftTree.init();
		rightTree.init();
		
		this.invalidate();
		this.repaint();
	} 
	
	public JPanel createButtonPane() {
		ButtonGroup group = new ButtonGroup();

		// Text Button Panel
		JPanel ButtonPanel = new JPanel();
		ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.X_AXIS));
		ButtonPanel.setBorder(border5);
  
		// Add Button 
		btn1 = createRun1Button(); ButtonPanel.add(btn1); group.add(btn1); ButtonPanel.add(Box.createRigidArea(VGAP5));
		btn2 = createRun2Button(); ButtonPanel.add(btn2); group.add(btn2); ButtonPanel.add(Box.createRigidArea(VGAP5));
		btn3 = createRun3Button(); ButtonPanel.add(btn3); group.add(btn3); ButtonPanel.add(Box.createRigidArea(VGAP5));
		btn4 = createRun4Button(); ButtonPanel.add(btn4); group.add(btn4); ButtonPanel.add(Box.createRigidArea(VGAP5));
		
		return ButtonPanel;
	}
	
	// Button [Start, Cancel]
	public JButton createRun1Button() {
		setRun1 = new AbstractAction("Btn") {
			public void actionPerformed(ActionEvent e) { 
				if (console.wMode == HML_Console.windowMode.SELECT_DB){  					
				} else if (console.wMode == HML_Console.windowMode.EDIT_DB){ 
				} else if (console.wMode == HML_Console.windowMode.ADD_PARENTS){ 
				} else if (console.wMode == windowMode.JOIN_RELATIONS) {
				} else if (console.wMode == HML_Console.windowMode.ADD_CLD){ 
				} else if (console.wMode == windowMode.LEARNING) { 
				} else if (console.wMode == windowMode.EVALUATION) { 
				}
			}
		};
		return createButton(setRun1);
	} 
	
	public JButton createRun2Button() {
		setRun1 = new AbstractAction("Btn") {
			public void actionPerformed(ActionEvent e) { 
				if (console.wMode == HML_Console.windowMode.SELECT_DB){   
				} else if (console.wMode == HML_Console.windowMode.EDIT_DB){ 
				} else if (console.wMode == HML_Console.windowMode.ADD_PARENTS){ 
					// Add parents
					// Create an MFrag, if it is required. 
					console.mTheory.addParents(leftTree.selectedObject, rightTree.selectedObjects);
					console.init(HML_Console.windowMode.ADD_PARENTS);
				} else if (console.wMode == windowMode.JOIN_RELATIONS) { 
				} else if (console.wMode == HML_Console.windowMode.ADD_CLD){ 
				} else if (console.wMode == windowMode.LEARNING) { 
				} else if (console.wMode == windowMode.EVALUATION) { 
				}
			}
		};
		return createButton(setRun1);
	} 
	
	public JButton createRun3Button() {
		setRun1 = new AbstractAction("Btn") {
			public void actionPerformed(ActionEvent e) {  
				if (console.wMode == HML_Console.windowMode.SELECT_DB){  
				} else if (console.wMode == HML_Console.windowMode.EDIT_DB){
				} else if (console.wMode == HML_Console.windowMode.ADD_PARENTS){
				} else if (console.wMode == windowMode.JOIN_RELATIONS) {
				} else if (console.wMode == HML_Console.windowMode.ADD_CLD){ 
				} else if (console.wMode == windowMode.LEARNING) { 
				} else if (console.wMode == windowMode.EVALUATION) { 
				}
			}
		};
		return createButton(setRun1);
	} 
	
	public JButton createRun4Button() {
		setRun4 = new AbstractAction("Btn") {
			public void actionPerformed(ActionEvent e) {   
				if (console.wMode == HML_Console.windowMode.SELECT_DB){  
					console.selectedDB = leftTree.selectedObject;
					console.init(HML_Console.windowMode.EDIT_DB);
				} else if (console.wMode == HML_Console.windowMode.EDIT_DB){
					console.init(HML_Console.windowMode.ADD_PARENTS);
				} else if (console.wMode == HML_Console.windowMode.ADD_PARENTS){
					console.init(HML_Console.windowMode.JOIN_RELATIONS);
				} else if (console.wMode == windowMode.JOIN_RELATIONS) {
					console.init(HML_Console.windowMode.ADD_CLD);
				} else if (console.wMode == HML_Console.windowMode.ADD_CLD){ 
					console.init(HML_Console.windowMode.LEARNING);
				} else if (console.wMode == windowMode.LEARNING) { 
					console.init(HML_Console.windowMode.EVALUATION);
				} else if (console.wMode == windowMode.EVALUATION) { 
				}
			}
		};
		return createButton(setRun4);
	}  
}
