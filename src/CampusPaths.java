package chiefarch;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Scanner;

import chiefarch.CampusParser.MalformedDataException;

/**
 * CampusPaths contains a main for interacting with a user to find paths between buildings on campus.
 * CampusPaths is a mix of view and controller components.
 * 
 * @author Nolan
 */
public class CampusPaths {
	
	// paths for CampusPaths data
	private static final String DATA_PATH = "data/";
	private static final String BUILDINGS_PATH = DATA_PATH + "campus_buildings.dat";
	private static final String PATHS_PATH = DATA_PATH + "campus_paths.dat";
	
	private static CampusMap map;
	
	/**
	 * Prints a list of options for user input
	 */
	private static void printMenu() {
		System.out.println(
				"Menu:"
			  + "\n\tr to find a route"
			  + "\n\tb to see a list of all buildings"
			  + "\n\tq to quit\n"
		);
	}
	
	/**
	 * Prints a list of buildings and their abbreviations
	 */
	private static void listBuildings() {
		System.out.println("Buildings:");
		
		List<String> abbrevs = map.getBuildingAbbrevs();
		for (String abbrev : abbrevs)
			System.out.println("\t" + abbrev + ": " + map.getFullName(abbrev));
		System.out.println();
	}
	
	/**
	 * Takes user input and prints the path between two specified buildings
	 * 
	 * @param scanner : to request user input
	 */
	private static void findRoute(Scanner scanner) {
		
		// get buildings via user input
		System.out.print("Abbreviated name of starting building: ");
		String start = scanner.nextLine();
		System.out.print("Abbreviated name of ending building: ");
		String end = scanner.nextLine();
		
		// check that these buildings are valid
		boolean unknown = false;
		if (!map.hasBuilding(start)) {
			System.out.println("Unknown building: " + start);
			unknown = true;
		}
		if (!map.hasBuilding(end)) {
			System.out.println("Unknown building: " + end);
			unknown = true;
		}
		if (unknown) {
			System.out.println();
			return;
		}
		
		// declare path explored
		System.out.println("Path from " + map.getFullName(start) + " to " + map.getFullName(end) + ":");
		
		// get path and initialize variables for iteration
		List<Point2D.Double> path = map.findRoute(start, end);
		Double totalDistance = 0.0;
		Point2D.Double src = map.locationOf(start);
		
		// iterate over path members and print the deets
		for (Point2D.Double dest : path) {
			Double distance = map.distanceBetween(src, dest);
			String direction = CampusMap.getDirection(src, dest);
			
			System.out.println("\tWalk " + String.format("%d", Math.round(distance)) + " feet " + direction
					+ " to " + String.format("(%d, %d)", Math.round(dest.x), Math.round(dest.y)));
			
			src = dest;
			totalDistance += distance;
		}
		
		// report total distance
		System.out.println("Total distance: " + String.format("%d", Math.round(totalDistance)) + " feet\n");
	}
	
	public static void main(String[] args) {
		try {
			map = new CampusMap(BUILDINGS_PATH, PATHS_PATH);
		} catch (MalformedDataException e) {
			e.printStackTrace();
			System.out.println("invalid files");
			return;
		}
		
		printMenu();
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter an option ('m' to see the menu): ");
		String input;
		if (scanner.hasNext()) {
			input = scanner.nextLine();
		} else {
			scanner.close();
			return;
		}
		
		while (!input.equals("q")) {
			if (input.equals("") || input.startsWith("#")) {
				System.out.println(input);
				if (scanner.hasNext()) {
					input = scanner.nextLine();
					continue;
				}
				else break;
			}
			
			else if (input.equals("b")) listBuildings();
			else if (input.equals("r")) findRoute(scanner);
			else if (input.equals("m")) printMenu();
			
			else System.out.println("Unknown option\n");
			
			System.out.print("Enter an option ('m' to see the menu): ");
			if (scanner.hasNext()) input = scanner.nextLine();
			else break;
		}
		
		scanner.close();
	}
}
