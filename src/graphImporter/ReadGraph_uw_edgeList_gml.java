package graphImporter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * Read Graph from gml file format
 * @author Dewan Ferdous Wahid
 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
 * @date September 14, 2016
 * 
 * @input Graph in edge list (gml file format)
 * @return G<V, E>, where V = the vertices set, and E = the edge set.
 * 
 * Using library: jGrapht; available at: www.jGrapht.org
 * 
 ***/

public class ReadGraph_uw_edgeList_gml {

	@SuppressWarnings("resource")
	public static UndirectedGraph<Integer, DefaultEdge> fromFile(String filename) {

		System.out.println("Data Name:" + filename);
		UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge> (DefaultEdge.class);

		FileInputStream f = null; 

		try {

			f = new FileInputStream (filename);
			Scanner s = new Scanner (f);
			int lineTracker = 0;

			while (s.hasNext()) {

				//Keep track the graph data line
				lineTracker = lineTracker + 1; 
				String graphDataLine = s.nextLine();

				//avoid the data name and info
				if (graphDataLine.charAt(0) == '#') continue;

				else {

					//split graph data line
					String[] line = graphDataLine.trim().split("\\s+");

					if (line[0].equals("edge")) continue;
					else if (line[0].equals("[")) continue;
					else if (line[0].equals("]")) continue;
					else if (line[0].equals("source")) {
						int src = Integer.parseInt(line[1]);	
						String graphDataLine3 = s.nextLine();
						lineTracker = lineTracker + 1; 

						//split graph data line
						String[] line3 = graphDataLine3.split("\\s+");

						if (line3[0].equals("target")) {
							int trg = Integer.parseInt(line3[1]);

							//add vertices
							g.addVertex(src+1);
							g.addVertex(trg+1);
							g.addEdge(src+1, trg+1);
						}
						else continue;
					}
					else continue;	
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return g ;
	}

	//Main method
	public static void main(String[] args) {
		UndirectedGraph<Integer, DefaultEdge> g = ReadGraph_uw_edgeList_gml.fromFile("data/sample_data.gml.txt");
		System.out.println("Nodes: " + g.vertexSet().size());
		System.out.println("Edges: " + g.edgeSet().size());

		// Print all edges
		for (DefaultEdge e: g.edgeSet()) {
			System.out.print(g.getEdgeSource(e) + "," + g.getEdgeTarget(e) + "\n");
		}
	}
}
