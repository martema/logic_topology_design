package ltd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * LTD greedy heuristic
 *
 * @author Cilia Martina
 */

public final class Ltd_manhattan
{
    private Ltd_manhattan(){
    } // ensure non-instantiability.

    /**
     * The starting point for the main
     *
     */
    public static void main(String [] args)
    {
    	int N = 16; /* Number of nodes */
    	int rowManh = 4; /* Manhattan topology grid rows */
    	int colManh = 4; /* Manhattan topology grid columns */
    	 
    	Matrix trafficMatrix; /* Traffic matrix */
    	Matrix greedyMatrix; /* Matrix representing the topology obtained with the greedy heuristic */
    	Matrix metaMatrix; /* Matrix representing the topology obtained with the meta-heuristic */
    	
    	ArrayList<List<DefaultWeightedEdge>> greedyPath; /* Shortest path routing for the topology obtained with the greedy heuristic 
    														placement */ 
    	ArrayList<List<DefaultWeightedEdge>> metaPath; /* Shortest path routing for the topology obtained after the meta-heuristic 
    														algorithm */
    	
    	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> greedyGraph;/* topology obtained with the greedy heuristic placement */
    	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> metaGraph;/* topology obtained after the meta-heuristic algorithm */
    	
    	double greedyFlow; /* Maximum flow for the topology obtained with the greedy heuristic placement */
    	double metaFlow; /* Maximum flow for the topology obtained after the meta-heuristic algorithm */
    	
    	/*Generation of the random traffic matrix*/
    	trafficMatrix = new Matrix(N,N);
        randomTrafficMatrix(trafficMatrix);
        System.out.println("TRAFFIC MATRIX");
        System.out.println(trafficMatrix.toString() + "\n" + "\n");
        
        /*Generation of topology obtained with the greedy heuristic placement */
        greedyMatrix = greedy(trafficMatrix, rowManh, colManh);
        System.out.println("GREEDY HEURISTIC MATRIX:");
        System.out.println(greedyMatrix.toString() + "\n" + "\n");
        greedyGraph = greedyMatrix.matrixToGraph();
        System.out.println("GREEDY HEURISTIC GRAPH:");
        System.out.println(greedyGraph.toString() + "\n" + "\n");
        
        /*Traffic routing using shortest path algorithm for the topology obtained with the greedy heuristic placement */
        greedyPath = routing(greedyGraph);
        
        /*Maximum flow computation for the topology obtained with the greedy heuristic placement */
    	greedyFlow = maximumFlow(greedyGraph, greedyPath, trafficMatrix, N);
        System.out.println("GREEDY HEURISTIC MAXIMUM FLOW:");
        System.out.println(greedyFlow + "\n" + "\n");

        /*Generation of topology obtained after the meta-heuristic algorithm */
        metaMatrix = metaHeuristic(greedyMatrix,trafficMatrix,N);
        System.out.println("META-HEURISTIC MATRIX:");
        System.out.println(metaMatrix.toString() + "\n" + "\n");
        metaGraph = metaMatrix.matrixToGraph();
        System.out.println("META-HEURISTIC GRAPH:");
        System.out.println(metaGraph.toString() + "\n" + "\n");
        
        /*Traffic routing using shortest path criterion for the topology obtained after the meta-heuristic algorithm*/
        metaPath = routing(metaGraph);
        
        /*Maximum flow computation for the topology obtained after the meta-heuristic algorithm*/
    	metaFlow = maximumFlow(metaGraph, metaPath, trafficMatrix, N);
        System.out.println("META-HEURISTIC MAXIMUM FLOW:");
        System.out.println(metaFlow + "\n" + "\n");
    	
    }
    
    /**
     * Generate the topology obtained with the greedy heuristic placement
     *@param trafficMatrix - matrix of the flows between all the sources and all the destinations
     *@param rowManh - rows of the Manhattan topology grid
     *@param colManh - columns of the Manhattan topology grid
     *@return a matrix that represents the Manhattan topology obtained with the greedy heuristic placement
     */
    private static Matrix greedy(Matrix trafficMatrix, int rowManh, int colManh){
    	Matrix trafficMatrixTemp = new Matrix(trafficMatrix);
		

    	Matrix positions;
    	Matrix graphMatrix;
    	int rowMax;
    	int colMax;
 
    	graphMatrix = new Matrix(rowManh, colManh);
    	
		for (int i = 0; i < trafficMatrixTemp.getRow() * trafficMatrixTemp.getColumn(); i++) {
			/*source and destination that exchange the maximum traffic flow */
			rowMax = trafficMatrixTemp.getRowMax();
			colMax = trafficMatrixTemp.getColumnMax();

			if(!graphMatrix.full()){
				/*if source and destination still haven't been  placed, put them into two adjacent empty positions*/
				if(!graphMatrix.isPresent(rowMax) && !graphMatrix.isPresent(colMax)){
					positions = new Matrix(2,2);
					graphMatrix.findEmpties(positions);
					if(positions.full()){
						graphMatrix.setElement((int)positions.getElement(0, 0), (int)positions.getElement(0, 1), (double)rowMax);
						graphMatrix.setElement((int)positions.getElement(1, 0), (int)positions.getElement(1, 1), (double)colMax);

					}	
				}
				
				else if(graphMatrix.isPresent(rowMax) && !graphMatrix.isPresent(colMax)){
					/*if the source has been placed yet, put the destination in an empty node connected to the source if possible */
					int rowElem = graphMatrix.rowElem(rowMax);
					int colElem = graphMatrix.colElem(rowMax);
					positions = new Matrix(1,2);
					graphMatrix.findNearEmpty(positions, rowElem, colElem);
					if(positions.full()){
						graphMatrix.setElement((int)positions.getElement(0, 0), (int)positions.getElement(0, 1), (double)colMax);

					}	

				}
				
				else if(!graphMatrix.isPresent(rowMax) && graphMatrix.isPresent(colMax)){
					/*if the destination has been placed yet, put the source in an empty node connected to the source if possible */
					int rowElem = graphMatrix.rowElem(colMax);
					int colElem = graphMatrix.colElem(colMax);
					positions = new Matrix(1,2);
					graphMatrix.findNearEmpty(positions, rowElem, colElem);
					if(positions.full()){
						graphMatrix.setElement((int)positions.getElement(0, 0), (int)positions.getElement(0, 1), (double)rowMax);

					}
				}
				trafficMatrixTemp.setElement(rowMax, colMax, 0);
			}
		}
		
		
    	return graphMatrix;
    	
    }
    
    /** Generates a uniform traffic matrix with values in the range [0.5, 1.5]
     * @param matrix - empty matrix of size nodes times nodes
     */
    private static void randomTrafficMatrix(Matrix matrix){
	
    	Random randomNumber = new Random();
    	//randomNumber.setSeed(10);

    	for(int i=0; i<matrix.getRow(); i++){
    		for(int j=0; j<matrix.getRow(); j++){
    			if(i==j)
    				matrix.setElement(i, j, 0);
    			else
    				matrix.setElement(i, j, randomNumber.nextDouble() + 0.5 );
    		}
    	}

    }
    
    /** Generates the topology obtained after a simulated annealing process
     * @param initialGraphMatrix - initial solution obtained with a greedy heuristic
     * @param trafficMatrix - matrix of the flows between all the sources and all the destinations
     * @param nodes - number of nodes in the network
     * @return a matrix that represents the Manhattan topology obtained after the simulating annealing meta-heuristic
     */
    private static Matrix metaHeuristic(Matrix initialGraphMatrix, Matrix trafficMatrix, int nodes){
    	double T; /* Temperature */
    	double coolingRate; /* Temperature decreasing rate */
    	
    	DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> currentGraph; /* Current solution */
    	ArrayList <List<DefaultWeightedEdge>> currentPath; /* List of the shortest paths for the current solution */
    	double currentFlow; /* Maximum flow of the current solution */
    	
    	DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> neighGraph; /*Neighbouring solution */
    	ArrayList <List<DefaultWeightedEdge>> neighPath; /* List of the shortest paths for the neighbouring solution*/
    	double neighFlow; /* Maximum flow of the neighbouring solution */
    	
    	Random random = new Random();
    	int randNum1; 
    	int randNum2;

    	
    	T = 100;
    	coolingRate = 0.003;
    	
    	Matrix currentGraphMatrix = new Matrix(initialGraphMatrix);
		
		currentGraph = currentGraphMatrix.matrixToGraph();
    	currentPath = routing(currentGraph);
    	currentFlow = maximumFlow(currentGraph, currentPath, trafficMatrix, nodes);
    	
    	while(T>0.00001){
    		Matrix neighGraphMatrix = new Matrix(currentGraphMatrix);
    		
    		/* Choose two nodes at random */
    		do{
    			randNum1 = random.nextInt(nodes);
    			randNum2 = random.nextInt(nodes);
    		}while(randNum1 == randNum2);
    		
    		/* Generated a neighbouring solution swapping the chosen nodes */
    		neighGraphMatrix.swap(randNum1, randNum2);
    		neighGraph = neighGraphMatrix.matrixToGraph();
    		
    		/* Route the traffic and compute the maximum flow for the neighbouring solution */
    	  	neighPath = routing(neighGraph);
        	neighFlow = maximumFlow(neighGraph, neighPath, trafficMatrix, nodes);
        	
        	/*Generation of the probability of accepting a worsening solution */
        	double prob = acceptanceProbability(currentFlow, neighFlow, T);
        	
        	/* Chose the neighbouring solution with probability "prob"*/
        	if(prob>Math.random()){
        		currentGraphMatrix = new Matrix(neighGraphMatrix);
        	}
        	
        	/* Calculate the maximum flow for the current solution */
        	currentGraph = currentGraphMatrix.matrixToGraph();
        	currentPath = routing(currentGraph);
        	currentFlow = maximumFlow(currentGraph, currentPath, trafficMatrix, nodes);
        	
        	/* Update the temperature */
        	T *= 1-coolingRate;
    		
    	}
    	
    	return currentGraphMatrix;
    }
    
    
    /** Generates the probability to accept a neighbouring solution in the simulated annealing meta-heuristic
     * @param currentFlow - maximum flow for the current solution
     * @param neighFlow - maximum flow for the neighbouring solution
     * @param T - temperature
     * @return probability to accept a neighbouring solution 
     */
    private static double acceptanceProbability (double currentFlow, double neighFlow, double T){
    	/*if the neighbouring solution is better than the current one return a probability equal to 1 */
    	if (neighFlow < currentFlow)
    		return 1.0;
    	/*if the neighbouring solution is worse than the current one generate the probability using the simulated annealing formual */
    	return Math.exp((currentFlow - neighFlow)/T);
    }
    
    /**
     * Calculation of the flow on the maximum congested edge
     *
     * @param graph - logical topology
     * @param shortestPathList  - list of all the paths using the shortest path criterion
     * @param trafficMatrix - matrix of the flows between all the sources and all the destinations
     * @param nodes - number of nodes in the network
     * @return maximum flow
     */
    private static double maximumFlow(DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> graph, 
    		ArrayList <List<DefaultWeightedEdge>> shortestPathList, Matrix trafficMatrix, int nodes){
    	double maxFlow = 0;
    	//DefaultWeightedEdge maxCongestedEdge=null;
    	double currentFlow;
    	String source;
    	String destination;
    	Set<DefaultWeightedEdge> graphEdges;
    	int s;
    	int d;

    
    	
    	graphEdges = graph.edgeSet();
    
    	/*For all the edges of the graph add the flow from source to destination
    	 * if the shortest path from source to destination contains that edge*/
    	for(DefaultWeightedEdge edge:graphEdges){
    		
    		currentFlow = 0;

    		for(List<DefaultWeightedEdge> path:shortestPathList){
    			
    			if(path.contains(edge)){
    				source = graph.getEdgeSource(path.get(0));
    				destination = graph.getEdgeTarget(path.get(path.size()-1));
    				s = Integer.parseInt(source.substring(1));
    				d = Integer.parseInt(destination.substring(1));
    				currentFlow = currentFlow + trafficMatrix.getElement(s,d);
    			}

    		}
    		
			if(currentFlow >maxFlow){
				maxFlow = currentFlow;
				//maxCongestedEdge = edge;
			}
			
			
    		
    	}
    	
    	return maxFlow;
    	
    }
    
    /**
     * Generation of all the path from source to destination using the shortest path criterion
     *
     * @param graph - logical topology
     * @return list of shortest path between all the sources and all the destinations
     */
    private static ArrayList <List<DefaultWeightedEdge>> routing (DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> graph){
    	/*List of all the shortest paths*/
    	ArrayList <List<DefaultWeightedEdge>> shortestPathList;
    	shortestPathList = new ArrayList<List<DefaultWeightedEdge>>();
    	
    	List<DefaultWeightedEdge> shortestPath;
    	
    	Set<String> vertices = graph.vertexSet();    	
    	for(String source:vertices){
    		for(String destination:vertices){
    			if(source!=destination){
    				shortestPath = (BellmanFordShortestPath.findPathBetween(graph, source, destination));
    		        shortestPathList.add(shortestPath);
    			}
    		}
    	}

    	return shortestPathList;
    }
    

}
