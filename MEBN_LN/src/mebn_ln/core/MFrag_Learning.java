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
package mebn_ln.core;
 
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import mebn_ln.core.Learning_Common;
import mebn_ln.core.MNode_Learning;
import mebn_rm.MEBN.CLD.Categorical;
import mebn_rm.MEBN.MFrag.MFrag;
import mebn_rm.MEBN.MNode.MNode;

/**
 * MFrag_Learning is the class for MFrag learning. 
 * <p>
 * 
 * @author      Cheol Young Park
 * @version     0.0.1
 * @since       1.5
 */

public class MFrag_Learning extends Learning_Common {
	static Logger logger = Logger.getLogger(MFrag_Learning.class);
    MNode_Learning MNode_learning = new MNode_Learning();
    MFrag mFrag = null;

    public void run(MFrag f) {
        mFrag = f;
        getCandidateMGraphs(f, 5.0);
        logger.debug("******************* Begin MFrag learning with the " + f.name + " MFrag *******************");
  
        f.initSelectedDataset(-1);
        run_operation(f);
        logger.debug("******************* End MFrag learning with the " + f.name + " MFrag *******************");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void run_operation(MFrag f) { 
        if (f.learningType == MFrag.LearningType.PARAMETER || f.learningType == MFrag.LearningType.BAYES) {
            for (MNode n : f.arrayResidentNodes) {
                MNode_learning.run(n);
            }
            return;
        }
        if (f.learningType != MFrag.LearningType.STRUCTURE_HYBRID_DISCRETIZED) return;
        
//        This is a structuring BN learning  
//	        try {
//	            if (MTheory_Learning.structure_learning) {
//	                BNStructureLearning.run(mFrag.cvsFile, f);
//	            } 
//	        }
//	        catch (IOException e) {
//	            e.printStackTrace();
//	        }
	         
        
        for (MNode n : f.arrayResidentNodes) {
            MNode_learning.run(n);
            
            List<MNode> recurNodes = n.getRecursiveNodes();
            
            for (MNode re : recurNodes) {
            	MNode_learning.run(re);
            }
        }
    }

    public void getCandidateMGraphs(MFrag mfrag, Double size) {
    }
}

