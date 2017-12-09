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

import mebn_rm.MEBN.parameter.Parameter;
import mebn_rm.data.ConditionalDataSet;
import util.TempMathFunctions;
import util.math.Sum_for_Log; 

public class Learning_Common extends TempMathFunctions {
    public Sum_for_Log logsum = new Sum_for_Log();
    public String typeLearning = "ML_Approach";

    public boolean isMC_Approach() {
        if (typeLearning.equalsIgnoreCase("MC_Approach")) {
            return true;
        }
        return false;
    }

    public boolean isML_Approach() {
        if (typeLearning.equalsIgnoreCase("ML_Approach")) {
            return true;
        }
        return false;
    }

    public Double calculate(ConditionalDataSet CD, Parameter para) {
        Double logTotal = 0.0;
        return logTotal;
    }
}

