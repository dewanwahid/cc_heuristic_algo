package graphImporter;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ReadGraph_w_edgeList {
	
	/***
	 * Read Weighted Graph from csv or txt file (edge list and separated by comma, tab or spaces)
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
	 * @date September 14, 2016
	 * 
	 * @input Graph in edge list (separated by comma, tab or spaces)
	 * @return G<V, E>, where V = the vertices set, and E = the weighted edge set.
	 * 
	 * Using library: jGrapht; available at: www.jGrapht.org
	 * 
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> fromFile(String filename){

		System.out.println("Weighted Graph Name: " + filename);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		FileInputStream f = null;

		try {
			f = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("resource")
		Scanner s = new Scanner(f);

		while(s.hasNext()){
			String graphDataLine = s.nextLine();

			// avoid to read starting comments of data
			if (graphDataLine.charAt(0) == '#') {
				continue;
			}

			// split the line 
			//String[] line = graphDataLine.split("\t");
			String[] line = graphDataLine.split(",");

			// all data line should have 3 columns 
			if (line.length != 3) {
				System.out.println("Critical read error. Found line:\n"  + graphDataLine);
				System.exit(0);
			}

			//add vertices of the graph
			int src = Integer.parseInt(line[0]); 
			g.addVertex(src);

			int target = Integer.parseInt(line[1]); 
			g.addVertex(target);

			//add edge and edgeWeight to the graph
			double weight = Double.parseDouble(line[2]);
			g.setEdgeWeight(g.addEdge(src, target), weight);
		}
		return g;
	}
	
	//Main method
	public static void main(String[] args){
		String outputFile = "data/sample_data.txt";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ReadGraph_w_edgeList.fromFile(outputFile);
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
	}

}
