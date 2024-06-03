package graphImporter;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class TestClass {
	
	/**
	 * Testing Class
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan
	 * 
	 * Using library: jGrapht; available at:  www.jGrapht.org
	 * 
	 */
	
	public static void main (String[] args){
		ImportTradeData.ID_print.clear();
	
		String inputFile = "data/sampleTradeData_clean.csv";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ImportTradeData.readAverageTradeGrowth(inputFile);
		
		System.out.println(g);
		
		System.out.println(ImportTradeData.ID_print.get(2));
		
		
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
