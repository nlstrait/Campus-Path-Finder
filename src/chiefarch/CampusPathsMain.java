package chiefarch;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import chiefarch.CampusMap;
import chiefarch.CampusParser.MalformedDataException;

import javafx.scene.shape.Circle;


/**
 * CampusPathsMain serves as main and generates GUI
 *
 * @author Nolan Strait
 */
public class CampusPathsMain {
	
	// paths for CampusPaths data
	private static final String DATA_PATH = "data/";
	private static final String BUILDINGS_PATH = DATA_PATH + "campus_buildings.dat";
	private static final String PATHS_PATH = DATA_PATH + "campus_paths.dat";
	
	/**
	 * MapPanel is a panel that represents a map and a path on said map
	 * 
	 * @author Nolan Strait
	 */
	private static class MapPanel extends JPanel {
		
		private List<Point2D.Double> path;
		private Image map;
		
		/**
		 * Creates a new MapPanel
		 * 
		 * @param filename : path to an image representing our map
		 */
		public MapPanel(String filename) {
			map = Toolkit.getDefaultToolkit().getImage(filename);	
			int width = 1024;
			int height = (int) Math.round(1024.0 / 4330 * 2964);
			this.setSize(width, height);
			this.setPreferredSize(new Dimension(width, height));
			repaint();
		}
		
		/**
		 * Sets a new path for this MapPanel
		 * 
		 * @param path : the path to eventually be drawn
		 */
		public void setPath(List<Point2D.Double> path) {
			this.path = path;
		}
		
		/**
		 * Clears the path stored in this MapPanel
		 */
		public void clearPath() { path = null; }
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			// draw map
			g2d.drawImage(map, 0, 0, this.getWidth(), this.getHeight(), this);                      
			
			// draw path (if there is one to be drawn)
			if (path == null) return;
			
			// account for aspect ratio of this MapPanel
			double xFactor = (double) this.getWidth() / map.getWidth(this);
			double yFactor = (double) this.getHeight() / map.getHeight(this);
			double compFactor = (double) (this.getWidth() + this.getHeight())
					/ (map.getWidth(this) + map.getHeight(this));
			
			// make sure our lines can be easily seen
			g2d.setStroke(new BasicStroke((int) Math.round(13 * compFactor)));
			g2d.setColor(Color.MAGENTA);
			
			// draw circles for starting and ending points
			int size = (int) Math.round(35 * compFactor);
			Point2D.Double src = path.get(0);
			Point2D.Double dest = path.get(path.size() - 1);
			int src_x = (int) Math.round(src.x * xFactor);
			int src_y = (int) Math.round(src.y * yFactor);
			int dest_x = (int) Math.round(dest.x * xFactor);
			int dest_y = (int) Math.round(dest.y * yFactor);
			g2d.fillOval(src_x - size / 2, src_y - size / 2, size, size);
			g2d.fillOval(dest_x - size / 2, dest_y - size / 2, size, size);
			
			// draw lines to make path
			for (int i = 0; i < path.size() - 1; i++) {
				src = path.get(i);
				dest = path.get(i + 1);
				src_x = (int) Math.round(src.x * xFactor);
				src_y = (int) Math.round(src.y * yFactor);
				dest_x = (int) Math.round(dest.x * xFactor);
				dest_y = (int) Math.round(dest.y * yFactor);
				g2d.drawLine(src_x, src_y, dest_x, dest_y);
			}
		}
	}
	
	/**
	 * PathButton is a JButton used to find a path
	 * 
	 * @author Nolan Strait
	 */
	private static class PathButton extends JButton implements ActionListener {
		private CampusMap campusMap;
		private MapPanel mapPanel;
		private JComboBox startList;
		private JComboBox endList;
		
		/**
		 * Creates a new PathButton
		 * 
		 * @param campusMap : the model to refer to
		 * @param mapPanel  : the panel on which the map is displayed
		 * @param startList : the component used to select the starting point
		 * @param endList   : the component used to select the ending point
		 */
		public PathButton(CampusMap campusMap, MapPanel mapPanel, JComboBox startList,
				JComboBox endList) {
			super("Find path!");
			this.addActionListener(this);
			this.campusMap = campusMap;
			this.mapPanel = mapPanel;
			this.startList = startList;
			this.endList = endList;
		} 

		@Override
		public void actionPerformed(ActionEvent e) {
			String start = (String) startList.getSelectedItem();
			String end = (String) endList.getSelectedItem();
			List<Point2D.Double> path = campusMap.findRoute(start, end);
			path.add(0, campusMap.locationOf(start));
			mapPanel.setPath(path);
			mapPanel.repaint();
		}
	}
	
	
	/**
	 * ResetButton is a JButton used to reset the GUI
	 * 
	 * @author Nolan Strait
	 */
	private static class ResetButton extends JButton implements ActionListener {
		private MapPanel mapPanel;
		private JComboBox startList;
		private JComboBox endList;
		
		/**
		 * Creates a new ResetButton
		 * 
		 * @param mapPanel : the panel on which the map is displayed
		 */
		public ResetButton(MapPanel mapPanel, JComboBox startList, JComboBox endList) {
			super("Reset");
			this.mapPanel = mapPanel;
			this.startList = startList;
			this.endList = endList;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mapPanel.clearPath();
			startList.setSelectedIndex(0);
			endList.setSelectedIndex(0);
			mapPanel.repaint();
		}
	}

	
	public static void main(String[] args) {
		// initialize new CampusMap
		CampusMap campusMap;
		try {
			campusMap = new CampusMap(BUILDINGS_PATH, PATHS_PATH);
		} catch (MalformedDataException e) {
			System.out.println("Bad building/paths file(s)");
			return;
		}
		List<String> buildings = campusMap.getBuildingAbbrevs();
		
		// initialize top-level container
		JFrame frame = new JFrame("Campus Path Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		
		// create panel for map and paths
		MapPanel mapPanel = new MapPanel(DATA_PATH + "campus_map.jpg");
		
		// create panel for menu and add buttons
		JPanel menu = new JPanel();
		JComboBox startList = new JComboBox(buildings.toArray());
		JComboBox endList = new JComboBox(buildings.toArray());
		PathButton pathButton = new PathButton(campusMap, mapPanel, startList, endList);
		ResetButton resetButton = new ResetButton(mapPanel, startList, endList);
		menu.add(startList);
		menu.add(endList);
		menu.add(pathButton);
		menu.add(resetButton);
		
		
		// create content pane
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(mapPanel, BorderLayout.CENTER);
		contentPane.add(menu, BorderLayout.SOUTH);
		frame.setContentPane(contentPane);
		
		// make visible after doing all the work
		frame.setVisible(true);
	}
}
