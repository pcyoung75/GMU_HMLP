/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  mebn_rm.MEBN.MFrag.MFrag
 *  mebn_rm.MEBN.MFrag.MFrag$MFragType
 *  mebn_rm.MEBN.MTheory.MRoot
 *  mebn_rm.MEBN.MTheory.MTheory
 *  mebn_rm.util.SortableValueMap
 */
package mebn_ln.core;

import java.io.PrintStream;
import java.util.Set;
import mebn_ln.core.Learning_Common;
import mebn_ln.core.MFrag_Learning;
import mebn_rm.MEBN.MFrag.MFrag;
import mebn_rm.MEBN.MTheory.MRoot;
import mebn_rm.MEBN.MTheory.MTheory;
import util.SortableValueMap; 

public class MTheory_Learning
extends Learning_Common {
    MFrag_Learning MFrag_learning = new MFrag_Learning();
    public static boolean structure_learning = true;

    public void run(MRoot mRoot) {
        this.getCandidateMTheories(mRoot.mtheoryCANs);
        System.out.println("******************* Begin MTheory learning with an MRoot *******************");
        System.out.println(mRoot.toString());
        this.run_operation(mRoot.mtheoryCANs);
    }

    public void run_operation(SortableValueMap<MTheory, Double> mtheoryCANs) {
        for (MTheory m22 : mtheoryCANs.keySet()) {
            for (MFrag f : m22.mfrags.keySet()) {
                if (f.mFragType != MFrag.MFragType.COMMON) continue;
                this.MFrag_learning.run((MFrag)f);
            }
        }
        for (MTheory m22 : mtheoryCANs.keySet()) {
            Object f;
            if (!this.isMC_Approach()) continue;
            f = m22.getAvgLogMFragScore();
        }
        for (MTheory m22 : mtheoryCANs.keySet()) {
            System.out.println("6 >>>>>>> " + m22.name + " logMTheorySC : " + mtheoryCANs.get((Object)m22));
        }
    }

    public void getCandidateMTheories(SortableValueMap<MTheory, Double> mtheoryCANs) {
    }
}
