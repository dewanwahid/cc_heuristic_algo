package algorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import randomSignedGraphGenerator.RandomSignedGraph;

public class AlgorithmBenchMarking {
	
	/**
	 * @author Dewan Ferdous Wahid
	 * @affiliation Dr. Yong Gao's Research Group, Computer Science, UBC Okanagan 
	 * @date October 12, 2016
	 * 
	 **/
		
	public static void main (String[] args) {
		
		/**
		 * Runtime Benchmarking for varing n (size) in the signed random networks G(n,e,p) size
		 * @param n - the size of the network
		 * @param e - the probability of creating an edge (fixed)
		 * @param p - the probability of that edge is positive, otherwise it is negative (fixed)
		 */
		
		int nStart = 10;	// staring nodes number
		int nStop = 300;	// stoping number of nodes
		double threshold = 0.5; 
		double e;		// edge probability
		double p; 	// positive edge probability	
		
		System.out.println("\ne = 0.1; p = 0.1");
		e = 0.1; p = 0.1;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.3; p = 0.3");
		e = 0.3; p = 0.3;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.5; p = 0.3");
		e = 0.5; p = 0.3;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.5; p = 0.5");
		e = 0.5; p = 0.5;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.7; p = 0.3");
		e = 0.7; p = 0.3;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.7; p = 0.7");
		e = 0.7; p = 0.7;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.9; p = 0.3");
		e = 0.9; p = 0.3;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.9; p = 0.5");
		e = 0.9; p = 0.5;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		System.out.println("\ne = 0.9; p = 0.9");
		e = 0.9; p = 0.9;
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p, threshold);
		
		/**
		 * 
		 * Runtime Benchmarking in the signed random networks G(n,e,p) size
		 * @param n - the size of the network (fixed)
		 * @param e - the probability of creating an edge (variying)
		 * @param p - the probability of that edge is positive, otherwise it is negative (fixed)
		 */
		/*
		int n = 100; 		// fixed 
		double eStart = 0;  // stating e
		double eStop = 1;   // stoping e
		double p = 0.7;		// fixed
		double threshold = 0.5;
		getRuntimeBenchmarkFor_e(n, eStart, eStop, p, threshold);
		*/
		
		/**
		 * Runtime Benchmarking in the signed random networks G(n,e,p) size
		 * @param n - the size of the network (fixed)
		 * @param e - the probability of creating an edge (fixed)
		 * @param p - the probability of that edge is positive, otherwise it is negative (variying)
		 */
		/*
		int n = 50; 			// fixed 
		double e = 0.7;  		// fixed
		double pStart = 0;  	// starting p
		double pStop = 1;		// stoping p
		double threshold = 0.5;
		getRuntimeBenchmarkFor_p(n, e, pStart, pStop, threshold);
		*/
		
		/**
		 * Disagreement Benchmarking when threshold variying 0 to 1.
		 **/
		/*
		for (int i=0; i<10; i++){
			int n = 50;
			double e = 0.5;
			double p = 0.5;
			
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g =
					randomSignedGraphGenerator.RandomSignedGraph.getGnpGraph_jGrapht(n, e, p);
			
			int ilpFlag = 0; 
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> inducedWeightedGraph = CCEditing.ILpRelaxed(g, ilpFlag);
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> ultraGraph = 
					Ultrametric.getUltrametricDistanceMatrixGraph(inducedWeightedGraph);
			for (double t=0; t<1; t+=0.1){
				double error = HeuristicCCEditing.getClusteringDisaggrements(ultraGraph, g, t);
				System.out.println(t + "\t\t" + error);
			}
			System.out.println("..........................................................");
		}*/

	}

	@SuppressWarnings("unused")
	private static void getRuntimeBenchmarkFor_p(
			int n, 
			double e, 
			double pStart, 
			double pStop, 
			double threshold
			) {
		
		System.out.println("p\t\t" + "Time (sec)");	
		for (double e1=0.1; e<=1; e1+=0.2) {
			for (double p=pStart; p<=pStop; p+=0.1) {
				double startTime = System.nanoTime(); 
				SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = RandomSignedGraph.getGnpGraph_jGrapht(n, e, p);
				HeuristicCCEditing.solveCCEditing(g, threshold);
				double endTime = System.nanoTime();

				double duration = (endTime - startTime) / 1000000000 ;/// 1000000 ;
				System.out.println(p+ "\t\t" + duration);
			}
			System.out.println("e = " + e1 +"\n");
		}
	}

	@SuppressWarnings("unused")
	private static void getRuntimeBenchmarkFor_e(
			int n, 
			double eStart,
			double eStop,
			double p, 
			double threshold
			) {
		System.out.println("e\t\t" + "Time (sec)");		
		for (double p1=0.1; p1<=1; p1+=0.2) {
			for (double e=eStart; e<=eStop; e+=0.05) {
				double startTime = System.nanoTime(); 
				SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = RandomSignedGraph.getGnpGraph_jGrapht(n, e, p1);
				HeuristicCCEditing.solveCCEditing(g, threshold);
				double endTime = System.nanoTime();

				double duration = (endTime - startTime) / 1000000000 ;/// 1000000 ;
				System.out.println(e+ "\t\t" + duration);
			}
			System.out.println("p = " + p1 +"\n");
		}
	}

	@SuppressWarnings("unused")
	private static void getRuntimeBenchmarkFor_N (
			int nStart, 
			int nStop,
			double e, 
			double p, 
			double threshold
			) {
		
		System.out.println("|V|\t\t" + "Time (sec)");
		for (int n=nStart; n<=nStop; n+=nStart) {
			
			double startTime = System.nanoTime(); 
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = RandomSignedGraph.getGnpGraph_jGrapht(n, e, p);
			HeuristicCCEditing.solveCCEditing(g, threshold);
			double endTime = System.nanoTime();
			
			double duration = (endTime - startTime) / 1000000000 ;/// 1000000 ;
			System.out.println(n+ "\t\t" + duration);
		}
	}

}
