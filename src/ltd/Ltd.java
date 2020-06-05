package ltd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.*;


/**
 * LTD greedy heuristic
 *
 * @author Cilia Martina
 * @since 2016
 */

public final class Ltd
{
    private Ltd(){
    } // ensure non-instantiability.

    /**
     * The starting point for the main
     *
     */
    public static void main(String [] args)
    {
    	int N=30; /*Number of nodes*/
    	int degree = 5; /*Node transmitters/receivers*/
    	double maxFlowGreedy; /*Maximum flow using the greedy heuristic*/
    	double maxFlowRandom; /*Maximum flow using the random topology*/
    	double maxFlowRandomTemp;
    	
        
    	ArrayList<List<DefaultWeightedEdge>> pathsGreedy; /*shortest path routing for the greedy heuristic*/
    	ArrayList<List<DefaultWeightedEdge>> pathsRandom; /*shortest path routing for the random topology*/
    	ArrayList<List<DefaultWeightedEdge>> pathsRandomTemp;
    	
    	DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> randomGraph; /*Random generated topology*/
        DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> graph; /*Greedy heuristic topology*/
        DefaultDirectedWeightedGraph <String, DefaultWeightedEdge> randomGraphTemp; 
        Matrix trafficMatrix; /*Traffic matrix*/

        /*Generation of the random traffic matrix*/
        trafficMatrix = new Matrix(N,N);
        randomTrafficMatrix(trafficMatrix);
        System.out.println("TRAFFIC MATRIX");
        System.out.println(trafficMatrix.toString());

        /*Generation of the starting ring topology for the greedy heuristic*/
        graph = createGraph(N);
        
        /*Generation of the greedy heuristic logical topology*/ 
        graphLTD(graph, trafficMatrix, degree);
        
        /*Generation of 500 random topology and choice of the best one among them */
        int iter = 0;
        do{
        	iter ++;
        	randomGraph = randomGraph(N,degree,Math.random());
        	pathsRandom = routing(randomGraph);
        } while (pathsRandom.contains(null));
        
        maxFlowRandom = maximumFlow(randomGraph, pathsRandom, trafficMatrix, N);
        
        while(iter<500){
        	randomGraphTemp = randomGraph(N,degree,Math.random());
        	pathsRandomTemp = routing(randomGraphTemp);
        	if(!pathsRandomTemp.contains(null)){
        		maxFlowRandomTemp =maximumFlow(randomGraphTemp, pathsRandomTemp, trafficMatrix, N);
        		if(maxFlowRandomTemp<maxFlowRandom && randomGraphTemp.edgeSet().size() == graph.edgeSet().size()){
        			randomGraph.equals(randomGraphTemp);
        			pathsRandom.equals(pathsRandomTemp);
        			maxFlowRandom = maxFlowRandomTemp;
        		}
        	}
        	iter++;
        }
       	
        
        /*Traffic routing using shortest path algorithm*/
        pathsGreedy = routing(graph);
        
        /*Maximum flow computation*/
        maxFlowGreedy = maximumFlow(graph, pathsGreedy, trafficMatrix, N);
        System.out.println("\n\nMAXIMUM FLOW GREEDY:");
        System.out.println(maxFlowGreedy);
        System.out.println("\nMAXIMUM FLOW RANDOM:");
        System.out.println(maxFlowRandom);
    }
    
    /**
     * Creates a directed and weighted graph with a ring topology
     *
     * @param nodes - number of nodes in the network
     * @return ring topology
     */
    private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> createGraph(int nodes)
    {
    	DefaultWeightedEdge edge;
    	
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g =
                new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        /* add the vertices */
        for(int i=0;i<nodes; i++){
        	String s = new String("v" + i);
        	
        	g.addVertex(s);        	
        }
        
        /* add the edges */
		 for (int i=0; i<nodes; i++){
			 edge=g.addEdge("v" + (i), "v" + ((i+1)%nodes));
			 g.setEdgeWeight(edge, 1);
		 }
        return g;
    }

    
    /** Generates a uniform traffic matrix with values in the range [0.5, 1.5]
     * @param matrix - empty matrix of size nodes times nodes
     */
    private static void randomTrafficMatrix(Matrix matrix){
    	
    	Random randomNumber = new Random();
  
 
    	for(int i=0; i<matrix.getRow(); i++){
    		for(int j=0; j<matrix.getRow(); j++){
    			if(i==j)
    				matrix.setElement(i, j, 0);
    			else
    				matrix.setElement(i, j, randomNumber.nextFloat() + 0.5);
    		}
    	}
   
    }
    
    /** Generates an unbalanced traffic matrix with values in the range [0.5, 1.5] for 90% of the nodes
     * and values in the range [5, 15] for 10% of the nodes
     * @param matrix - empty matrix of size nodes times nodes
     */
 /*   private static void randomTrafficMatrix(Matrix matrix){
    	
    	Random randomNumber = new Random();

    	for(int i=0; i<matrix.getRow(); i++){
    		for(int j=0; j<matrix.getRow(); j++){
    			if(i==j)
    				matrix.setElement(i, j, 0);
    			else
    				if(Math.random()<=0.1)
    					matrix.setElement(i, j, randomNumber.nextInt(11)+5);
    				else
    					matrix.setElement(i, j, randomNumber.nextDouble()+0.5);
    		}
    	}
   
    }*/
    
    /**
     * Generates the logical topology using the greedy heuristic implemented
     *
     * @param graph: starting ring topology
     * @param trafficMatrix - matrix of the flows between all the sources and all the destinations
     * @param degree - maximum number of transmitters/receivers per node
     */
    private static void graphLTD(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph, Matrix trafficMatrix, int degree) {
		int maxIndexRowMatrix;
		int maxIndexColumnMatrix;
		int outDegree;
		int inDegree;
		DefaultWeightedEdge edge;
		
		/* copy of the traffic matrix */
		Matrix matrix = new Matrix(trafficMatrix);
	    
		for (int i = 0; i < matrix.getRow() * matrix.getColumn(); i++) {
			/*source and destination that exchange the maximum traffic flow */
			maxIndexRowMatrix = matrix.getRowMax();
			maxIndexColumnMatrix  = matrix.getColumnMax();;

			/*actual number of incoming and outgoing links from source and destination chosen*/
			outDegree = graph.outDegreeOf("v" + maxIndexRowMatrix);
			inDegree = graph.inDegreeOf("v" + maxIndexColumnMatrix);
			
			/*link addition if the actual degree is less then the maximum degree*/
			if (outDegree < degree && inDegree < degree && maxIndexRowMatrix != maxIndexColumnMatrix) {
				edge = graph.addEdge("v" + maxIndexRowMatrix, "v" + maxIndexColumnMatrix);
				if(edge!=null){
					graph.setEdgeWeight(edge, 1);
				}
			}
			
			/*set the considered entry of the matrix to 0*/
			matrix.setElement(maxIndexRowMatrix, maxIndexColumnMatrix, 0);

		}

	}

    /**
     * Generates the random topology
     *
     * @param nodes - number of nodes in the network
     * @param degree - maximum number of transmitters/receivers per node
     * @param p - Erdős-Rényi probability
     */
    private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> randomGraph (int nodes, int degree, double p){
    	DefaultWeightedEdge edge;
    	
    	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g =
                new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        /* add the vertices */
        for(int i=0;i<nodes; i++){
        	String s = new String("v" + i);
        	
        	g.addVertex(s);  
        }


        /*Add random edges using probability p and checking the maximum degree of the nodes*/
        Set<String> vertices = g.vertexSet();
        	for(String i:vertices){
        		for(String j:vertices){
        			if(Math.random()<p && g.outDegreeOf(i)<degree && g.inDegreeOf(j)<degree && i!=j){
        				edge = g.addEdge(i, j);
        				g.setEdgeWeight(edge, 1);
        				}
        		}	
        	}
        	
        return g;
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
}