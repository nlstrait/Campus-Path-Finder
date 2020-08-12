package chiefarch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import chiefarch.Graph;

/**
 * GraphAlgos contains various algorithms for use on a Graph
 * 
 * @author Nolan Strait
 */
public class GraphAlgos {
	
	/**
	 * Runs Dijsktra's algorithm to find the shortest path between two nodes in a graph.
	 * 
	 * @param graph	: the graph on which to perform the search
	 * @param src  	: the node to find a path from
	 * @param dest 	: the node to find a path to
	 * @param <N>	: the type of nodes in the given graph
	 * @return an ordered list where the first element is the first node from src and the last
	 * 		element is dest (this list is empty if src == dest); null if either src or dest are not
	 * 		in this graph or there is no path between these two nodes
	 * @throws IllegalArgumentException if g is null
	 */
	public static <N> List<N> runDijsktra(Graph<N, Double> g, N src, N dest) {
		if (g == null) throw new IllegalArgumentException();
		if (!g.contains(src) || !g.contains(dest)) return null;
		
		Set<N> finished = new HashSet<N>();
		Queue<Path<N>> active = new PriorityQueue<Path<N>>();
		active.add(new Path<N>(src, 0.0));
		
		while (!active.isEmpty()) {
			Path<N> minPath = active.remove();
			List<N> nodes = minPath.nodes;
			N minDest = nodes.get(nodes.size() - 1);
			
			if (minDest.equals(dest)) {
				nodes.remove(0); // remove src
				return nodes;
			}
			
			if (finished.contains(minDest)) continue;
			
			Map<N, Set<Double>> childMap = g.childMap(minDest);
			for (N child : childMap.keySet()) {
				if (finished.contains(child)) continue;
				List<Double> edges = new ArrayList<Double>(childMap.get(child));
				Collections.sort(edges);
				Path<N> newPath = minPath.addNode(child, edges.get(0));
				active.add(newPath);
			}
			
			finished.add(minDest);
		}
		return null; // no path found
	}
	
	/**
	 * Path represents a path from one node to another
	 * 
	 * Abstraction Function:
	 * 		nodes.get(0) == the source node in this path
	 * 		nodes.get(nodes.size() - 1) == the destination node in this path
	 * 		cost == the cost of this path
	 * 
	 * Representation Invariant:
	 * 		nodes != null
	 * 		nodes[0..nodes.size() - 1] != null
	 * 		cost != null 
	 * 
	 * @author Nolan
	 */
	private static class Path<N> implements Comparable<Path<N>> {
		public final List<N> nodes; // nodes in this path
		public final Double cost; // cost of this path
		
		/**
		 * Creates a new Path
		 * 
		 * @param node : the only node to be in this path
		 * @param cost : the starting cost of this path
		 */
		public Path(N node, Double cost) {
			nodes = new ArrayList<N>();
			nodes.add(node);
			this.cost = cost;
		}
		
		/**
		 * Creates a new Path
		 * 
		 * @param nodes : the nodes to be in this path
		 * @param cost : the cost of this path
		 */
		public Path(List<N> nodes, Double cost) {
			this.nodes = nodes;
			this.cost = cost;
		}
		
		/**
		 * Adds a node to this path and returns the resulting path
		 * 
		 * @param node : the node to be added
		 * @param cost : the cost to get from this path's end to the provided node
		 * @return a new Path which is this path plus a new node
		 */
		public Path<N> addNode(N node, Double cost) {
			List<N> newPath = new ArrayList<N>();
			for (N oldNode : this.nodes)
				newPath.add(oldNode);
			newPath.add(node);
			Double newCost = this.cost + cost;
			return new Path<N>(newPath, newCost);
		}

		/**
		 * Compares this Path with another
		 * 
		 * @param other : the path to compare this to
		 * @return the difference between this path's cost and the other path's cost
		 */
		@Override
		public int compareTo(Path<N> other) {
			return this.cost.compareTo(other.cost);
		}
	}
}
