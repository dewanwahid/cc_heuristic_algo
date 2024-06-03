package graphImporter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ImportTradeData {
	
	/**
	 * Import Trade Data
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
	 * 
	 * Using library: jGrapht; available at:  www.jGrapht.org
	 * 
	 */

	public static HashMap<Integer, String> ID_print = new HashMap<Integer, String>(); 


	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> readAverageTradeGrowth(String inputFile) {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		FileInputStream f = null;
		int line = 0; 

		//data reading
		try {
			f = new FileInputStream (inputFile);
			Scanner s = new Scanner (f);

			//reading data
			while (s.hasNext()) {

				//Reading lines
				String dataline = s.nextLine();
				line = line + 1;
				//System.out.println("line: " + line);
				
				//if line is empty, then continue
				if (dataline.equals("")) continue;

				//if the first character is "#" write the info line continue
				if (Character.toString(dataline.charAt(0)).equals("#")) {
					continue;
				}

				//split the data line at ',' 
				String[] lineParts = dataline.split(",");

				//if lineParts lenght is not 3, then there is error in data line
				if (lineParts.length < 3 && lineParts.length > 3) {
					System.out.println("Error in line: " + line);
					break;
				}
//				//if first line part is "ID", then it is a country ID line
//				if (lineParts.length == 2){
//					int id = Integer.parseInt(lineParts[0].trim());
//					String countryName = lineParts[1].trim();
//					ID_print.put(id, countryName);
//				}

				//this is a graph data line
				else if (lineParts.length == 3){

					//if the line is giving country ID
					//else the line is giving edge between two countries
					if (lineParts[0].trim().equals("ID")) {
						int id = Integer.parseInt(lineParts[1].trim());
						String countryName = lineParts[2].trim();
						ID_print.put(id, countryName);
					}
					else {
						//add vertices of the graph
						int src = Integer.parseInt(lineParts[0].trim()); 
						g.addVertex(src);

						int target = Integer.parseInt(lineParts[1].trim()); 
						g.addVertex(target);

						//add edge and edgeWeight to the graph
						double weight = Double.parseDouble(lineParts[2]);
						g.setEdgeWeight(g.addEdge(src, target), weight);
					}
				}
				else System.out.println("Error in line: " + line);
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return g;	
	}

	public static void main (String [] args) {
		String inputFile = "data/AverageGrowth.v.1.2.clean.csv";
		//String outputFile = "data/sampleTradeData_clean.csv";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = readAverageTradeGrowth(inputFile);
		
		System.out.println(ID_print.get(231));

		int n = g.vertexSet().size();

//		for (int i=1; i<=n; i++){
//			for (int j=i+1; j<=n; j++){
//				if (g.containsEdge(i, j)){
//					System.out.println("(" + i +", " + j + "): " + g.getEdgeWeight(g.getEdge(i, j)));
//				}
//				else if (g.containsEdge(j, i)) {
//					System.out.println("(" + j +", " + i + "): " + g.getEdgeWeight(g.getEdge(j, i)));
//				}
//				else continue;
//			}
//		}
		
		System.out.println(n);
	}

}
