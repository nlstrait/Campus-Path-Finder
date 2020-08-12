package chiefarch;

import java.util.*;


/**
 * Graph represents a directed, labeled multi-graph.
 *
 * @author Nolan Strait
 *
 * @specfield nodes : set // The nodes
 * @specfield edges : set // The directed edges between nodes
 * @param N : the type of nodes in this graph
 * @param E : the type of edges in this graph
 * 
 * Abstraction Function:
 * 		All nodes in links.keySet()
 * 		T t is a parent of all nodes in links.get(t).keySet()
 * 		The edge labels from T parent to T child are found in links.get(parent).get(child)
 * 
 * Representation Invariant:
 * 		links != null
 * 		For any node in the graph represented by String s, links.get(s).keySet() should be a subset
 * 			of links.keySet()
 */
public class Graph<N, E> {
	
	private static final boolean TESTING = false; // turns on/off calls to checkRep()
    
    private HashMap<N, HashMap<N, HashSet<E>>> links;
    
    /**
     * Creates a null graph.
     */
    public Graph() {
        links = new HashMap<N, HashMap<N, HashSet<E>>>();
        if (Graph.TESTING) checkRep();
    }
    
    /**
     * Creates an empty graph from a set of nodes.
     * 
     * @param nodes : a set of nodes to be added to this graph
     * @modifies nodes
     * @effects adds new members to nodes
     */
    public Graph(Set<N> nodes) {
    	links = new HashMap<N, HashMap<N, HashSet<E>>>();
    	for (N node : nodes)
    		links.put(node, new HashMap<N, HashSet<E>>());
    	if (Graph.TESTING) checkRep();
    }
    
    /**
     * Adds a new node to this graph. Does nothing if this node is already a part of the graph.
     * 
     * @param node : the node to be added to this graph
     * @modifies nodes
     * @effects may add new node to nodes
     * @return true if this node was successfully added to this graph, false if this node was
     *      already a part of this graph
     */
    public boolean addNode(N node) {
        if (links.containsKey(node)) return false;
        links.put(node, new HashMap<N, HashSet<E>>());
        return true;
    }
    
    /**
     * Removes specified node from this graph. Does nothing if this node is not in this graph.
     *  
     * @param node : the node to be removed from this graph
     * @modifies nodes
     * @effects may remove node from nodes
     * @return true if this node was successfully removed from this graph, false if this node
     * 		was not already a part of this graph
     */
    public boolean removeNode(N node) {
    	if (Graph.TESTING) checkRep();
        if (!links.containsKey(node)) return false;
        links.remove(node);
        for (HashMap<N, HashSet<E>> edges : links.values()) edges.remove(node);
        if (Graph.TESTING) checkRep();
        return true;
    }
    
    /**
     * Adds an edge to this graph. Does nothing if this edge already exists in this graph.
     * 
     * @param src   : the node at the tail end of this directed edge
     * @param dest  : the node at the head of this directed edge
     * @param label : the label of this edge
     * @modifies edges
     * @effects may add edge to edges
     * @throws IllegalArgumentException if either src or dest are not nodes in this graph
     * @return true if this edge was successfully added to this graph, false if this edge was
     * 		already a part of this graph
     */
    public boolean addEdge(N src, N dest, E label) {
        if (!links.containsKey(src) || !links.containsKey(dest))
            throw new IllegalArgumentException("No such node(s) in graph");
        HashMap<N, HashSet<E>> children = links.get(src);
        // add dest as child of src if it isn't already
        if (!children.containsKey(dest)) children.put(dest, new HashSet<E>());
        return children.get(dest).add(label);
    }
    
    /**
     * Removes specified edge from this graph. 
     * 
     * @param src   : the node at the tail end of this directed edge
     * @param dest  : the node at the head of this directed edge
     * @param label : the label of this edge
     * @modifies edges
     * @effects may remove edge from edges
     * @throws IllegalArgumentException if either src or dest are not nodes in this graph
     * @return true if this edge was successfully removed from this graph, false if this edge
     * 		was not already a part of this graph
     */
    public boolean removeEdge(N src, N dest, E label) {
        if (!links.containsKey(src) || !links.containsKey(dest))
            throw new IllegalArgumentException("No such node(s) in graph");
        if (!links.get(src).containsKey(dest)) return false;
        Set<E> edges = links.get(src).get(dest);
        boolean success = edges.remove(label);
        // if no more edges exist from src to dest, remove dest from mapping of children
        if (edges.isEmpty()) links.get(src).remove(dest);
        return success;
    }
    
    /**
     * Fetches nodes in this graph.
     * 
     * @return a set of all nodes in this graph
     */
    @SuppressWarnings("unchecked")
    public Set<N> nodeSet() {
        return ((HashMap<N, HashMap<N, HashSet<E>>>) links.clone()).keySet();
    }
    
    /**
     * Returns a mapping of children to edges for a specified node.
     * 
     * @param node : the node in question
     * @return null if the specified node is not in this graph; otherwise, returns a map where the
     *      keys are children of node and the values are sets directed edges leading from node to
     *      one of its children
     */
    @SuppressWarnings("unchecked")
    public Map<N, Set<E>> childMap(N node) {
        if (!links.containsKey(node)) return null;
        // creates a copy of the value (HashMap) associated with key s
        Map<N, Set<E>> hm = new HashMap<N, Set<E>>();
        HashMap<N, HashSet<E>> dests = links.get(node);
        for (N dest : dests.keySet())
            hm.put(dest, (HashSet<E>) dests.get(dest).clone());
        return hm;
    }
    
    /**
     * Fetches a set of directed edges leading from one node to another.
     * 
     * @param parent : the parent node to be considered
     * @param child  : the child node to be considered
     * @return a set of all directed edges leading from parent to child
     */
    @SuppressWarnings("unchecked")
	public Set<E> edgesBetween(N parent, N child) {
    	return (Set<E>) links.get(parent).get(child).clone();
    }
    
    /**
     * Identifies whether or not this Graph has a specified node.
     * 
     * @param node : the node to search for
     * @return true if this node is in the graph, false otherwise
     */
    public boolean contains(N node) {
    	return links.keySet().contains(node);
    }
    
    /**
     * Identifies whether or not a specified parent-child relationship exists in this Graph
     * 
     * @param parent : the proposed parent
     * @param child : the proposed child
     * @return true iff this relationship holds
     */
    public boolean hasParentChild(N parent, N child) {
    	if (!this.contains(parent) || !this.contains(child)) return false;
    	return links.get(parent).containsKey(child);
    }
    
    /**
     * Checks to ensure that this Graph's representation invariant holds
     * 
     * @throws RuntimeException if the representation invariant does not hold
     */
    private void checkRep() {
    	if (links == null) throw new RuntimeException("links should not be null");
    	for (N parent : links.keySet())
    		for (N child : links.get(parent).keySet())
    			if (!links.keySet().contains(child))
    				throw new RuntimeException("child found that is not a part of parent set");
    }
}
