package randomSignedGraphGenerator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class NestedSignedRandomGraphV2 {
	
	/**
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
	 * 
	 **/

	private static Random random = new Random();

	/**
	 * Nested Random Signed Weighted Graph Generator G(m, n, p, r, pNoise, rNoise)
	 *
	 * @param m - the number of groups in the graph
	 * @param n - the number of vertices in each group
	 * @param p - the probability of creating positive edge inside a group
	 * @param r - the probability of creating negative edge between two groups
	 * @param pNoise - the porbability of adding nosie inside a group (i.e. adding negative edges inside a group/nest)
	 * @param rNoise - the probability of adding nosie between two groups (i.e. adding positive edges between two groups)	- 
	 *
	 * @return A signed weighted graph G(V,E) in which a signed weight associated with an edge
	 * 
	 * @throws FileNotFoundException 
	 * 
	 * Using library: jGrapht; available at:  www.jGrapht.org
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> 
	getJGrpahtGraph(
			int m, 
			int n, 
			double p, 
			double r, 
			double pNoise, 
			double rNoise
			)  {

		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//Creating m nest (group), where each group is a G(n,p) random graph
		for (int grId = 1; grId <= m; grId++) {
			getNest(g, grId, n, p, pNoise);	
		}
		
		//Adding negative edges between two groups and nosie(positive edges)
		for (int grI = 1; grI <= m; grI++) {
			for (int grJ = grI + 1; grJ <= m; grJ++) {
				connectGroups(g, grI, grJ, n, r, rNoise);
			}
		}

		return g ;
	}
	
	/**
	 * Connect two groups (nests) by adding negative edges with the probability r
	 * and adding noises (positive edges) with the probability rNoise
	 * 
	 * @param g - the nested signed graph
	 * @param grI - group/nest ID
	 * @param grJ - group/nest ID
	 * @param n - the number of vertices in each group
	 * @param r - the probability of adding negative edge between two groups 
	 * @param rNoise - the probability of adding noise (positive edge) between two edges
	 **/

	private static void connectGroups(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g,
			int grI, 
			int grJ, 
			int n,
			double r, 
			double rNoise) {
		
		//iterating through all nodes in group I (grI)
		for (int i=(grI-1)*n+1; i <= grI*n; i++) {
			//iterating through all nodes in group J (grJ)
			for (int j=(grJ-1)*n+1; j <= grJ * n; j++) {
				
				double rnd1 = random.nextDouble();
				double rnd2 = random.nextDouble();
				
				//ading a negative edge (i,j)
				if (i == j) {
					continue;
				}
				else if (!g.containsEdge(i, j) || !g.containsEdge(j, i)) {	
					
					if (rnd1 <= r) {
						g.setEdgeWeight(g.addEdge(i, j), -1);
					}
				}
				else continue;
				
				//adding noise 
				if (rnd2 < rNoise) {
					//altering edge sign if there exist already an edge (i,j)
					//otherwise add a negative edge(i,j) 
					if (g.containsEdge(i, j) || g.containsEdge(j, i)) {
						g.setEdgeWeight(g.getEdge(i, j), 1);
					}
					else {
						g.setEdgeWeight(g.addEdge(i, j), 1);
					}
				}
			}
		}
	}

	/**
	 * Create a G(n,p,pNosie) Random Signed Nest Graph 
	 * @param g - the nested random graph in which this group will add
	 * @param grId - the group Id in the nested graph
	 * @param p  - the probability of connecting two vertices with a positive edge
	 * @param pNoise - the pobability adding noise inside the nest (i.e. adding negative edge)
	 * 
	 * Adding noise inside a group: - select randomly two vertices from the group/nest
	 * 								- if there already exist an edge (positive) between the vertices, altered the sign
	 * 								- if there does not exist any edge between the vertices, add a negative edge
	 * @throws FileNotFoundException 
	 **/
	private static void getNest(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g, 
			int grId, 
			int n, 
			double p, 
			double pNoise
			) {

		//Add all nodes to the graph
		for (int j = (grId-1)*n+1; j<=grId*n; j++ ) {
			g.addVertex(j);
		}

		//add positive edge between two vertics in the group with the probability p
		//add negative edge (nosie) two vertics in the group with the probability pNosie
		for (int u=(grId-1)*n + 1; u<=grId*n; u++) {
			for (int v=u+1; v<=grId*n; v++) {

				double rnd1 =  random.nextDouble();
				double rnd2 = random.nextDouble();

				//adding a positive edge
				if (rnd1 <= p) {
					g.setEdgeWeight(g.addEdge(u, v), 1);
					//System.out.println(u+","+v+","+1);
				}
				else continue;


				//adding a negative edge (noise)
				if (rnd2 <= pNoise) {
					//if there exist a positive edge (u,v) alter the sign of the edge
					//else add a negative edge (u,v)
					if (g.containsEdge(u, v)) {
						g.setEdgeWeight(g.getEdge(u, v), -1);
						//System.out.println(u+","+v+","+-1);
					}
					else {
						g.setEdgeWeight(g.addEdge(u, v), -1);
						//System.out.println(u+","+v+","+-1);
					}
				}
				else continue;
			}
		}
	}


	/**
	 * Main method
	 * 
	 **/
	public static void main(String[] args) {

		int m = 3;	//# of groups (nest) in the graph
		int n = 5; 	//# of vertice in each group
		double p = 0.9; //probability of creating positive edge 
		double r = 0.5; //probability of crating negative edge
		double pNoise = 0.5; //probability of adding nosie inside a group
		double rNoise = 0.1; //probability of adding nosie between two groups

		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = getJGrpahtGraph(m, n, p, r, pNoise, rNoise);

		//size of the graph
		int N = g.vertexSet().size();
		//System.out.println(g);

		//writing graph in csv file
		String outputFile = "data/NestedSignedRandomGraph.csv";
		try {
			PrintWriter w = new PrintWriter (outputFile);
			w.println("#source,target,weight(signed)");
			for (int i=1; i<=N; i++){
				for (int j=i+1; j<=N; j++){
					if (g.containsEdge(i, j)) {
						w.println(i+","+j+","+ g.getEdgeWeight(g.getEdge(i, j)));
						System.out.println(i+","+j+","+ g.getEdgeWeight(g.getEdge(i, j)));
					}
					else continue;
				}
			}
			w.flush();
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}
}
