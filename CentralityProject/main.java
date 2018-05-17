 import java.util.*;

import com.sun.javafx.collections.FloatArraySyncer;
/**
 * The main method which prints final outputs and ties together all classes
 *
 * @author Nicholas Walters and James Caldon 2226341
 * @version 9 May 2018
 */
public class main
{
    public static void main(String[] args) throws Exception
    {
        // read the file data for 428333.edges.txt
        //FileData data428333 = new FileData(1);
        //Centrality c4 = new Centrality(data428333);
        //c4.getDegreeCentrality(data428333.getNodes());
        //c4.getClosenessCentrality(data428333.getAdjMatrix());
    	
    	
        //Graph testGraph = new Graph("test.txt");
        //FileData data = new FileData(3);
        //Centrality c4 = new Centrality(data);
        //Integer closeness = c4.getClosenessCentrality(testGraph);
        //System.out.println(closeness);
        
        Graph tg = new Graph("428333.edges.txt");
        FileData data = new FileData(1);
        Centrality c = new Centrality(data);
        float[] betweeness = c.getBetweenessCentrality(tg);
        System.out.println("---Betweeness Centralities----");
        for(float item: betweeness){
            System.out.println(item);
        }
        System.out.println("------------------------------");
        
        c.calculateKatz(1);
        System.out.println("---Katz Centralities----");
        float[] katz = c.returnKatzCentrality();
        for(float item: katz){
            System.out.println(item);
        }
        System.out.println("------------------------------");
        
        
        
    	//for (int i = 0; i < closeness.length; i++) {
			//System.out.println(closeness[i]);
		//}
    	//double[] closenesss = c4.getKatzCentrality(testGraph);
        /**
        float[] floatArray = c4.getBetweenessCentrality(testGraph);
        System.out.println(testGraph);
        for (float f : floatArray) {
			System.out.println(f);
		}
		**/
        //int[] matrix = data428333.getAjMatrix();
        //ArrayList<Integer> nodesUnique = data428333.getUniqueNodes();
        // run the 4 centrality measures, also time performance
        long time = System.currentTimeMillis(); //Start timer
        /*
        Centrality graph438333 = new Centrality(data428333);
        Node[] degree42833 = graph438333.getDegreeCentrality(matrix, nodesUnique);
        System.out.println("Degree centrality for: " + data428333.getFileName());
        
        for(int i=0; i<degree42833.length; i++){
            System.out.println();
        }
        long endTime = System.currentTimeMillis();
        long totalTimeTaken = endTime - time;
        System.out.println("Run time:" + totalTimeTaken + "ms");
        System.out.println();
        */
        
        
        // read the file data for 78813.edges.txt
        /*
        try {
            FileData data78813 = new FileData(2);
            data78813.getNodes();
        }
        catch (Exception e) {


        }
        */
        //data78813.getAjMatrix();
    }
    

}
