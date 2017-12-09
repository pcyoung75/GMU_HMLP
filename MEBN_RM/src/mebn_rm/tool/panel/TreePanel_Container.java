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
package mebn_rm.tool.panel; 
import java.awt.BorderLayout; 
import java.awt.event.ActionEvent;  

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton; 
import javax.swing.JPanel;

import mebn_rm.tool.MEBN_RM_Console; 
 
  
public class TreePanel_Container extends MyPanel { 
    /**
	 * 
	 */
	private static final long serialVersionUID = 38238115429232725L;

	MEBN_RM_Console console = null;  
 
	TreePanel_Left leftTree = null;  
	Action setRun4;   
	JButton btn4; 
	
	public TreePanel_Container(MEBN_RM_Console con, TreePanel_Left l){
		console = con; 
		leftTree = l; 
		 
		treeFrame.add(leftTree);  
		
		JPanel btnPane = createButtonPane();  
		treeFrame.add(btnPane, BorderLayout.SOUTH);
	} 
	
	public void init(){  
		if (console.wMode == MEBN_RM_Console.windowMode.SELECT_DB){  
			changeName("RM to MEBN"); 
			btn4.setText("Select"); btn4.setVisible(true);
		} else if (console.wMode == MEBN_RM_Console.windowMode.EDIT_DB){ 
		}  
		
		leftTree.init(); 
		
		this.invalidate();
		this.setVisible(true);
		this.repaint();
	} 
	
	public JPanel createButtonPane() {
		ButtonGroup group = new ButtonGroup();

		// Text Button Panel
		JPanel ButtonPanel = new JPanel();
		ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.X_AXIS));
		ButtonPanel.setBorder(border5);
  
		// Add Button 
		btn4 = createRun4Button(); ButtonPanel.add(btn4); group.add(btn4); ButtonPanel.add(Box.createRigidArea(VGAP5));
		
		return ButtonPanel;
	}
	 
	public JButton createRun4Button() {
		setRun4 = new AbstractAction("Btn") {
			public void actionPerformed(ActionEvent e) {  
				System.out.println(console.wMode);
				if (console.wMode == MEBN_RM_Console.windowMode.SELECT_DB ||
					console.wMode == MEBN_RM_Console.windowMode.EDIT_DB){  
					console.selectedDB = leftTree.selectedObject;
					console.init(MEBN_RM_Console.windowMode.EDIT_DB);
				} 
			}
		};
		return createButton(setRun4);
	}   
}
