/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.tetrad.bayes.BayesIm
 *  edu.cmu.tetrad.bayes.BayesPm
 *  edu.cmu.tetrad.bayes.DirichletBayesIm
 *  edu.cmu.tetrad.bayes.DirichletEstimator
 *  edu.cmu.tetrad.data.DataSet
 *  edu.cmu.tetrad.graph.Dag
 *  edu.cmu.tetrad.graph.EdgeListGraph
 *  edu.cmu.tetrad.graph.Graph
 *  edu.cmu.tetrad.graph.Node
 */
package mebn_rm.MEBN.CLD;

import edu.cmu.tetrad.bayes.BayesIm;
import edu.cmu.tetrad.bayes.BayesPm;
import edu.cmu.tetrad.bayes.DirichletBayesIm;
import edu.cmu.tetrad.bayes.DirichletEstimator;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Dag;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mebn_rm.MEBN.CLD.LPD_Discrete;
import mebn_rm.MEBN.MNode.MNode;
import mebn_rm.MEBN.parameter.Parameter;
import mebn_rm.MEBN.rv.RV;
import mebn_rm.data.ConditionalDataSet; 
import mebn_rm.util.Tetrad_Util;
import util.TempMathFunctions;

public class Categorical
extends LPD_Discrete {
    Map<String, BayesIm> ipcIMs = new HashMap<String, BayesIm>();

    public Categorical() {
        super("", "Dirichlet");
        this.parameterSize = 1;
        this.isSampling = false;
    }

    public Categorical(String name) {
        super(name, "Dirichlet");
        this.parameterSize = 1;
        this.isSampling = false;
    }

    public ArrayList<String> generateParent(int parentSize) {
        ArrayList<String> parents = new ArrayList<String>();
        int i = 0;
        while (i < parentSize) {
            parents.add(this.randomByUniform("T", "W"));
            ++i;
        }
        return parents;
    }

    public Double calculateDist(RV rv, Parameter para) {
        if (!para.bTrueParameter) {
            return 0.5;
        }
        if (rv.value.equalsIgnoreCase("Slow") || rv.value.equalsIgnoreCase("High") || rv.value.equalsIgnoreCase("T") || rv.value.equalsIgnoreCase("Good")) {
            return para.parameters.get(0);
        }
        return 1.0 - para.parameters.get(0);
    }

    public Object getValueAt(BayesIm bayesIm, int nodeIndex, int tableRow, int tableCol) {
        int[] parentVals = bayesIm.getParentValues(nodeIndex, tableRow);
        if (tableCol < parentVals.length) {
            Node columnNode = bayesIm.getNode(bayesIm.getParent(nodeIndex, tableCol));
            BayesPm bayesPm = bayesIm.getBayesPm();
            return bayesPm.getCategory(columnNode, parentVals[tableCol]);
        }
        int colIndex = tableCol - parentVals.length;
        if (colIndex < bayesIm.getNumColumns(nodeIndex)) {
            return bayesIm.getProbability(nodeIndex, tableRow, colIndex);
        }
        return "null";
    }

    @Override
    public List<String> getCategories() {
        if (this.arrayCategories == null) {
            this.arrayCategories = new ArrayList();
            BayesIm bayesIm = null;
            Iterator<String> iterator = this.ipcIMs.keySet().iterator();
            if (iterator.hasNext()) {
                String condition = iterator.next();
                bayesIm = this.ipcIMs.get(condition);
            }
            if (this.selectedData != null) {
                Node thisNode = this.selectedData.getVariable(this.mNode.name);
                int c = 0;
                while (c < bayesIm.getBayesPm().getNumCategories(thisNode)) {
                    this.arrayCategories.add(bayesIm.getBayesPm().getCategory(thisNode, c));
                    ++c;
                }
            }
        } else {
            return this.arrayCategories;
        }
        return this.arrayCategories;
    }

    @Override
    public String getCPS() {
        List<MNode> discreteParents = this.mNode.getDiscreteParents();
        String s = "{ defineState(Discrete, ";
        Node thisNode = this.selectedData.getVariable(this.mNode.name);
        for (String state : this.mNode.getCategories()) {
            s = String.valueOf(s) + state + ", ";
        }
        s = s.substring(0, s.length() - 2);
        s = String.valueOf(s) + " ); \n";
        s = String.valueOf(s) + "p( " + this.mNode.name;
        if (discreteParents.size() > 0) {
            s = String.valueOf(s) + " | ";
            for (MNode p : discreteParents) {
                s = String.valueOf(s) + p.name + " , ";
            }
            s = s.substring(0, s.length() - 2);
        }
        s = String.valueOf(s) + " ) = \n";
        boolean b = true;
        for (String condition : this.ipcIMs.keySet()) {
            BayesIm bayesIm = this.ipcIMs.get(condition);
            if (!condition.isEmpty()) {
                if (b) {
                    s = String.valueOf(s) + "if( " + condition + " ) {";
                    b = false;
                } else {
                    s = String.valueOf(s) + "\nelse if( " + condition + " ) {";
                }
            } else {
                s = String.valueOf(s) + "{ ";
            }
            int k = 0;
            while (k < bayesIm.getNumColumns(0)) {
                Double p = bayesIm.getProbability(0, 0, k);
                String prob = TempMathFunctions.safeDoubleAsString(p);
                String state2 = bayesIm.getBayesPm().getCategory(thisNode, k);
                s = String.valueOf(s) + state2 + " : " + prob;
                s = String.valueOf(s) + "; ";
                ++k;
            }
            s = String.valueOf(s) + " }";
        }
        s = String.valueOf(s) + "\n";
        return s;
    }

    @Override
    public Double calculateBestPara(ConditionalDataSet CD, ConditionalDataSet prior_CD) {
        if (this.mNode.name.equalsIgnoreCase("Activity")) {
            System.out.println();
        }
        EdgeListGraph hybridGraph = new EdgeListGraph();
        this.IPCs = this.initIPCs((Graph)hybridGraph);
        if (this.IPCs == null) {
            return 0.0;
        }
        if (this.mNode.parentMNodes.size() == 2) {
            System.out.println();
        }
        for (String ipc : this.IPCs) {
            DataSet _dataSet_dis = null;
            _dataSet_dis = ipc.equalsIgnoreCase("") ? this.selectedData : Tetrad_Util.getSubsetdataFromIPC(ipc, this.selectedData);
            EdgeListGraph graph = new EdgeListGraph();
            Node child = _dataSet_dis.getVariable(this.mNode.name);
            graph.addNode(child);
            Dag dag = new Dag((Graph)graph);
            BayesPm bayesPm = new BayesPm((Graph)dag);
            DirichletBayesIm prior = DirichletBayesIm.symmetricDirichletIm((BayesPm)bayesPm, (double)0.5);
            DirichletBayesIm bayesIm = DirichletEstimator.estimate((DirichletBayesIm)prior, (DataSet)_dataSet_dis);
            this.ipcIMs.put(ipc, (BayesIm)bayesIm);
        }
        return 0.0;
    }
}
