package randomSignedGraphGenerator;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Random;

import javax.xml.transform.TransformerConfigurationException;

import org.jgrapht.ext.ExportException;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.xml.sax.SAXException;

/**
 * Nested Random Graph Generator
 * @author Dewan Ferdous Wahid
 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
 * 
 * @param m - the number of edges adding each time in a nested group
 * @param groupNum - the number of nested groups in the graph
 * @param nodesNum - the number of nodes in each nestes group
 * 
 * @return G(V,E), V = the set of nodes, E = the set of edges 
 * 
 * Using library: jGrapht; available at:  www.jGrapht.org
 **/

public class NestedSignedRandomGraphV1 {

	private static Random random = new Random();


	/**
	 * Generate a nested random signed graph where each nest is a G(n,p) signed graph with 
	 * @param m - the number of groups/nests in the graph
	 * @param n - the number of vertices in each group/nest
	 * @param p - the conecting two vertices with a positive edge inside the nest (inter-group positive probability)
	 * @param r - intra-groups negative probability
	 * 
	 * @return G(V,E) in UndirectedGraph<V,E> format (jGrapht) 
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> getGnpNestedGraph(
			int m, 
			int n, 
			double p, 
			double r
			) {
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge>  g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);
		
		//Creating nest (group), where each group is a G(n,p) random graph
		for (int group = 1; group <= m; group++) {
			getGnpNest(g, group, n, p);	
		}
		
		//Adding iner-edges between groups
		for (int grI = 1; grI <= m; grI++) {
			for (int grJ = grI + 1; grJ <= m; grJ++) {
				connectGroups(g, grI, grJ, n, r);
			}
		}
		return g;
	}
		
	/**
	 * Generate G(n,p) Nested Random Graph in CSV format
	 * @param m - the number of groups/nests in the graph
	 * @param n - the number of vertices in each group/nest
	 * @param p - the conecting two vertices with a positive edge inside the nest (inter-group positive probability)
	 * @param r - intra-groups negative probability
	 * 
	 * @return G(V,E) in CSV edgelist format 
	 ***/
	public static void getGnpNestedGraph_CSV(
			int m, 
			int n, 
			double p, 
			double r, 
			String outputFile
			) {
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = getGnpNestedGraph(m,n,p,r) ;
		
		try {
			PrintWriter writer = new PrintWriter (outputFile );
			//CSV file data heading
			writer.print("#Source,Target,weight(signed)\n");
			
			for (DefaultWeightedEdge e: g.edgeSet()){
				writer.println(g.getEdgeSource(e) + "," + g.getEdgeTarget(e)+ "," + g.getEdgeWeight(e));
			}

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	/**
	 * Generate G(n,p) Nested Random Signed Graph in GraphML format
	 * @param m - the number of groups/nests in the graph
	 * @param n - the number of vertices in each group/nest
	 * @param p - the conecting two vertices with a positive edge inside the nest (inter-group positive probability)
	 * @param r - intra-groups negative probability
	 * 
	 * @return G(V,E) in GraphML format 
	 * @throws ExportException 
	 **/
	public static void getGnpNestedGraph_GraphML(
			int m, 
			int n, 
			double p, 
			double r, 
			String outputFile
			) throws TransformerConfigurationException, SAXException, ExportException {
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = getGnpNestedGraph(m, n, p,r) ;
		Writer w;
		try {
			GraphMLExporter<Integer, DefaultWeightedEdge> exporter = new GraphMLExporter<Integer, DefaultWeightedEdge>(); 
			w = new FileWriter(outputFile);
			exporter.exportGraph(g, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Connects two nested groups grI and grJ
	 * @param grI - group ID
	 * @param grJ - group ID
	 * @param nodesNum - number of nodes contains in each group
	 * @param r - the probability of connecting two nodes from different nested groups
	 ***/

	private static void connectGroups(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g,
			int grI, 
			int grJ,
			int n,
			double r
			) {

		//Iterating through all nodes in group I (grI)
		for (int i = (grI-1)*n + 1; i <= grI * n; i++) {
			//Iterating through all nodes in group J (grJ)
			for (int j = (grJ-1)*n + 1; j <= grJ * n; j++) {
				if (i == j) {
					continue;
				}
				else if (g.containsEdge(i, j) || g.containsEdge(j, i)) {
					continue;
				}
				else {
					double randomNumber = random.nextDouble();
					if (randomNumber <= r) {
						g.setEdgeWeight(g.addEdge(i, j), -1);
					}
					else {
						g.setEdgeWeight(g.addEdge(i, j), 1);
					}
				}
			}
		}
	}
	
	
	/**
	 * Create a G(n,p) Random nest graph 
	 * @param g - the nested random graph in which this group will add
	 * @param nodeNum - total number nodes
	 * @param groupNum - the group ID in the nested graph
	 * @param p  - the probability for a existing node connects with new node
	 **/
	private static void getGnpNest(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g, 
			int group, 
			int n, 
			double p
			){
		
		//Add all nodes to the graph
		for (int j = (group-1)*n + 1; j <= group*n; j++ ) {
			g.addVertex(j);
		}
		
		//Add edge between two nodes in the group
		for (int u = (group-1)*n + 1; u <= group*n; u++) {
			for (int v = u+1; v <= group*n; v++) {
				//If radom indicates the edge probability exist
				double rnd =  random.nextDouble();
				
				if (rnd <= p) {
					g.setEdgeWeight(g.addEdge(u, v), 1);
				}
				else {
					g.setEdgeWeight(g.addEdge(u, v), -1);
				}
			}
		}
	}

	// Main Method
	public static void main (String [] args) {
		int m = 10;
		int n = 50;
		double p = 0.50;
		double r = 0.50;

		String outputFile = "data/nested_rnd_graph.csv";
		
		//SimpleGraph<Integer, DefaultEdge> g = getBarabasiAlbertNestedRandomGraph_jGrapht(m, groupNum, nodesNum, p, r);
		//SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = getGnpNestedGraph(m, n, p, r);
		
		
		// Generate graph in csv format
		//getBarabsiAlbertNestedRandomGraph_CSV(m, groupNum, nodesNum, p, r, outputFile);
	
		
		// G(m,n, p, r, outputFile);
		getGnpNestedGraph_CSV(m,n,p, r, outputFile);
		
		//Print graph information
		//System.out.println("Nodes: " + g.vertexSet().size());
		//System.out.println("Edge: " + g.edgeSet().size());
		System.out.println("Inter-group postive edge probability p = " + p);
		System.out.println("Intra-groups negative edge probability r = " + r);
		
		
	}

}
