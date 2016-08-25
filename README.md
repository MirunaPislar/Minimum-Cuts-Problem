# MinimumCutsProblem

Java program that uses Karger's randomized algorithm to compute the minimum cuts of an undirected, connected graph.

# Input

This program takes as input a file containing the adjacency list representation of a simple, undirected, connected graph. The first column in the file represents the vertex label, and the particular row (other entries except the first column) tells all the vertices that the vertex is adjacent to.

# Output

An integer representing the minimum cut i.e the fewest number of crossing edges.

# How it works

The Random Contraction Algorithm invented by David Karger:

- gradually contracts all vertices until there are only 2 nodes left in the graph

- first, it picks uniformly at random a remaining edge (u,v)

- then, it merges the two vertices into a new one, say w

- removes all self loops (w,w) in the graph

- the number of edges between the remaining 2 vertices denotes the number of cuts

# Running time

- is Big Omega(n^2 * m), where n = no of nodes and m = no of edges

- can get to O(n^2) with speed-ups

- although it may seem like a slow algorithm, it is actually extremely efficient compared to other approaches

- however it does not always compute the minimum cut since it is a randomized algorithm and depends on which edges are contracted

- we need to run the algorithm many times with different random seeds, and remember the smallest cut that we ever find

# More info

Check the Coursera "Algorithms: Design and Analysis, Part 1" by professor Tim Roughgarden from Stanford University. In week 3 there is a complete proof of the probability of success (which btw is 1/n) and why this algorithm works efficiently.

Also, check the wikipedia page:
https://en.wikipedia.org/wiki/Karger%27s_algorithm
