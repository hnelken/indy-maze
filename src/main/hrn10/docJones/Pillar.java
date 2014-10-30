package main.hrn10.docJones;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;

/**
 * Represents a pillar in the gridded temple used in the DocJones algorithm
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class Pillar {
	
	private int row;			//row index in grid
	private int column;			//column index in grid
	private List<Pillar> neighbors;		//the list of all neighbors
	private List<Pillar> reachable;		//the list of reachable neighbors
	
	/**
	 * Constructor to make a pillar in a grid.
	 * @param row The row index in the grid
	 * @param column The column index in the grid
	 */
	public Pillar(int row, int column) {
		this.row = row;
		this.column = column;
		neighbors = new ArrayList<Pillar>();
		reachable = new ArrayList<Pillar>();
	}
	
	/**
	 * Initializes the pillars lists of neighbors given a layout graph
	 * @param layout A graph of connections between pillars
	 */
	public void initPillar(UndirectedGraph<Pillar, Plank> layout) {
		if (layout != null) {
			Set<Plank> planks = layout.edgeSet();
			for(Plank plank : planks) {			//search all edges
				if (plank.containsPillar(this)) {	//if this pillar is involved
					if (plank.isUsable()) {				//and this edge is usable
						reachable.add(plank.targetOfPillar(this));	//add reachable neighbor
					}
					neighbors.add(plank.targetOfPillar(this));	//add to list of neighbors
				}
			}
		}
	}
	
	/**
	 * Returns whether or not this pillar is the exit of the temple
	 * @return whether or not this is the exit
	 */
	public boolean isExit() {
		return ((row == 0) && (column == 0));
	}
	
	/**
	 * Returns whether this pillar is the start of the temple
	 * @param rows The number of rows in the temple
	 * @param columns The number of columns in the temple
	 * @return True if this pillar is the bottom right of the grid
	 */
	public boolean isStart(int rows, int columns) {
		return (row == rows - 1 && column == columns - 1);
	}

	/**
	 * Returns this pillars row index
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Return this pillars column index
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Returns the list of all this pillars neighbors
	 * @return the neighbors
	 */
	public List<Pillar> getNeighbors() {
		return neighbors;
	}

	/**
	 * Returns the list of this pillars reachable neighbors
	 * @return the list of reachable neighbors
	 */
	public List<Pillar> getReachable() {
		return reachable;
	}
	
	/**
	 * Determines equality based on row and column indexes
	 */
	@Override public boolean equals(Object o) {
		if (o instanceof Pillar) {
			return this.equals((Pillar)o);
		}
		else {
			return false;
		}
	}
	
	public boolean equals(Pillar p) {
		if (null == p) {
			return false;
		}
		else {
			return ((this.getRow() == p.getRow()) && (this.getColumn() == p.getColumn()));
		}
	}
	
	@Override public int hashCode() {
		int hash = 13;
		hash += row + column;
		return hash;
	}
	
}
