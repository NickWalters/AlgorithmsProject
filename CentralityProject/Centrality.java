import java.awt.geom.GeneralPath;
import java.util.*;
import java.util.Map.Entry;


import sun.security.krb5.internal.crypto.dk.AesDkCrypto;

/**
 * This is the big boy class. all the centrality work is calculated here
 * But! dont make it too big! you can use helper classes too if nessessary!
 *
 * @author Nicholas Walters and James Caldon 22226341
 * @version 9 May 2018 (last change by Nick)
 */
public class Centrality
{
    // instance variables - replace the example below with your own
    ArrayList<Integer> array;
    private int numVertices;
    private int[][] edgeMatrix;
    private float[] betweenessCentralities;
    private HashMap<Integer, Integer> degreeCentralities;
    private float[] katzCentralities;
    private int[][] weightsOfShortestPaths;
    
    

    
    public Centrality(FileData data)
    {
        //array = info.readFile(1);
        
        ArrayList<int[]> n = data.getNodes();
        degreeCentralities = getDegreeCentrality(n);
        numVertices = getNumVertices();
        betweenessCentralities = new float[numVertices];
        weightsOfShortestPaths = new int[numVertices][numVertices];
        katzCentralities = new float[numVertices];
    }
    
    // EDIT, changed to use an arraylist filled with arrays of ints (no parsing needed)- James
    /**
     * returns the degree centrality of a given graph
     * @param matrixOfGraph, the matrix representation of graph/text file
     * @param allNodesUnique, a list containing info of all distinct nodes
     */
    public HashMap<Integer, Integer> getDegreeCentrality(ArrayList<int[]> nodes){
        HashMap<Integer, Integer> nodesIndex = new HashMap<>();
        //indexNodes not used
        HashMap<Integer, Integer> indexNodes = new HashMap<>();
        for(int[] bothNodes: nodes){
            // if hashmap contains these nodes already, then increment its edgeValue
            // if not in hashmap, then add Node and its edge count is 1
            if(!nodesIndex.containsKey(bothNodes[0])){
                nodesIndex.put(bothNodes[0], 1);
            }
            else{
                int numEdges = nodesIndex.get(bothNodes[0]);
                nodesIndex.put(bothNodes[0], numEdges++);
            }
            
            if(!nodesIndex.containsKey(bothNodes[1])){
                nodesIndex.put(bothNodes[1], 1);
            }
            else{
                int numEdges = nodesIndex.get(bothNodes[1]);
                nodesIndex.put(bothNodes[1], numEdges++);
            }
        }
        //print all the nodes, with their associated values
        System.out.println(nodesIndex.entrySet());
        numVertices = nodesIndex.size();
        return nodesIndex;
    }
    
	/**
	 * @author James
	 * holds reference to the vertex and its weight
	 */
    
	public class Node {
		public final Integer v;
		public final Float w;
		Node (Integer vertex, Float weight) {
			v = vertex;
			w = weight;
		}
	}
	
	/**
	 * @author James
	 * Comparator for two nodes, used by the priority queue
	 * the highest priority is the lower value
	 */
	public class NodeComparator implements Comparator<Node>{

		@Override
		public int compare(Node o1, Node o2) {
			if (o1.w < o2.w) return 1;
			else if (o1.w > o2.w) return -1;
			return 0;
		}	
		
	}
	/*
    adj is edgeMatrix rip effieciency
    public int[] getClosenessCentrality(Graph g) {
    	int[][] adj = g.getAdjMatrix();
    	int size = g.getNumberOfVertices();
    	System.out.println(g);
    	int[] closeness = new int[size];
    	//for (int vertex = 0; vertex < adj.length; vertex++) {
    	int vertex = 0;
    		boolean[] visited = new boolean[size];
    		PriorityQueue<Node> pq = new PriorityQueue<Node>(size, new NodeComparator());
    		
    		
    		pq.add(new Node(vertex, 0));
    		
    		
    		while (!pq.isEmpty()) {
    			Node u = pq.remove();
    			if(!visited[u.v]) {
    				visited[u.v] = true;
    				for (int i = 0; i < size; i++) {
    					//Priority Queue speeds up extract-min
    					if (!visited[i]) {
    						if (adj[u.v][i] > 0) {
    							pq.add(new Node(i, adj[u.v][i]+u.w));
    						} 

    					}
    				}
    				closeness[vertex] = u.w;
    			}
    			
    		}
		//}
		for (int i = 0; i < closeness.length; i++) {
			System.out.print(closeness[i] + ", ");
			
		}
        return closeness;
    }
    */
    
	/**Uses adjacency list instead for that extra effieciency. Complexity O((|E| + |V|) * |V|).
	 * 
	 * @param g
	 * @return
	 */
    public Integer getClosenessCentrality(Graph g) {
    	ArrayList<HashSet<Integer>> adj = g.getAdjList();
    	int size = g.getNumberOfVertices();
    	System.out.println(g);
    	float[] closeness = new float[size];
    	PriorityQueue<Node> pq = new PriorityQueue<>(new NodeComparator());
    	for (int vertex = 0; vertex < size; vertex++) {

	    	int[][] distance = new int[size][2];
	    	for (int i = 0; i < size; i++) {
				distance[i][0] = -1;
			}
	    	distance[vertex][0] = 0;
	    	Queue<Integer> q = new LinkedList<Integer>();
	
	    	q.add(vertex);
	    	
	    	while (!q.isEmpty()) {
	    		int v = q.poll();
	    		HashSet<Integer> adjacent = adj.get(v);
	    		for (Integer i : adjacent) {
	    			if(distance[i][0] == -1) {
						distance[i][0] = distance[v][0] + 1;
						distance[i][1] = v;
						q.add(i);
	    			}
				}
	    		closeness[vertex] += distance[v][0];
	    	
	    	}
	    	
	    	pq.add(new Node(vertex, 1/closeness[vertex]));
	    	closeness[vertex] = 1/closeness[vertex]; 
    	}
        return g.getVertex(pq.poll().v);
    }
    
    /**
     * Brandes algorithm is the most efficient algorithm for betweeness Centrality
     * this runs in O(nm) time, compared to all other algorithms which require O(n^3) time for unweighted graphs
     */
    public float[] getBetweenessCentrality(Graph g){
    	int[][] edgeMatrix = g.getAdjMatrix();
	    int numNodes = edgeMatrix.length;
	    
        // Brandes algorithm O(EV^2) for UNWEIGHTED graphs: 
        // Brandes algorithm : https://people.csail.mit.edu/jshun/6886-s18/papers/BrandesBC.pdf
        
        java.util.Stack<Integer> stack;
        // assign the shortest paths list to use later on. Corresponds to P on paper
        ArrayList paths[] = new ArrayList[numNodes];
        // create a sigma list according to paper (σ)
        float sigma[] = new float[numNodes];
        // create a delta list according to paper (δ)
        float[] delta = new float[numNodes];
        // holds the distance for each iteration of the paths
        int distances[];
        // assign an empty queue
        Queue<Integer> queue;
        
        // for each s in V
        // beginning with the starting node, for all Vertex V which is an element of the graph G do:
        for(int startingNode = 0; startingNode < numNodes; startingNode++)
        {
            stack = new java.util.Stack<Integer>();
            distances = new int[numNodes];
            for(int i = 0; i<numNodes; i++)
            {
                paths[i] = new ArrayList(); // create an array inside of an array, to store the different sequences shortest paths
                sigma[i] = 0;
                distances[i] = -1;
            }
            sigma[startingNode] = 1;
            distances[startingNode] = 0;
            
            queue = new ArrayDeque<Integer>();
            queue.add(startingNode);
            int v; // current Node/Vertex
            // while Q not empty do:
            while(!queue.isEmpty())
            {
                // dequeue v from Q and push to S
                v = queue.remove();
                stack.push(v);
                // for each neighbour w of v/currentVertex do:
                for(int currentNeighbor = 0; currentNeighbor< numNodes; currentNeighbor++)
                {
                    if(edgeMatrix[v][currentNeighbor] == 1)
                    {
                        if(distances[currentNeighbor]<0)
                        {
                            queue.add(currentNeighbor);
                            distances[currentNeighbor] = distances[v]+1;
                        }
                        if(distances[currentNeighbor] == distances[v]+1)
                        {
                            sigma[currentNeighbor] += sigma[v];
                            paths[currentNeighbor].add(v);
                        }
                    }
                    weightsOfShortestPaths[v] = distances;
                }
            }
            
            for(int i = 0; i< numNodes; i++)
            {
                //δ[v] = 0, for all vertex thats an element of Graph
                delta[i] = 0;
            }
            
            //While Stack is not empty do:
            while(!stack.isEmpty())
            {
                // pop one by one
                v = stack.pop();
                // for each vertex in P/Paths, delta[w] = delta[w] + (sigma[w] / sigma[v]) * (1+ delta[v])
                java.util.Iterator<Integer> pathIterator = paths[v].iterator();
                int w; // w is the neighbour
                while(pathIterator.hasNext())
                {
                    w = pathIterator.next();
                    delta[w] += (float)((float)(sigma[w])/ (float)(sigma[v]))*(float)(1+ delta[v]);
                }
                if(v != startingNode){
                    betweenessCentralities[v] += (float)delta[v];
                }
            }
        }
        return betweenessCentralities;
    }
    
    
    
    
    
    @SuppressWarnings("unchecked")
	public double[] getKatzCentrality(Graph g){
    	ArrayList<HashSet<Integer>> adjList = g.getAdjList();
    	int[][] adjMatrix = g.getAdjMatrix();
    	int size = g.getNumberOfVertices();
    	System.out.println(g);
    	double alpha = 1.0;
    	double[] catz = new double[size];
    	int currentVertex = 0;
    	
    	
    	for (int i = 0; i < 100; i++) {
			//lastVertex = currentVertex;
			//currentVertex = lastVertex;
			
		}
    	
    	//for (int vertex = 0; vertex < size; vertex++) {
    	int vertex = 1;
    		@SuppressWarnings("rawtypes")
			ArrayList shortestPaths[] = new ArrayList[size];
    		for (int i = 0; i < size; i++) {
				shortestPaths[i] = new ArrayList<Integer>();
			}
	    	int[][] distance = new int[size][2];
	    	for (int i = 0; i < size; i++) {
				distance[i][0] = -1;
				distance[i][1] = -1;
			}
	    	distance[vertex][0] = 0;
	    	Queue<Integer> pq = new LinkedList<Integer>();
	
	    	pq.add(vertex);
	    	
	    	while (!pq.isEmpty()) {
	    		int v = pq.poll();
	    		HashSet<Integer> adjacent = adjList.get(v);
	    		for (Integer i : adjacent) {
	    			if(distance[i][0] == -1) {
						distance[i][0] = distance[v][0] + 1;
						distance[i][1] = v;
						shortestPaths[i].add(v);
						
						System.out.println(i + ": " + v + "| ");
						
						pq.add(i);
	    			}
				}
	    	
	    	}
	    	
    	//}

        return null;
    	
    }
    
    
    public int getNumVertices(){
        return numVertices;
    }
    
    /*
    public void calculateKatz(float katzConstant){
        for(int i=0; i<numVertices; i++){
            for(int r=0; r<numVertices; r++){
                katzCentralities[i] += ((float)degreeCentralities.get(r))*Math.pow(katzConstant, weightsOfShortestPaths[i][r]);
            }
        }
    }
    */
    
    public float[] returnKatzCentrality(){
        return katzCentralities;
    }
}

