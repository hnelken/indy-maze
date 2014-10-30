package test;

import main.hrn10.docJones.*;
import static org.junit.Assert.*;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * Tests the Pillar class used in the DocJones algorithm
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class PillarTest {
	
	private Pillar[][] temple;
	private UndirectedGraph<Pillar, Plank> layout;

	/**
	 * A method used for creating a testable grid of pillars
	 * @param rows The number of rows in the grid
	 * @param columns The number of columns in the grid
	 * @param full True if all neighbors to one pillar should be reachable
	 */
	private void createTemple(int rows, int columns, boolean full) {
		temple = new Pillar[rows][columns];
		layout = new SimpleGraph<Pillar, Plank>(Plank.class);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Pillar p = new Pillar(i, j);
				layout.addVertex(p);
				temple[i][j] = p;
			}
		}
		for (Pillar pillar : layout.vertexSet()) {
			addPlanks(pillar, rows, columns, full);
		}
	}
	
	/**
	 * Adds a plank object for each direction (n, s, w, e) if there is a neighboring pillar there
	 * @param pillar The pillar whose neighbors will be checked
	 * @param rows The number of rows in the grid
	 * @param columns The number of columns in the grid
	 * @param full True if all neighbors to one pillar should be reachable
	 */
	private void addPlanks(Pillar pillar, int rows, int columns, boolean full) {
		int row = pillar.getRow();
		int col = pillar.getColumn();
		Plank add = null;
		
		if (row - 1 >= 0) {
			if (full) {
				add = new Plank(pillar, temple[row - 1][col], true);
			}
			else {
				add = new Plank(pillar, temple[row - 1][col], false);
			}
			layout.addEdge(pillar, temple[row - 1][col], add);
		}
		if (row + 1 < rows) {
			if (full) {
				add = new Plank(pillar, temple[row + 1][col], true);
			}
			else {
				add = new Plank(pillar, temple[row + 1][col], false);
			}
			layout.addEdge(pillar, temple[row + 1][col], add);
		}
		if (col - 1 >= 0) {
			if (full) {
				add = new Plank(pillar, temple[row][col - 1], true);
			}
			else {
				add = new Plank(pillar, temple[row][col - 1], false);
			}
			layout.addEdge(pillar, temple[row][col - 1], add);
		}
		if (col + 1 < columns) {
			if (full) {
				add = new Plank(pillar, temple[row][col + 1], true);
			}
			else {
				add = new Plank(pillar, temple[row][col + 1], false);
			}
			layout.addEdge(pillar, temple[row][col + 1], add);
		}
	}
	
	/**
	 * Covers the structured basis and data flow tests of the constructor
	 */
	@Test public void testConstructor() {
		Pillar p = new Pillar(3, 5);
		
		//Structured Basis
		assertTrue("Constructor creates a pillar", p instanceof Pillar);
		
		//Data Flow
		assertEquals("Data is assigned correctly", 3, p.getRow());
		assertEquals("Data is assigned correctly", 5, p.getColumn());
	}
	
	/**
	 * Covers structured basis tests of the initPillar method
	 * Utilizes reachable and unreachable planks, as well as planks not containing this pillar.
	 * The data flow tests regarding the layout argument are covered in the structured basis tests
	 */
	@Test public void testInitPillar() {
		createTemple(3, 3, true);
		temple[1][1].initPillar(layout);
		
		//Structured basis - nominal case
		assertTrue("A reachable neighbor is assembled", temple[1][1].getReachable().contains(temple[0][1]));
		assertTrue("Reachable neighbors are normal neighbors", temple[1][1].getNeighbors().contains(temple[0][1]));
		
		createTemple(3, 3, false);
		temple[1][1].initPillar(layout);
		
		//Structured basis - No reachable neighbors, all neighbors still filled
		assertTrue("No reachable neighbors", temple[1][1].getReachable().isEmpty());
		assertFalse("Unreachable neighbors assembled", temple[1][1].getNeighbors().isEmpty());
		
		Pillar p = new Pillar(3, 3);
		layout = new SimpleGraph<Pillar, Plank>(Plank.class);
		p.initPillar(layout);
		
		//Structured basis - Unfilled graph of planks, no edges to sift through
		assertTrue("No neighbors", p.getReachable().isEmpty());
		assertTrue("No neighbors", p.getNeighbors().isEmpty());
		
		//Structured basis - null layout argument escapes whole init process
		p.initPillar(null);
		assertTrue("No neighbors", p.getReachable().isEmpty());
		assertTrue("No neighbors", p.getNeighbors().isEmpty());
	}
	
	/**
	 * Tests the isExit method
	 * Covers the structured basis tests and equality boundaries
	 */
	@Test public void testIsExit() {
		Pillar exit = new Pillar(0, 0);
		Pillar notExit = new Pillar(2, 3);
		Pillar rowLT = new Pillar(-1, 0);
		Pillar rowGT = new Pillar(1, 0);
		Pillar colLT = new Pillar(0, -1);
		Pillar colGT = new Pillar(0, 1);
		
		//Structured Basis
		assertTrue("Both sides of && satisfied, is the exit", exit.isExit());
		assertFalse("Both sides of && unsatisfied, not the exit", notExit.isExit());
		
		//Boundary
		assertFalse("Upper boundary on row requirement", rowGT.isExit());
		assertFalse("Lower boundary on row requirement", rowLT.isExit());
		assertFalse("Lower boundary on col requirement", colLT.isExit());
		assertFalse("Upper boundary on col requirement", colGT.isExit());
	}
	
	/**
	 * Tests the isStart method
	 * Assumes a grid size of 4 x 4
	 * Covers the structured basis tests and equality boundaries
	 */
	@Test public void testIsStart() {
		Pillar notStart = new Pillar(1, 7);
		Pillar start = new Pillar(3, 3);
		Pillar rowGT = new Pillar(4, 3);
		Pillar rowLT = new Pillar(2, 3);
		Pillar colGT = new Pillar(3, 2);
		Pillar colLT = new Pillar(3, 4);
		
		//Structured Basis
		assertTrue("Both sides of && satisfied, is start", start.isStart(4, 4));
		assertFalse("Both sides of && unsatisfied, not start", notStart.isStart(4,4));
		
		//Boundary
		assertFalse("Upper boundary on row requirement", rowGT.isStart(4, 4));
		assertFalse("Lower boundary on row requirement", rowLT.isStart(4, 4));
		assertFalse("Lower boundary on col requirement", colLT.isStart(4, 4));
		assertFalse("Upper boundary on col requirement", colGT.isStart(4, 4));
	}
	
	/**
	 * Tests the only code path getRow can follow
	 * No additional tests needed besides structured basis.
	 */
	@Test public void testGetRow() {
		Pillar p = new Pillar(23, 4);
		
		//Structured Basis
		assertEquals("Nominal Case", 23, p.getRow());
	}
	
	/**
	 * Tests the only code path getColumn can follow
	 * No additional tests needed besides structured basis.
	 */
	@Test public void testGetColumn() {
		Pillar p = new Pillar(23, 4);
		
		//Structured Basis
		assertEquals("Nominal Case", 4, p.getColumn());
	}
	
	/**
	 * Covers the structured basis tests for the getNeighbors method
	 * Only one code path possible, no additional tests needed
	 */
	@Test public void testGetNeighbors() {
		createTemple(3, 3, true);
		temple[0][0].initPillar(layout);
		
		//Structured Basis
		assertFalse("Initialized pillar has neighbors", temple[0][0].getNeighbors().isEmpty());
	}
	
	/**
	 * Covers the structured basis tests for the getReachable method
	 * Only one code path possible, no additional tests needed
	 */
	@Test public void testGetReachable() {
		createTemple(3, 3, true);
		temple[0][0].initPillar(layout);
		
		//Structured Basis
		assertFalse("Initialized pillar has reachable neighbors", temple[0][0].getReachable().isEmpty());
	}
	
	/**
	 * Tests the overridden equals method that checks for type equality first
	 * Structured basis tests and boundaries of equality are covered.
	 */
	@Test public void testEquals() {
		Integer i = new Integer(3);
		Pillar p = new Pillar(3, 3);
		Pillar eq = new Pillar(3,3);
		Pillar diff = new Pillar(2, 1);
		
		//Structured Basis
		assertFalse("Compared to a non-pillar object", p.equals(i));
		
		//Structured Basis / Boundary
		assertTrue("Two pillars can be equal", p.equals(eq));
		
		//Boundary
		assertFalse("Two pillars can be different", p.equals(diff));
	}
	
	/**
	 * Tests the equals method that takes a pillar object
	 * Covers the structured basis tests and equality boundaries.
	 */
	@Test public void testPillarEquals() {
		Pillar p = new Pillar(3, 3);
		Pillar eq = new Pillar(3, 3);
		Pillar diff = new Pillar(1, 4);
		Pillar rowLT = new Pillar(1, 3);
		Pillar rowGT = new Pillar(4, 3);
		Pillar colLT = new Pillar(3, 1);
		Pillar colGT = new Pillar(3, 4);
		
		//Structured Basis
		assertFalse("Null comparison", p.equals(null));

		//Structured Basis / Boundary
		assertTrue("Both sides of && satisfied, equal", p.equals(eq));
		assertFalse("Both sides of && not satisfied, not equal", p.equals(diff));
		
		//Boundary
		assertFalse("Compared pillar's row is less than test pillar", p.equals(rowLT));
		assertFalse("Compared pillar's row is greater than test pillar", p.equals(rowGT));
		assertFalse("Compared pillar's column is less than test pillar", p.equals(colLT));
		assertFalse("Compared pillar's column is greater than test pillar", p.equals(colGT));
	}

}
