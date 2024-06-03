package graphImporter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/***
 * Read Graph from csv or txt file (edge list and separated by comma, tab or spaces)
 * @author Dewan Ferdous Wahid
 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
 * @date September 14, 2016
 * 
 * @input Graph in edge list (separated by comma, tab or spaces)
 * @return G<V, E>, where V = the vertices set, and E = the edge set.
 * 
 * Using library: jGrapht; available at: www.jGrapht.org
 * 
 **/

public class ReadGraph_uw_edgeList {

	@SuppressWarnings("resource")
	public static UndirectedGraph<Integer, DefaultEdge> fromFile(String filename) {

		System.out.println("Data Name:" + filename);
		UndirectedGraph<Integer, DefaultEdge> g = 
				new SimpleGraph<Integer, DefaultEdge> (DefaultEdge.class);
		FileInputStream f = null; 

		try {
			f = new FileInputStream (filename);
			Scanner s = new Scanner (f);
			int lineTracker = 0;

			while(s.hasNext()) {

				lineTracker = lineTracker + 1; //Keep track the graph data line
				String graphDataLine = s.nextLine();

				//avoid the data name and info
				if (graphDataLine.charAt(0) == '#') continue;
				else{

					//split the data line
					//String[] line = graphDataLine.split(",");   // split by comma
					String[] line = graphDataLine.split("\t");   // split by tab
					//String[] line = graphDataLine.split("\\s");   // split by space 
					

					//all data line has 2 column, since it is unweighted graph 
					if (line.length != 2) {
						System.out.println("Error in Graph Data line: " + lineTracker); 
						System.exit(0);
					}
					else {

						//add source node
						int source = Integer.parseInt(line[0]);
						g.addVertex(source);

						//add target node
						int target = Integer.parseInt(line[1]);
						g.addVertex(target);

						//add edge
						g.addEdge(source, target);
						//System.out.println("add Edge (" + source + ", " + target + ")");
					}
				}
			} //end-while

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return g ;
	}

	//Main method
	public static void main(String[] args) {
		UndirectedGraph<Integer, DefaultEdge> g = ReadGraph_uw_edgeList.fromFile("data/companyDivided.txt");
		System.out.println("Nodes: " + g.vertexSet().size());
		System.out.println("Edges: " + g.edgeSet().size());

		// Print all edges
		for (DefaultEdge e: g.edgeSet()) {
			System.out.print(g.getEdgeSource(e) + "," + g.getEdgeTarget(e) + "\n");
		}
	}

}
