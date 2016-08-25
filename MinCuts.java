import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 *  Karger's algorithm to compute the minimum cut of a connected graph.
 *  Takes as input an undirected connected graph and computes a cut
 *  with the fewest number of crossing edges. We need to repeat the 
 *  algorithm n(n-1)*(ln n) / 2 times to guarantee success.
 *  The probability of not finding the minimum cut is 1/n.
 */
public class MinCuts {
	
	/*
	 *  Method that calculates the minimum cut by randomly choosing an edge,
	 *  contracting the vertices making that edge, removing all the self loops
	 *  until there are two vertices left in the graph.
	 *  This is an efficient approach invented by MIT professor David Karger in 1993.
	 */
	
	public static int minCut(HashMap<Integer, ArrayList<Integer>> adjList, 
			ArrayList<Integer> vertices)
	{
		// Initialize the new label that would be applied to contracted edges to begin
		// with n+1 (just make sure that labels don't repeat and so they conflict)
		int label = adjList.size() + 1;
		// Need to loop over the graph until there are only 2 vertices remained.
		while (vertices.size() > 2)
		{
			// Build an edge matrix (m x 2) corresponding to the edge graph
			// This is updated at each iteration in order to synchronize it with the new graph changes.
			int noOfEdges = getNoOfEdges(adjList, vertices);
			int[][] edges = buildEdges(noOfEdges, adjList, vertices);
			
			//Randomly choose an edge
			Random rand = new Random();
			
			// Randomly choose an edge numbered from 0 to no of edges - 1.
			int no = rand.nextInt(edges.length);
			// Let vertex1 be the first end of the randomly chosen edge
			int vertex1 = edges[no][0];
			// Let vertex2 be the second end of the randomly chosen edge
			int vertex2 = edges[no][1];
			
			// Let valuesV1 be the array of values corresponding to vertex1
			ArrayList<Integer> valuesV1 = adjList.get(vertex1);		
			// Let valuesV2 be the array of values corresponding to vertex2
			ArrayList<Integer> valuesV2 = adjList.get(vertex2);
			
			// Add all values from vertex2 to vertex1
			for(int i = 0; i < valuesV2.size(); i++)
				valuesV1.add(valuesV2.get(i));
			
			// Remove vertex2 from the array of vertices 
			vertices.remove(vertices.indexOf(vertex2));
			
			// Remove vertex2 from the adjacency list 
			adjList.remove(vertex2);

			// Give a new label to vertex1 and change all the labels of 
			// of vertex1 or vertex2 to the new label
			for(int i = 0; i < vertices.size(); i++)
			{
				ArrayList<Integer> values = adjList.get(vertices.get(i));
				for(int j = 0; j < values.size(); j++)
				{
					if(values.get(j) == vertex1)
						values.set(j, label);
					if(values.get(j) == vertex2)
						values.set(j, label);
				} // for
			} // for
			
			// Change the label of the key node vertex1 in the adjList
			ArrayList<Integer> values = adjList.get(vertex1);
			adjList.remove(vertex1);
			adjList.put(label, values);
			
			// Change the label of vertex1 in the vertices ArrayList
			int index = vertices.indexOf(vertex1);
			vertices.set(index, label);
			
			// Remove self loops by looping over the element of the new label 
			// and by deleting all edges outgoing to its own label.
			int position = 0;
			int length = adjList.get(label).size();
			while (position < length)
			{
				if(adjList.get(label).get(position) == label)
				{
					adjList.get(label).remove(position);
					// Need to decrease length, since we removed an edge
					length--;
				} // if
				else
					position++;
			} // while
			
			// Increase the label by 1 so its uniqueness is preserved
			label++;

		} // while		
		
		// The minimum cut no is the number of edges between the two vertices left.
		return adjList.get(vertices.get(0)).size();
	} //minCut
	
	// Method to read the data necessary to construct a graph (more precisely, its adj list.
	public static void construct(HashMap<Integer, ArrayList<Integer>> adjList, 
			ArrayList<Integer> vertices) throws IOException
	{
		File file = new File(System.getProperty("user.dir") + "/adjList.txt");
		BufferedReader input = new BufferedReader(new FileReader(file));
		try 
		{
			// Read a line of text from the file
		    String line = input.readLine();
		    while (line != null) 
		    {
		    	// Split the line of text into a vector of Strings
		    	// The regex is the tab between ints
		    	String[] vector = line.split("\t");
		    	// Keep track of the key values
		    	int key = Integer.parseInt(vector[0]);
		    	vertices.add(key);
		    	// Convert the vector of values for the edges to integers
		    	ArrayList<Integer> edges = new ArrayList<Integer>();
		    	for(int i=0; i < vector.length - 1; i++)
		    		if(Integer.parseInt(vector[i+1]) != key)
		    			edges.add(Integer.parseInt(vector[i+1]));
		    	// map he key to its edges
		    	adjList.put(key, edges);
		    	// Read a new line of text
		    	line = input.readLine();
		    } // while
		} // try
		finally
		{
			input.close();
		} // finally
	}
	
	// Main method that constructs n - 1 graphs and performs the min cut algorithm
	public static void main(String[] arg) throws IOException
	{
		HashMap<Integer, ArrayList<Integer>> adjList = new HashMap<Integer, ArrayList<Integer>>();
		
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		
		// Construct the initial graph
		construct(adjList, vertices);
		
		// Initialize the absolute minimum to be n*n, where n = no of nodes in the graph.
		int absoluteMinimum = adjList.size() * adjList.size();
		
		// Loop over the graph (n*(n-1)/2) times in order to find the absolute minimum cuts.
		// I will actually call it n - 1 times, still works.
		for(int i = 0; i <= adjList.size() - 1; i++)
		{
			int minFound = minCut(adjList, vertices);
			
			// Print the minFound at each call - totally optional, just for fun
			System.out.println("Min found on call " + i + " is: " + minFound);
			
			// Compare the minimum cuts number found on the current call
			// with the overall minimum cuts number in order to find an absolute
			if(minFound < absoluteMinimum)
				absoluteMinimum = minFound;
			
			// Clear the current values held in adjacency list and vertices array
			adjList.clear();
			vertices.clear();
			// Construct a new identical graph to perform the minCut algo again
			construct(adjList, vertices);
		} // for
		
		// Print the absolute minimum found
		System.out.println("\n MINIMAL CUT FOUND IS \n" + absoluteMinimum);
	} // main
	
	// Used for debugging - prints a string representation if an ArrayList of Integers
	public static String print(ArrayList<Integer> vector)
	{
		String toPrint = "";
		for(int i = 0; i < vector.size(); i++)
			toPrint += vector.get(i) + " ";
		toPrint += "\n";
		return toPrint;
	} // print
	
	// This method takes the number of edges of a graph, its adjacency list 
	// and its vertices and returns a mx2 matrix representing the edges of a graph
	public static int[][] buildEdges(int noOfEdges, HashMap<Integer, 
			ArrayList<Integer>> adjList, ArrayList<Integer> vertices)
	{
		int k = 0;
		int[][] edges = new int[noOfEdges][2];
		for(int i= 0; i < vertices.size(); i++)
		{
			ArrayList<Integer> vector = adjList.get(vertices.get(i));
			for(int j= 0; j< vector.size(); j++)
			{
				edges[k][0] = vertices.get(i);
				edges[k][1] = vector.get(j);
				k++;
			} // for
		} // for
		return edges;
	} // buildEdges
	
	// Given the adjList and the vertices of a graph, this method returns the number of edges
	public static int getNoOfEdges(HashMap<Integer, ArrayList<Integer>> adjList, ArrayList<Integer> vertices)
	{
		int noOfEdges = 0;
		for(int i= 0; i < vertices.size(); i++)
		{
			ArrayList<Integer> vector = adjList.get(vertices.get(i));
			noOfEdges += vector.size();
		} // for
		return noOfEdges;
	} // buildEdges
	
} // MinCuts
