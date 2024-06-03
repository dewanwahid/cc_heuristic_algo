package algorithm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graphImporter.ReadGraph_w_edgeList;
import randomSignedGraphGenerator.NestedSignedRandomGraphV1;
import randomSignedGraphGenerator.RandomSignedGraph;

/**
 * 
 * Heuristic Algorithm for Correlation Clustering Problems
 * @author Dewan Ferdous Wahid
 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
 * @date September 23, 2016
 * 
 **/

public class HeuristicCCEditing {
	
	public static List<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
	public static double imbP;
	public static String clustersOutput = "data/clustersOutput.csv";

	/**
	 * Main method
	 * Use only one graph input method, either 01, or 02 or 03
	 * 
	 **/
	public static void main(String[] args) {

		// Given threshold
		double threshold = 0.49;

		// Read graph data
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ReadGraph_w_edgeList.fromFile("data/sample_data.txt");
				//NestedSignedRandomGraphV1.getGnpNestedGraph(2, 50, 0.5, 0.1);
				
		//ImportTradeData.readAverageTradeGrowth("data/sampleTradeGrowthData.csv");
		//ImportTradeData.readAverageTradeGrowth("data/AverageGrowth.v.1.2.clean.csv");

		// Solve heuristic CCEditing algorithm
		solveCCEditing(g, threshold);
	}

	/**
	 * Solving Correlation Clustering Edition Problems
	 * 
	 * @param g - undirected, signed weighted graph
	 * @param k - the number of spanning tree calculated from the relaxed solution process
	 * @param threshold - the rounding threshold 
	 * @return finalUltrametric - a clustered graph
	 * 
	 * Using library: jGrapht; available at:  www.jGrapht.org
	 * 
	 **/
	public static void solveCCEditing(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g, 
			double threshold
			) {

		//	public static void solveClusteringEditing(UndirectedGraph<Integer, org.jgraph.graph.DefaultEdge> g,
		//				int groupNum, int nodesNum, double threshold, double p, double r) {

		/**
		 * STEP 0: Read/Load Input Graph
		 **/
		//from data file
		//UndirectedGraph<Integer, DefaultWeightedEdge> g = ReadGraph_uw_edgeList.fromFile("data/sample_data.txt");		

		//TEST PRINT: Input Graph's Basic Descriptions 
		//System.out.println("INPUT GRAPH:");
		//System.out.println("|V|= " + g.vertexSet().size() + "; |E| = " + n);

		/**
		 * STEP 1: (a) Solve the ILP Relaxed formulation of the CORRELATON CLUSTERING EDITING problem
		 * 		   (b) Save the solution as a distance matrix, i.e. as a induced weighted graph G(V,E, w) 
		 * 			   'inducedWeightedGraph'.
		 * Here we use 'IBM Cplex V.12.1'  solver package to solve the linear programming problem
		 **/	
		int ilpFlag = 0; // flag for solving Relaxed-ILP problem

		// Solving ILP-Relaxed and getting the induced weighted graph induced by the relaxed solution matrix (X_R)
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> inducedWeightedGraph = CCEditing.ILpRelaxed(g, ilpFlag);

		/**
		 * STEP 2: Find the Utrametrix distance matrix for inducedWeightedGraph
		 **/
		// Finding ultrametric graph 
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultraGraph = 
				Ultrametric.getUltrametricDistanceMatrixGraph(inducedWeightedGraph);


		/**
		 * Active this step only you want to print and write the final clusters
		 * The clusters output at "data/
		 * STEP 3: 	(a) Rounding ultrametric based on the given threshold
		 * 			(b) Return cluster graph 'finalUltametric' and clusters list
		 **/
		//getAndPrintClusters(ultraGraph, threshold);

		/**
		 * Print the clustering disarrement 
		 **/
		//System.out.println("Disagreements : "+ getClusteringDisaggrements(ultraGraph, g, threshold));


		/**
		 * Active this part only when running AlgorithmBenchMarking.java
		 * 
		 * STEP 3: (a) Rounding ultrametric based on the given threshold 
		 * 		   (b) Return clustered graph 'finalUltrametric' and clusters list
		 **/	
		getClusteredGraph(ultraGraph, threshold);



		/**
		 * Benchmarking: Cluster coloring
		 * 
		 **/
		/*
		int m = 2;
		int n = 50;
		 //Coloring the planted groups nodes
		HashMap<Integer, Integer> color = new HashMap<Integer, Integer>();
		for (int cl=1; cl<=m+1; cl++){
			for (int v=(cl-1)*n + 1; v<=(m*n); v++){
				color.put(v, cl);
			}
		}
		System.out.println("color: " + color);

		// List all non empty cluster and then sorting according their size 
		int cSize = clusters.size();
		HashMap<Integer, ArrayList<Integer>> clusterList = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> clusterSizeList = new ArrayList<Integer>();

		int c_flag = 1; // # of nonempty clusters
		for (int l=1; l<=cSize-1; l++) {
			int c = clusters.get(l).size(); // cluster size

			// if nonempty cluster
			if (c > 0) {
				clusterList.put(c_flag, clusters.get(l));
				c_flag += 1;
				clusterSizeList.add(c);
			}
		}

		// Shoring
		Collections.sort(clusterSizeList);
		Collections.reverse(clusterSizeList);
		System.out.println(clusterList);
		System.out.println(clusterList.size());

		// Assign color of each nodes in individual cluster
		double p = 0.5; double r = 0.1;
		HashMap<Integer, Integer> colorLater = new HashMap<Integer, Integer>();
		int cListSize = clusterList.size();
		System.out.print(p + " \t\t " + r + " \t\t ");
		for (int c=1; c<=cListSize ; c++){
			System.out.print(clusterList.get(c).size() + " \t\t ");
			// Select the cluster for checking 
			ArrayList<Integer> cluster = clusterList.get(c);
			int s = cluster.size();
			//System.out.println("c: " + c + "; " + cluster );

			// Get the color of an element of the cluster
			int v = cluster.get(0);  // select a node fror the selected cluster
			//System.out.println("gr: " + c + "; v: " + v); 
			int col = color.get(v);  // get the original color of this node

			// Color all nodes in this selected cluster with 'col'
			for (int i=0; i<s; i++){
				int u = cluster.get(i);
				colorLater.put(u, col);
			}
		}
		System.out.println();
		System.out.println(colorLater);

		// Calculating imbalance
				int imb = 0;
				for (int i=1; i<=(m*n); i++){
					if (color.containsKey(i) && colorLater.containsKey(i)) {
						//System.out.println("i: " + i + "; c: " + color.get(i) + "; cL: " + colorLater.get(i));
					if (color.get(i)!=colorLater.get(i)){
							imb+=1;
						}
					}
					else if (color.containsKey(i) && !colorLater.containsKey(i)){
						//System.out.println("i: " + i + "; c: " + color.get(i));
						imb+=1;
					}
				}
		System.out.println("Imb: " + imb);

		// Imbalance in percentage
				double p1 = m*n;
				double imbPer = (imb / p1) * 100;
				System.out.println(p + " \t\t " + r + " \t\t " + imbPer);
				//System.out.println(threshold + " \t\t " + imbPer);
				
*/
	}


	public static double getClusteringDisaggrements(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultraGraph,
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g,
			double threshold
			) {

		int n = g.vertexSet().size();
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> clusteredGraph = 
				getClusteredGraph(ultraGraph, threshold);

		int disAgree = 0;

		for (int i=1; i<=n; i++){
			for (int j=i+1; j<=n; j++){
				double w = 0;
				if (g.containsEdge(i, j)){
					w = g.getEdgeWeight(g.getEdge(i, j));
				}
				else if (g.containsEdge(j, i)){
					w = g.getEdgeWeight(g.getEdge(j, i));
				}
				else continue;

				double x = 1;
				if (clusteredGraph.containsEdge(i, j)){
					x = clusteredGraph.getEdgeWeight(clusteredGraph.getEdge(i, j));
				}
				else if (clusteredGraph.containsEdge(j, i)){
					x = clusteredGraph.getEdgeWeight(clusteredGraph.getEdge(j, i));
				}
				else continue;
				//System.out.println(i + "," +j + ": " + x );

				if (w < 0) {
					disAgree += (Math.abs(w) * (1-x));
				}
				else {
					disAgree += (Math.abs(w) * x);
				}
			}
		}

		return disAgree;
	}


	/**
	 * Method for geting cluster graph
	 **/
	@SuppressWarnings("unused")
	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> getClusteredGraph(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultraGraph,
			double threshold
			) {
		//Create final ultrametric graph object
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> clusteredGraph = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		for (DefaultWeightedEdge e : ultraGraph.edgeSet()) {

			double edgeWeight = ultraGraph.getEdgeWeight(e);
			int src = ultraGraph.getEdgeSource(e);
			int trg = ultraGraph.getEdgeTarget(e);

			clusteredGraph.addVertex(src); 
			clusteredGraph.addVertex(trg);

			if (edgeWeight <= threshold) {
				clusteredGraph.setEdgeWeight(clusteredGraph.addEdge(src, trg), 0);
			}
			else {
				clusteredGraph.setEdgeWeight(clusteredGraph.addEdge(src, trg), 1);
			}
		}
		return clusteredGraph;
	}


	/**
	 * Method for get, print, and write clusters from the ultrametric graph
	 **/
	private static void getAndPrintClusters(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultrametricGraph, 
			double threshold
			) {

		int n = ultrametricGraph.vertexSet().size();
		
		// Create clusteredGraph object to return final graph
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> clusteredGraph = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// Remove all clusters list from previous calculation
		clusters.clear();

		// Put each vertex to a different cluster
		// Then add these clusters to the clusters list
		for (int i=0; i<=n; i++){
			ArrayList<Integer> c = new ArrayList<Integer>();
			c.add(i);
			clusters.add(i, c);
		}


		// Rounding each edge from the ultrametricGraph
		// Add edge to the clusteredGraph based on the edge rounding
		for (int i=1; i<=n; i++) {
			for (int j=i+1; j<=n; j++) {
				double edgeWeight = 0;

				// If edge exist in the ultrametricGraph then get the edge weight
				if (ultrametricGraph.containsEdge(i, j)) {
					edgeWeight = ultrametricGraph.getEdgeWeight(ultrametricGraph.getEdge(i, j));
					//System.out.println("("+i+","+j+"): "+ edgeWeight);
				}
				else if (ultrametricGraph.containsEdge(j, i)) {
					edgeWeight = ultrametricGraph.getEdgeWeight(ultrametricGraph.getEdge(j, i));
					//System.out.println("("+j+","+i+"): "+ edgeWeight);
				}
				else continue;

				// If edgeWeight less than threshold then: 
				// 		(a) add edge (i,j) to the clusteredGraph,
				// 		(b) get the clusters that contains the edge vertices i & j
				
				clusteredGraph.addVertex(i);
				clusteredGraph.addVertex(j);
				
				if (edgeWeight <= threshold) {
					// Add vertices i & j and edge (i,j) to the clusteredGraph
					clusteredGraph.setEdgeWeight(clusteredGraph.addEdge(i, j), 0);

					// Get the cluster that contains i & j
					ArrayList<Integer> ci = clusters.get(i);	// cluster that contains vertices i
					ArrayList<Integer> cj = clusters.get(j);	// cluster that contains vertices i

					if (!cj.isEmpty()){

						// Put all elements from cj cluster to ci clusters
						for (int l : cj){
							ci.add(l);
						}
						cj.clear(); //clear all elements from cj cluster
					}
				}
			}
		}

		try {
			PrintWriter w = new PrintWriter (clustersOutput);
			int clId = 1;


			// Print and write the clusters from the clusters list if size > 0
			for (int i=1; i<=n; i++) {
				ArrayList<Integer> ci = clusters.get(i);
				if (ci.size() > 0) {	

					int ciN =ci.size();					
					w.println("#Cluster: " + clId);
					clId+=1;
					int temp = 0;

					for (int j : ci){					
						System.out.print(j);
						w.print(j);

						if (temp < ciN-1) {
							System.out.print(",");
							w.print(",");						
						}
						temp+=1;						
					}
					System.out.println(); 
					w.println();
				}
			} 
			w.flush();
			w.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
