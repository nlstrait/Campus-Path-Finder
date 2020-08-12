package chiefarch;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import chiefarch.Graph;
import chiefarch.GraphAlgos;
import chiefarch.CampusParser.MalformedDataException;

/**
 * CampusMap represents a map of a campus and can find paths between campus buildings.
 * The upper left corner of a CampusMap is the point (0, 0).
 * 
 * @author Nolan
 */
public class CampusMap {
	
	// a graph of locations and paths around campus
	private Graph<Point2D.Double, Double> g;
	
	// a dictionary of sorts for looking up buildings on campus by their abbreviations
	private Map<String, Building> buildings;
	
	/**
	 * Creates a new CampusMap
	 * 
	 * @param buildings_filename : name of file containing building data
	 * @param paths_filename     : name of file containing path data
	 * @throws MalformedDataException if either file is malformed
	 */
	public CampusMap(String buildings_filename, String paths_filename) throws MalformedDataException {
		this.buildings = CampusParser.parseBuildingData(buildings_filename);
		Map<Point2D.Double, Map<Point2D.Double, Double>> paths = CampusParser.parsePathData(paths_filename);
		
		g = new Graph<Point2D.Double, Double>();
		for (Point2D.Double point : paths.keySet()) {
			g.addNode(point);
			
			Map<Point2D.Double, Double> thisMap = paths.get(point);
			for (Point2D.Double point_i : thisMap.keySet()) {
				g.addNode(point_i);
				
				Double distance = thisMap.get(point_i);
				g.addEdge(point, point_i, distance);
			}
		}
	}
	
	/**
	 * Finds the shortest route from one building to another
	 * 
	 * @param start : the abbreviation for the building from which we start our journey
	 * @param end   : the abbreviation for the destination building
	 * @return a list of coordinate points that mark locations along the shortest route from start
	 * 		to end where list[0] is the first location to head to from start and list[list.length - 1]
	 * 		is end. Returns null if there is no path between start and end (including the case that
	 * 		either start or end are not buildings on this campus)
	 */
	public List<Point2D.Double> findRoute(String start, String end) {
		if (!buildings.containsKey(start) || !buildings.containsKey(end)) return null;
		
		Building a = buildings.get(start);
		Building b = buildings.get(end);
		return GraphAlgos.runDijsktra(g, a.location, b.location);
	}
	
	/**
	 * Fetches a list of all buildings on this campus
	 * 
	 * @return a sorted list of abbreviated building names
	 */
	public List<String> getBuildingAbbrevs() {
		List<String> abbrevs = new ArrayList<String>(buildings.keySet());
		Collections.sort(abbrevs);
		return abbrevs;
	}
	
	/**
	 * Fetches the full name of a building by passing in its abbreviation
	 * 
	 * @param abbrev : a building name abbreviation
	 * @return the full name of a building
	 */
	public String getFullName(String abbrev) {
		return buildings.get(abbrev).name;
	}
	
	/**
	 * Fetches the location of a building on this campus
	 * 
	 * @param building : an abbreviated building name
	 * @return the coordinates of the specified building
	 */
	public Point2D.Double locationOf(String building) {
		return buildings.get(building).location;
	}
	
	/**
	 * Fetches the distance between two adjacent coordinate points on this CampusMap
	 * 
	 * @param a : some coordinate point on this CampusMap
	 * @param b : another coordinate point
	 * @return the distance between a and b
	 */
	public Double distanceBetween(Point2D.Double a, Point2D.Double b) {
		List<Double> edges = new ArrayList<Double>(g.edgesBetween(a, b));
		Collections.sort(edges); // shouldn't need to do this, but just to be sure
		return edges.get(0);
	}
	
	/**
	 * Identifies whether or not a specified building is a part of this CampusMap
	 * 
	 * @param building : the abbreviated name of the building in question
	 * @return true if this building is on this CampusMap, false otherwise
	 */
	public boolean hasBuilding(String building) {
		return buildings.containsKey(building);
	}
	
	/**
	 * Finds the compass direction to get from one point to another
	 * 
	 * @param start : the starting location
	 * @param end   : the ending location
	 * @return the direction to go from start to get to end (N, NE, E, SE, S, SW, W, or NW)
	 */
	public static String getDirection(Point2D.Double start, Point2D.Double end) {
		Double dx = end.x - start.x;
		Double dy = start.y - end.y;
		Double theta = Math.atan2(dy, dx);
		
		if      (theta < -7 * Math.PI / 8) 	return "W";
		else if (theta < -5 * Math.PI / 8) 	return "SW";
		else if (theta < -3 * Math.PI / 8) 	return "S";
		else if (theta < -1 * Math.PI / 8) 	return "SE";
		else if (theta < 	  Math.PI / 8)	return "E";
		else if (theta <  3 * Math.PI / 8)	return "NE";
		else if (theta <  5 * Math.PI / 8)	return "N";
		else if (theta <  7 * Math.PI / 8)	return "NW";
		else								return "W";
	}
}
