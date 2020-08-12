package chiefarch;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/**
 * Parser utility to load the campus map dataset.
 */
public class CampusParser {
	/**
	 * A checked exception class for bad data files
	 */
	@SuppressWarnings("serial")
	public static class MalformedDataException extends Exception {
		public MalformedDataException() {
		}

		public MalformedDataException(String message) {
			super(message);
		}

		public MalformedDataException(Throwable cause) {
			super(cause);
		}

		public MalformedDataException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Reads the campus buildings dataset. Each line of the input file should contain the
	 * abbreviated name of a building, the full name, an x-coordinate, and a y-coordinate, with a
	 * single tab character separating each.
	 * 
	 * @param filename
	 *            : the file that will be read
	 * @return a map of abbreviated building names to Buildings
	 * @throws MalformedDataException
	 *             if the file is not well-formed: each line contains exactly
	 *             two tokens separated by a tab, or else starting with a #
	 *             symbol to indicate a comment line.
	 */
	public static Map<String, Building> parseBuildingData(String filename) throws MalformedDataException {
		BufferedReader reader = null;
		Map<String, Building> buildings = new HashMap<String, Building>();
		try {
			reader = new BufferedReader(new FileReader(filename));

			String inputLine;
			while ((inputLine = reader.readLine()) != null) {

				// Ignore comment lines.
				if (inputLine.startsWith("#")) continue;

				// Parse the data, throwing an exception for malformed lines.
				String[] tokens = inputLine.split("\t");
				if (tokens.length != 4) 
					throw new MalformedDataException("Missing tabs and/or data: " + inputLine);

				String shortName = tokens[0];
				String longName = tokens[1];
				Double x = Double.parseDouble(tokens[2]);
				Double y = Double.parseDouble(tokens[3]);

				// Create a new Building using parsed data and add mapping
				buildings.put(shortName, new Building(longName, x, y));
			}
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println(e.toString());
					e.printStackTrace(System.err);
				}
			}
		}
		return buildings;
	}
	
	/**
	 * Reads the campus paths dataset. For each endpoint of a path segment, there should be a line
	 * in the file listing the pixel coordinates of that point followed by a tab-indented line for
	 * each endpoint to which it is connected with a path segment:
	 * 
	 *	 x_1, y_1
	 *	 		x_2, y_2: distance_12
	 *	 		x_3, y_3: distance_13
	 *	 		...
	 *	 ...
	 *	 x_i, y_i
	 *	 		x_j, y_j: distance_ij
	 *	 		...
	 * 
	 * @param filename
	 *            : the file that will be read
	 * @return a map of endpoints (A) to maps of endpoints (B) to distances from A to B
	 * @throws MalformedDataException
	 *             if the file is not well-formed: each line contains exactly
	 *             two tokens separated by a tab, or else starting with a #
	 *             symbol to indicate a comment line.
	 */
	public static Map<Point2D.Double, Map<Point2D.Double, Double>> parsePathData(String filename) throws MalformedDataException {
		BufferedReader reader = null;
		Map<Point2D.Double, Map<Point2D.Double, Double>> paths = new HashMap<Point2D.Double, Map<Point2D.Double, Double>>();
		try {
			reader = new BufferedReader(new FileReader(filename));

			String inputLine = reader.readLine();
			while (inputLine != null) {

				// Ignore comment lines.
				if (inputLine.startsWith("#")) continue;

				// Parse the data, throwing an exception for malformed lines.
				String[] tokens = inputLine.split(",");
				if (tokens.length != 2) throw new MalformedDataException();

				Double x = Double.parseDouble(tokens[0]);
				Double y = Double.parseDouble(tokens[1]);
				
				// Create Point using parsed data and add mapping
				Point2D.Double point = new Point2D.Double(x, y);
				Map<Point2D.Double, Double> thisMap = new HashMap<Point2D.Double, Double>();
				paths.put(point, thisMap);
				
				// take care of endpoints to which this one is connected
				inputLine = reader.readLine();
				while (inputLine != null && inputLine.startsWith("\t")) {
					inputLine = inputLine.trim();
					inputLine = inputLine.replace(",", " ");
					inputLine = inputLine.replace(":", "");
					tokens = inputLine.split(" ");
					
					// throw an exception for malformed lines
					if (tokens.length != 3) throw new MalformedDataException();
					
					Double x_i = Double.parseDouble(tokens[0]);
					Double y_i = Double.parseDouble(tokens[1]);
					Double distance = Double.parseDouble(tokens[2]);
					
					// Create Points using parsed data and add mapping
					Point2D.Double point_i = new Point2D.Double(x_i, y_i);
					thisMap.put(point_i, distance);
					
					inputLine = reader.readLine();
				}
			}
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println(e.toString());
					e.printStackTrace(System.err);
				}
			}
		}
		return paths;
	}
}
