package chiefarch;

import java.awt.geom.Point2D;

/**
 * Building represents a building by its name and coordinates
 * 
 * @author Nolan Strait
 */
public class Building {
	
	// full name of this building
	public final String name;
	
	// the (x, y) coordinate pair marking the location of this building
	public final Point2D.Double location;
	
	/**
	 * Creates a new Building
	 * 
	 * @param name	: the name of this building
	 * @param x		: x coordinate of this building
	 * @param y		: y coordinate of this building
	 */
	public Building(String name, Double x, Double y) {
		this.name = name;
		this.location = new Point2D.Double(x, y);
	}
}
