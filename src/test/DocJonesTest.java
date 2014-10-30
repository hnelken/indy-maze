package test;

import main.hrn10.docJones.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

public class DocJonesTest {

	Pillar[][] temple;
	UndirectedGraph<Pillar,Plank> layout;
	DocJones.Test tester = new DocJones.Test();
	
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
	
	private void noEdgeSetup(int rows, int cols) {
		temple = new Pillar[rows][cols];
		layout = new SimpleGraph<Pillar, Plank>(Plank.class);
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				temple[i][j] = new Pillar(i, j);
				layout.addVertex(temple[i][j]);
			}
		}
	}
	
	private void simpleCase(int rows, int cols) {
		createTemple(rows, cols, false);
		int i = temple.length - 1;
		for (int j = temple.length - 1; j > 0; j--) {
			Plank p = layout.getEdge(temple[i][j], temple[i][j - 1]);
			p.setUsable(true);
		}
		for ( ; i > 0; i--) {
			Plank p = layout.getEdge(temple[i][0], temple[i - 1][0]);
			p.setUsable(true);
		}
	}
	
	private void initTemple() {
		for (Pillar p : layout.vertexSet()) {
			p.initPillar(layout);
		}
	}
	
	/**
	 * Tests the indy method (main algorithm implementation)
	 * Covers the single structured basis test required
	 */
	@Test public void testIndy() {
		createTemple(6, 6, true);
		initTemple();
		
		List<Pillar> path = DocJones.indy(6, 6, layout);
		assertEquals("Exit is in path", temple[0][0], path.get(path.size() - 1));
		assertEquals("Path length check", 11, path.size());
	}
	
	/**
	 * Tests the private getExit method
	 * Covers the necessary structured basis tests
	 * The line "if (p.isExit())" evaluates to true and false
	 * multiple times in the process of the looping method, 
	 * and therefore does not need it's own test case.
	 */
	@Test public void testGetExit() {
		simpleCase(4, 4);
		initTemple();
		
		//Structured Basis
		assertEquals("A simple temple has an exit", temple[0][0], tester.getExit(layout));
		
		layout = new SimpleGraph<Pillar, Plank>(Plank.class);
		
		//Structured Basis
		assertEquals("No pillars in temple --> no exit", null, tester.getExit(layout));
	}
	
	/**
	 * Tests the private getStart method
	 * Covers the necessary structured basis tests
	 * The line "if (p.isStart())" evaluates to true and false
	 * multiple times in the process of the looping method,
	 * and therefore does not need it's own test case.
	 */
	@Test public void testGetStart() {
		simpleCase(4, 4);
		initTemple();
		
		//Structured Basis
		assertEquals("A simple temple has an exit", temple[3][3], tester.getStart(4, 4, layout));
		
		layout = new SimpleGraph<Pillar, Plank>(Plank.class);
		
		//Structured Basis
		assertEquals("No pillars in temple --> no exit", null, tester.getExit(layout));
	}
	
	/**
	 * Tests the assembleFalseValues method
	 * Covers structured basis and data flow tests
	 * The line "if (processed.contains(neighbor))"
	 * evaluates to true and false multiple times
	 * during the nominal case, and doesn't need
	 * a separate test case to cover its functionality.
	 */
	@Test public void testAssembleFalseValues() {
		Result[][] docTrue = new Result[4][4];
		Result[][] docFalse = new Result[4][4];
		
		noEdgeSetup(4, 4);			//BFS is impossible
		Pillar exit = temple[0][0];
	
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - "while(bfi.hasNext())" skipped initially
		assertEquals("Exit has path length", 1, docFalse[0][0].getValue());
		assertEquals("All other nodes (including start) not evaluated", null, docFalse[3][3]);
		
		createTemple(4, 4, false);		//no usable planks
		initTemple();
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - "for(Pillar neighbor : curr.getReachable())" skipped initially
		assertEquals("Exit has path length", 1, docFalse[0][0].getValue());
		assertEquals("Other nodes have zero path length", 0, docFalse[3][3].getValue());
		assertEquals("Other nodes have zero path length", 0, docFalse[1][1].getValue());
		
		createTemple(4, 4, true);		//all neighboring pillars are reachable in this case
		initTemple();
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - Nominal Case
		assertEquals("Start pillar has minimum length stored", 7, docFalse[3][3].getValue());
	}
	
	/**
	 * Tests the assembleTrueValues method
	 * Covers structured basis and data flow tests
	 * The line "if (processed.contains(neighbor))"
	 * evaluates to true and false multiple times
	 * during the nominal case, and doesn't need
	 * a separate test case to cover its functionality.
	 */
	@Test public void testAssembleTrueValues() {
		Result[][] docTrue = new Result[4][4];
		Result[][] docFalse = new Result[4][4];
		
		noEdgeSetup(4, 4);			//BFS is impossible
		Pillar exit = temple[0][0];
	
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		docTrue = tester.assembleTrueValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - "while(bfi.hasNext())" skipped initially
		assertEquals("Exit has path length", 1, docTrue[0][0].getValue());
		assertEquals("All other nodes (including start) not evaluated", null, docTrue[3][3]);
		
		createTemple(4, 4, false);		//no usable planks
		initTemple();
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		docTrue = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - "for(Pillar neighbor : curr.getReachable())" skipped initially
		assertEquals("Exit has path length", 1, docTrue[0][0].getValue());
		assertEquals("Other nodes have zero path length", 0, docTrue[3][3].getValue());
		assertEquals("Other nodes have zero path length", 0, docTrue[1][1].getValue());
		
		createTemple(4, 4, true);		//all neighboring pillars are reachable in this case
		initTemple();
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		
		//Structured Basis - Nominal Case
		assertEquals("Start pillar has minimum length stored", 7, docTrue[3][3].getValue());
	}
	
	/**
	 * Tests the pathLengthResult method
	 * Covers the standard basis tests
	 * No additional tests required.
	 */
	@Test public void testPathLengthResult() {
		List<Result> results = new ArrayList<Result>();
		Result result = tester.pathLengthResult(results);
		
		//Structured Basis - both if statements are true
		assertEquals("Results list was empty", 0, result.getValue());
		assertEquals("Results list was empty", null, result.getPillar());
		
		Result five = new Result(5, null);
		Result two = new Result(2, null);
		Result nine = new Result(9, null);
		
		results.add(five);
		results.add(two);
		results.add(nine);
		
		//Structured Basis - both if statements are false
		result = tester.pathLengthResult(results);
		assertEquals("Smallest path length plus one", 3, result.getValue());
	}
	
	/**
	 * Tests the assemblePath method (1/2)
	 * The structured basis test of the nominal case is covered
	 * Assumes the path is clearly identified from assembling values
	 */
	@Test public void testAssemblePathNominal() {
		createTemple(3, 3, true);
		initTemple();
		Pillar exit = temple[0][0];
		Result[][] docTrue = new Result[3][3];
		Result[][] docFalse = new Result[3][3];
		
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		docTrue = tester.assembleTrueValues(exit, layout, docFalse, docTrue);
		List<Pillar> path = tester.assemblePath(tester.getStart(3, 3, layout), docTrue);
		
		assertTrue("The exit is in the path", path.contains(exit));
	}
	
	/**
	 * Tests the assemblePath method (2/2)
	 * The structured basis test with the only if statement being false is covered
	 */
	@Test public void testAssemblePathAlternate() {
		createTemple(1, 1, true);
		initTemple();
		Pillar exit = temple[0][0];
		Result[][] docTrue = new Result[1][1];
		Result[][] docFalse = new Result[1][1];
		
		docFalse = tester.assembleFalseValues(exit, layout, docFalse, docTrue);
		docTrue = tester.assembleTrueValues(exit, layout, docFalse, docTrue);
		List<Pillar> path = tester.assemblePath(tester.getStart(1, 1, layout), docTrue);
		
		assertTrue("The exit is in the path", path.contains(exit));
		assertTrue("The size of the path is 1", path.size() == 1);
	}
	
	/**
	 * Tests the baseCaseResult method
	 * Covers the two structured basis cases
	 * No additional tests needed
	 */
	@Test public void testBaseCaseResult() {
		Pillar exit = new Pillar(0, 0);
		Result test = new Result(1, null);
		
		//Structured Basis
		assertEquals("Value is 1, same result returned", test, tester.baseCaseResult(exit, test));
		
		test = tester.baseCaseResult(exit, new Result(0, null));
		
		//Structured Basis
		assertEquals("Value is not 1, result with value 1 is returned", 1, test.getValue());
	}
}
