package algorithm;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphImporter.ReadGraph_w_edgeList;


/**
 * 
 * @author Dewan Ferdous Wahid
 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
 * @date September 23, 2016
 * 
 **/

public class CCEditing {
	
	/**
	 * 
	 * Solving Relaxed ILP for Correlation Clustering Problems
	 * 
	 * @param g - signed graph
	 * @param intFlag - 1-> solving ILP, 0-> solving relaxed ILP
	 * 
	 * @return the unsigned weighted graph induced by the solution matrix
	 * 
	 * Using libraries: (1) jGrapht; available at:  www.jGrapht.org
	 * 					(2) IBM ILOG Cplex V.12.1 (academic edition)
	 * 
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> ILpRelaxed 
	(UndirectedGraph<Integer, DefaultWeightedEdge> g, int ilpFlag) {


		//Creating output Induced G_{X_R}} graph object (undirected and weighted)
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> ilpSolGraph =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// Number of vertices in the input graph
		int n = g.vertexSet().size();    

		/**
		 * Solve the ILP/ILP-Relaxed Correlation Clustering Proglem by using ILOG Cplex V.12.1
		 **/ 
		try {
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);

			/** 
			 * Creating Decision Variables X= ( x_{ij} ) for each i,j in V
			 * If x_{ij} = 0 then (i,j) in E; otherwise x_{ij} = 1.
			 **/ 	
			IloNumVar[][] x = new IloNumVar[n+1][];

			if (ilpFlag == 0){			// for selecting relaxed ILP
				//Value range of variables 
				for (int i=1; i<n+1; i++) {
					x[i] = cplex.numVarArray(n+1, 0, 1);     
				}
			}
			else if (ilpFlag == 1) {	// for selecting ILP
				//Value range of variables 
				for (int i=1; i<n+1; i++) {
					x[i] = cplex.boolVarArray(n+1);  		
				}
			}
			else System.out.print("Error in ILP flag");

			/**
			 * Create two dummy variables y[0][0] & y[0][1] 
			 * Assigned y[0][0] = 0; and y[0][1] = 1 
			 **/ 
			IloNumVar[][] y = new IloNumVar[2][];
			y[0] = cplex.numVarArray(2, 0, 1);

			if (ilpFlag == 0) { // for selecting relaxed ILP
				y[0] = cplex.numVarArray(2, 0, 1);
			} 
			else { 				// for selecting ILP
				y[0] = cplex.boolVarArray(2);             
			}

			/**
			 * Creating Objective Function: 
			 * \sum_{(i,j) \in E^-} w_{ij} (1 - x_{ij}) + \sum_{(i,j) \in E^+} w_{ij} x_{ij}
			 **/ 
		
			IloLinearNumExpr obj = cplex.linearNumExpr();	// linear objective fucntion

			for (int i=1; i<n+1; i++) { 
				for (int j=i+1; j<n+1; j++) {

					if (g.containsEdge(i, j) ){
						// get the non negative edge weight corresponding to edge (i,j)
						double w = g.getEdgeWeight(g.getEdge(i, j));
						double w_abs = Math.abs(g.getEdgeWeight(g.getEdge(i, j)));
						
						// if edge is negative: \sum_{(i,j) \in E^-} w_{ij} (1 - x_{ij})
						if (w < 0){
							obj.addTerm(w_abs, y[0][1]);
							obj.addTerm(-w_abs, x[i][j]);
						}
						// if edge is positive: \sum_{(i,j) \in E^+} w_{ij} x_{ij} 
						if (w >= 0) {
							obj.addTerm(w_abs, x[i][j]);
						}
					}
					else continue;
				}
			}
			
				
			// OBJECTIVE FUNCTION: Define
			cplex.addMinimize(obj);

			// ADD DUMMY CONSTRAINTS: y_{00} = 0; y{01} = 1;
			cplex.addEq(y[0][0], 0);
			cplex.addEq(y[0][1], 1);

			// CONSTRAINTS
			for (int i=1; i<n+1; i++) {
				for (int j=i+1; j<n+1; j++) {
					for (int k=j+1; k<n+1; k++) {

						// Clustering constraints (triangle inequalities)
						// x_{ij} + x_{jk} - x_{ik} >= 0
						IloNumExpr cliqueConst1 = cplex.sum(x[i][j], x[j][k], cplex.prod(-1, x[i][k]));
						cplex.addGe(cliqueConst1, 0);

						// x_{ij} + x_{ik} - x_{jk} >= 0
						IloNumExpr cliqueConst2 = cplex.sum(x[i][j], x[i][k], cplex.prod(-1, x[j][k]));
						cplex.addGe(cliqueConst2, 0);

						// x_{jk} + x_{ik} - x_{ij} >= 0
						IloNumExpr cliqueConst3 = cplex.sum(x[j][k], x[i][k], cplex.prod(-1, x[i][j]));
						cplex.addGe(cliqueConst3, 0);			
					}
				}
			}

			// if ilpFlag = 0, solve the ILP-Relaxed problem
			if (ilpFlag == 0){
				if (cplex.solve()) {
					// Print the objective value of ILP-Relaxed problem
					//System.out.println("ILP-Relaxed Problem Solved.");
					//System.out.println("Objectives:");
					//System.out.println("Total clustering error: " + cplex.getObjValue());


					for (int i=1; i<n+1; i++){
						for (int j=i+1; j<n+1; j++) {
							//System.out.println(i+","+j+"," + cplex.getValue(x[i][j]));

							// Add vertices to the ilpSolGraph
							ilpSolGraph.addVertex(i);
							ilpSolGraph.addVertex(j);

							// Add edge and weight to ilpSolGraph
							DefaultWeightedEdge e = ilpSolGraph.addEdge(i, j);
							Double relaxedEdgeWeight = cplex.getValue(x[i][j]);
							ilpSolGraph.setEdgeWeight(e, relaxedEdgeWeight);
						}
					}
				}
				else System.out.println("Model is not solved.");
			}

			//if ilpflag = 1, solve the ILP problem
			else {   
				if(cplex.solve()) {
					// Print objective value
					//System.out.println("ILP Problem Solved.");
					//System.out.println("Objectives:");
					//System.out.println("Total clustering error: " + cplex.getObjValue());


					// Print clusters steps:
					// 1. Put n vertics into n clusers, i.e. each cluster contains exactly one vertices
					// 2. Now for all 1 <= j<k <=n, if x_{jk} = 0, then put all elements in k's cluster to the j's cluster 
					// 3. Clear all element from k's cluster
					// 4. Print all cluster size > 0
					List<LinkedList<Integer>> clusters = new LinkedList<LinkedList<Integer>>();
					for (int i=0; i<n+1; i++) { //step 1
						LinkedList<Integer> c = new LinkedList<Integer>();
						c.add(i);
						clusters.add(i, c);
					}
					for (int j=1; j<n+1; j++) { //step 2
						for (int k=j+1; k< n+1; k++) {
							if (j!=k && cplex.getValue(x[j][k]) == 0) {
								LinkedList<Integer> c1 = clusters.get(j);
								LinkedList<Integer> c2 = clusters.get(k);
								
								if (!c2.isEmpty()){
									for (int l : c2){
										c1.add(l);
									}
									c2.clear(); //step 3
								}
							}
						}
					}
					
					for (int i=1; i<n+1; i++) { //step 4
						LinkedList<Integer> c3 = clusters.get(i);
						if (c3.size() > 0) {
							for (int j : c3){
								System.out.print(j + ",");
							}
							System.out.println();
						}
					}
					
					
					// Add vertices and edges to the solution graph
					// 	if x_{ij}=0; 1<=i<j<=n, then add an edge (i,j) to the solution graph
					// 	other wise add vertices i & j to the graph if they are not already exist
					for (int i=1; i<n+1; i++) {
						for (int j=i+1; j<n+1; j++) {
							System.out.println("ILP: x["+i+"]["+j+"] = " + cplex.getValue(x[i][j]));
							if (cplex.getValue(x[i][j]) == 0) {
								ilpSolGraph.addVertex(i);
								ilpSolGraph.addVertex(j);
								ilpSolGraph.setEdgeWeight(ilpSolGraph.addEdge(i, j), 0);
							}
							else {
								ilpSolGraph.addVertex(i);
								ilpSolGraph.addVertex(j);
							}
						}
					}
				}
				else {
					System.out.println("Model not solved!!");
				}
				cplex.end();
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return ilpSolGraph;
	}
	
	
	//Main method 
	public static void main(String[] args) {
		//SimpleWeightedGraph<Integer, DefaultWeightedEdge> relaxedWeightedGraph =
				//new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);
		
		// ILP/ILP-Relaxed flag
		int ilpFlag = 0;  // if ilpFlag == 1, then solve ilp
		
		// Read graph data
		UndirectedGraph<Integer, DefaultWeightedEdge> g = ReadGraph_w_edgeList.fromFile("data/sample_data.txt");
		
		// Load data from random generator
		//UndirectedGraph<Integer, org.jgraph.graph.DefaultEdge> g = NestedRandomGraph.getGnpNestedRandomGraph_jGrapht(2, 50, 0.90, 0.10);

		
		// Test Print
		System.out.println("Input Graph: " );
		System.out.println("Nodes: " + g.vertexSet().size());
		System.out.println("Edges: " + g.edgeSet().size());	
		System.out.println(g);
		//int i = 6;
		//int j = 3;
		//System.out.println("Edge weight:" + g.getEdgeWeight(g.getEdge(i, j)));
		
		
		// Output graph
		//String outputGraph = "data/outputGraph_sampl1e.csv";
		
		//Solving ILP-Relaxed
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> relaxedWeightedGraph = CCEditing.ILpRelaxed(g, ilpFlag);
		System.out.println(relaxedWeightedGraph);
		System.out.println(relaxedWeightedGraph.getEdgeWeight(relaxedWeightedGraph.getEdge(1, 3)));
	}
	
}