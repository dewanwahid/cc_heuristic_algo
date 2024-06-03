package randomSignedGraphGenerator;

import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class RandomSignedGraph {
	
	/**
	 * Signed Random Graph G(n,e,p) Generator
	 *@author Dewan Ferdous Wahid
	 *@affiliation Dr. Yong's Gao Research Group, Computer Science, UBC-Okanagan
	 *
	 **/
	
	private static Random random = new Random();
	
	/**
	 * Signed Random Graph G(n,e,p) Generator
	 * 
	 * @param n - number of vertices in the signed graph
	 * @param e - probability of edge between two vertices
	 * @param p - if there is an edge, then the probability of creating positive edge
	 * 				
	 * Note: Thus the probability of creating a positive edge is e*p and the probability of 
	 * 		 creating a negative edge is e*(1-p).
	 * 
	 * @return g - SimpleWeightedGrah (jGrapht) in which edge weight is either positive/negative signed
	 * 
	 * Dependency: jGrapht graph package from 'http://jgrapht.org/'
	 **/


	public static SimpleWeightedGraph<Integer,  DefaultWeightedEdge> getGnpGraph_jGrapht(
			int n, 
			double e, 
			double p
			) {
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//Add all nodes to the graph
		for (int i=1; i<=n; i++) {
			g.addVertex(i);
		}

		//Add edge between two nodes in the group
		for (int u = 1; u <= n; u++) {
			for (int v = u+1; v <= n; v++) {
				double rnd =  random.nextDouble();
				//if there is an edge 
				if (rnd <= e) {
					
					double posPro = random.nextDouble();
					
					//uniformly create positive/negative edge
					if (posPro <= p) {
						g.setEdgeWeight(g.addEdge(u, v), 1);
						//System.out.println(u +","+v+","+1);
					}
					else {
						g.setEdgeWeight(g.addEdge(u, v), -1);
						//System.out.println(u +","+v+","+-1);
					}		
				}
				else continue;
			}
		}
		return g ;
	}
	
	// Main method
	public static void main(String[] args) {
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = getGnpGraph_jGrapht(10, 0.3, 0.3);
		System.out.println("edges: "+ g.edgeSet().size());

	}

}
