package algorithm;

import java.util.HashMap;
import java.util.List;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import graphImporter.ReadGraph_w_edgeList;

public class Ultrametric {
	
	/**
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
	 * 
	 * This method implements the Closest Ultrametric Distance matrix algorithm proposed in Krivanek (1988). And finally
	 * returns rounding distance matrix by using given threshold.
	 * Time complexity: O(n^3) + O(n^2)
	 * 
	 * @param inducedWeightedGraph - the weighted graph induced by the solution matrix obtained from relaxed ILP.
	 * @return rounding distance matrix (rounded to 0/1).
	 * 
	 * Using library: jGrapht; available at:  www.jGrapht.org
	 * 
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> 
	getUltrametricDistanceMatrixGraph (SimpleWeightedGraph<Integer, DefaultWeightedEdge> inducedWeightedGraph ) {

		// Create the return graph object
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultrametricGraph = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// number of vertices in the induced weighted graph
		int n = inducedWeightedGraph.vertexSet().size(); 

		/**
		 * STEP 1: Compute minimum spanning tree
		 * Here we use the Kruskal's Minimum Spanning Tree (MST) algorithm (jGrapht package).
		 * 
		 **/
		// Finding Spanning tree using jGrapht
		KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge> spanningTree = 
				new KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge>(inducedWeightedGraph);
		//System.out.println("EdgeSet MST: " + spanningTree.getMinimumSpanningTreeEdgeSet());

		// Create a weighted version of minimmum spanning tree:
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> weightedMST = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// Add vertices and edge to the weighted minimum spanning tree 
		for (DefaultWeightedEdge e : spanningTree.getMinimumSpanningTreeEdgeSet()) {	//for-loop-1
			int src = inducedWeightedGraph.getEdgeSource(e);
			int trg = inducedWeightedGraph.getEdgeTarget(e);
			double weight = inducedWeightedGraph.getEdgeWeight(e);
			//System.out.println("src: " + src + "; trg: " + trg + "; w("+src+","+ trg+"): " + weight);

			//add vertices and weighted edge
			weightedMST.addVertex(src);
			weightedMST.addVertex(trg);
			if (weight >= 0) {
				weightedMST.setEdgeWeight(weightedMST.addEdge(src, trg), weight);
			}
			else {
				weightedMST.setEdgeWeight(weightedMST.addEdge(src, trg), 0);
			}

		}	//end-for-loop-1
		// Print the weightedMST
		//System.out.println("Weighted Spanning Tree: " + weightedMST);

		/**
		 * STEP 2: Compute valuation w': E(T)--> R0+ such that 
		 * 		   w'(e) = max { w(x,y); w(e) = max { w(f); f in T(x,y)}}.
		 * Here E(T) is the edge set of the weightedMinSpanning tree T. 
		 **/
		//Create an two dimension array for storing w'(e) as HashMap<Edge, w'(e)>
		HashMap<DefaultWeightedEdge, Double> wPr = new HashMap<DefaultWeightedEdge, Double>();
		double wPr_f = 0;
		
		for (int x=1; x<=n; x++){
			for (int y=x+1; y<=n; y++){
				// Check the edge (x,y) does not exist in E(T)
				if (!weightedMST.containsEdge(x, y)|| !weightedMST.containsEdge(y, x)) {
					
					//T(x,y): the path edges between the nodes (x,y) using DijkstraShoteshPath algorithm (jGrapht)
					List<DefaultWeightedEdge> path_T_xy = 
							new DijkstraShortestPath<Integer, DefaultWeightedEdge>
					(weightedMST, x, y).getPathEdgeList();
					
					double wPr_xy = 0;
					
					for (DefaultWeightedEdge e : path_T_xy){
						// Get edge weight of e in weithedMST
						double w_e  = weightedMST.getEdgeWeight(e);
						
						// Get wPr_xy = max {w_e; e \in path_T_xy}
						if (wPr_xy < w_e){
							wPr_xy = w_e;
						}
					}
					// Store the value of wPr_xy in wPr
					wPr.put(inducedWeightedGraph.getEdge(x, y), wPr_xy);
					
					// If wPr_f < wPr_xy then 
					if (wPr_f < wPr_xy) {
						wPr_f = wPr_xy;
					}
				}
			}
		}
		//System.out.println("wPr_f: " + wPr_f);
		
		for (DefaultWeightedEdge e : weightedMST.edgeSet()){
			double wPr_e = weightedMST.getEdgeWeight(e);
			//wPr.put(e, wPr_e);
			if (wPr_e < wPr_f){
				wPr.put(e, wPr_f);
				//System.out.println(e + ": "+ wPr_f);
			}
			else {
				wPr.put(e, wPr_e);
				//System.out.println(e + ": "+ wPr_e);
			}
		}
		
		/**
		 * STEP 3: Finding Ultrametric
		 * u*(x,y) = 0.5 * max {w'(e) + w(e) ; e in T(x,y)} 
		 **/

		for (int x = 1; x <= n; x++) { 				//for-loop-6
			for (int y = x+1; y <= n; y++) {			//for-loop-7
				double u_star_xy = 0.0;

				// T(x,y): the path edges between the nodes (x,y) using DijkstraShoteshPath algorithm (jGrapht)
				List<DefaultWeightedEdge> path_T_xy = 
						new DijkstraShortestPath<Integer, DefaultWeightedEdge>
				(weightedMST, x, y).getPathEdgeList();

				//Iterating for each edge in T(x,y)
				for (DefaultWeightedEdge e : path_T_xy) {		//for-loop-8

					//Getting edge weight w(e)
					double w_e = inducedWeightedGraph.getEdgeWeight(e);
					//System.out.println("e = " + e + "; w_e("+ e + ") = " + w_e);
					
					//Getting valuation for edge e, i.e. w'(e)
					double wPr_e = wPr.get(e);
	
					//Calculating u(x,y) = 0.5 * (w_e + wPr_e)
					double u_xy = 0.500 * (w_e + wPr_e);
					//System.out.println("wPr_e ("+ e + ")= " + wPr_e + "; u_xy(" + e + ") = " + u_xy);

					//Checking max for u*(x,y) = max {u(x,y)}
					if (u_star_xy < u_xy) {
						u_star_xy = u_xy;
					}
					else continue;

				} //end-for-loop-8
				//System.out.println("u*_xy = " + u_star_xy +"; w_xy = " + u_star_xy );

				//add vertices and edge to ultrametricRoundGraph
				ultrametricGraph.addVertex(x);
				ultrametricGraph.addVertex(y);

				//ultrametricRoundGraph.addEdge(x, y);
				ultrametricGraph.setEdgeWeight(ultrametricGraph.addEdge(x, y), u_star_xy);
			} //end-for-loop-7

		} //end-for-loop-6
		return ultrametricGraph ;
	} 
	

	// Main method
	public static void main (String[] args){

		String outputFile = "data/sample_data3.txt";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ReadGraph_w_edgeList.fromFile(outputFile);
		System.out.println(g);
		int n = g.vertexSet().size();

		for (int i=1; i<=n; i++){
			for (int j=i+1; j<=n; j++){
				if (g.containsEdge(i, j)){
					System.out.println("(" + i +", " + j + "): " + g.getEdgeWeight(g.getEdge(i, j)));
				}
				else if (g.containsEdge(j, i)) {
					System.out.println("(" + j +", " + i + "): " + g.getEdgeWeight(g.getEdge(j, i)));
				}
				else continue;
			}
		}

		// Ultrametric graph
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> ulG = getUltrametricDistanceMatrixGraph(g);
		
		int m = ulG.vertexSet().size();
		for (int i=1; i<=m; i++){
			for (int j=i+1; j<=m; j++){
				if (ulG.containsEdge(i, j)){
					System.out.println("ul (" + i +", " + j + "): " + ulG.getEdgeWeight(ulG.getEdge(i, j)));
				}
				else if (ulG.containsEdge(j, i)) {
					System.out.println("ul (" + j +", " + i + "): " + ulG.getEdgeWeight(ulG.getEdge(j, i)));
				}
				else continue;
			}
		}
	}
}
