package main.hrn10.docJones;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

/* Pseudocode implementation */

/**
 * Implementation of my own DocJones algorithm
 * Given a graph that represents a grid of pillars and connecting planks,
 * finds the shortest path between the bottom right corner of the grid
 * to the top left corner of the grid using only pillars reachable through
 * user designated planks that cross gaps. In the event a shorter path can 
 * be found through crossing a single gap without a plank, an extra plank 
 * held in the arms of Indiana Jones is employed. The algorithm assumes 
 * that every pillar in the graph has a plank to its neighbors at the 
 * north, west, south, and east positions relative to it in the grid 
 * and the planks that Indy can cross have an enabled 'usable' flag.
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class DocJones {
	
	/**
	 * Finds the shortest path from the start to the exit of the temple
	 * This is done with or without the use of an extra plank
	 * @param rows The number of rows in the temple representation
	 * @param columns The number of columns in the temple representation
	 * @param layout The graph of connections between the pillars that make up the temple
	 * @return A list of Pillar objects in the order of the shortest path using my DocJones algorithm
	 */
	public static List<Pillar> indy(int rows, int columns, UndirectedGraph<Pillar, Plank> layout) {
		Pillar exit = getExit(layout);
		Result[][] docTrue = new Result[rows][columns];
		Result[][] docFalse = new Result[rows][columns];
		
		docFalse = assembleFalseValues(exit, layout, docFalse, docTrue);
		
		docTrue = assembleTrueValues(exit, layout, docFalse, docTrue);
		
		return assemblePath(getStart(rows, columns, layout), docTrue);
	}
	
	/**
	 * Finds the exit to the temple
	 * @param layout The graph of all pillars in the temple
	 * @return The pillar at row 0, column 0 in the temple
	 */
	private static Pillar getExit(UndirectedGraph<Pillar, Plank> layout) {
		Set<Pillar> temple = layout.vertexSet();
		for (Pillar p : temple) {	//peruse all pillars in temple
			if (p.isExit()) {		//return if exit is found
				return p;		
			}
		}
		return null;
	}
	
	/**
	 * Finds the entrance to the temple
	 * @param rows The number of rows in the temple
	 * @param columns The number of columns in the temple
	 * @param layout The graph of all pillars in the temple
	 * @return The pillar at the maximum row and column index
	 */
	private static Pillar getStart(int rows, int columns, UndirectedGraph<Pillar, Plank> layout) {
		Set<Pillar> temple = layout.vertexSet();
		for (Pillar p : temple) {		//peruse all pillars in temple
			if (p.isStart(rows, columns)) {		//return if start is found
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Saves all shortest path lengths using no extra plank to a 2D array
	 * @param exit The exit of the temple
	 * @param layout The graph of all pillars in the temple
	 * @param docFalse The 2D array that will store the path lengths with no extra plank
	 * @param docTrue The 2D array that will store the path lengths with an extra plank
	 * @return A 2D array filled with path lengths from corresponding indexes 
	 */
	private static Result[][] assembleFalseValues(Pillar exit, 
								UndirectedGraph<Pillar, Plank> layout,
								Result[][] docFalse, Result[][] docTrue) {
		
		Pillar curr = exit;
		List<Pillar> processed = new ArrayList<Pillar>();
		GraphIterator<Pillar, Plank> bfi = new BreadthFirstIterator<Pillar, Plank>(layout, exit);
		docFalse[0][0] = new Result(1, exit);		// base case
		processed.add(exit);

		while (bfi.hasNext()) {							// peruse all pillars in BFS order
			docFalse[0][0] = baseCaseResult(exit, docFalse[0][0]);		//ensure base case is established
			curr = bfi.next();									//consider the next pillar in the BFS 
			List<Result> results = new ArrayList<Result>();	//prepare to store the path lengths of neighboring paths
															//then
			for (Pillar neighbor : curr.getReachable()) {	//foreach adjacent node that's reachable
				if (processed.contains(neighbor)) {		  // and has not yet been processed by the algorithm
					results.add(getResult(curr, neighbor, docTrue, docFalse, false));	//save the path length
																					//from the given neighbor
				}
			}
			docFalse[curr.getRow()][curr.getColumn()] = pathLengthResult(results);	//decide the shortest path from this pillar
			processed.add(curr);												//consider the current pillar as processed
		}
		docFalse[0][0] = baseCaseResult(exit, docFalse[0][0]);		//reestablish base case
		return docFalse;
	}
	
	/**
	 * Saves all shortest path lengths with the option of an extra plank in a 2D array
	 * @param exit The pillar that is exit of the temple
	 * @param layout The graph of all pillars in the temple
	 * @param docFalse The 2D array that will store the path lengths with no extra plank
	 * @param docTrue The 2D array that will store the path lengths with an extra plank
	 * @return A 2D array filled with path lengths from corresponding indexes 
	 */
	private static Result[][] assembleTrueValues(Pillar exit,
								UndirectedGraph<Pillar, Plank> layout, 
								Result[][] docFalse, Result[][] docTrue) {
		
		GraphIterator<Pillar, Plank> bfi = new BreadthFirstIterator<Pillar, Plank>(layout, exit);
		List<Pillar> processed = new ArrayList<Pillar>();
		
		Pillar curr = exit;					//begin BFS from exit
		docTrue[0][0] = new Result(1, exit);		//set base case (distance from exit is 1)
		processed.add(exit);				//consider the exit processed
		
		while (bfi.hasNext()) {		//peruse all pillars in BFS order
			docTrue[0][0] = baseCaseResult(exit, docTrue[0][0]);	//ensure base case is set
			
			curr = bfi.next();	//consider the next in the BFS
			List<Result> results = new ArrayList<Result>(); //prepare to make a list of nearby path lengths
																	//then
			for (Pillar neighbor : curr.getNeighbors()) {		//for each neighboring node
				if (processed.contains(neighbor)) { 			//that's value hasn't been calculated
					results.add(getResult(curr, neighbor, docTrue, docFalse, true));	//save resulting
				}																	//path length to list
			}
			//store 1 + the shortest nearby path length, or zero if exit is unreachable from current pillar
			docTrue[curr.getRow()][curr.getColumn()] = pathLengthResult(results);
			processed.add(curr);	//consider the current pillar being considered as processed
		}
		docTrue[0][0] = baseCaseResult(exit, docTrue[0][0]);
		return docTrue;
	}
	
	/**
	 * Saves the path length of a given pillar neighboring the current pillar
	 * @param curr The current pillar in the BFS
	 * @param neighbor The neighbor pillar currently being processed
	 * @param results The current list of results from previous neighbors
	 * @param docTrue The 2D array of path lengths calculated using an extra plank
	 * @param docFalse The 2D array of path lengths calculated using no extra plank
	 * @param hasPlank Whether or not a plank is available for use in this result calculation
	 * @return The list of results with the added result from the considered neighbor if result was nonzero
	 */
	private static Result getResult(Pillar curr, Pillar neighbor, Result[][] docTrue, Result[][] docFalse, boolean hasPlank) {
		Result result;
		if (hasPlank) {	// if a plank is available for use
			if (curr.getReachable().contains(neighbor)) {	// and this neighbor is reachable
				result = docTrue[neighbor.getRow()][neighbor.getColumn()];	//save the result from docTrue
			}
			else { 	//else neighbor is UNreachable
				result = docFalse[neighbor.getRow()][neighbor.getColumn()];	//save the result from docFalse
			}
		}
		else {	//else extra plank has already been used
			result = docFalse[neighbor.getRow()][neighbor.getColumn()];	//save the result from docFalse

		}
		if (result.getValue() != 0) {		//if the result is nonzero
			return new Result(result.getValue(), neighbor);
		}
		return new Result(0, neighbor);		//else associate 0 path length with the neighbor
	}
	
	/**
	 * Returns a result with the value of the shortest path neighboring a given pillar plus one
	 * Will return zero if the exit is unreachable from the given pillar
	 * @param results The final list of path lengths of neighboring paths
	 * @return A result with the given pillars path length and a pointer to the next pillar in the path
	 */
	private static Result pathLengthResult(List<Result> results) {
		Collections.sort(results);		//sort the results so the smallest is first in the list
		//if the list is empty, set path length to 0 (exit can't be reached from here)
		//otherwise return the minimum path length. The value will never be zero.
		Result result = (results.size() == 0) ? new Result(0, null) : results.get(0);
		if (result.getValue() == 0) {
			return result;			//if the value is zero, return this
		}
		else {		//else if value of path length is not zero, return 1 + shortest neighboring path length
			return new Result(1 + result.getValue(), result.getPillar());	//next pillar from here in shortest path is set
		}
	}
	
	/**
	 * Assembles the shortest path from the start to the exit of the temple
	 * @param start The pillar that is the entrance to the temple
	 * @param exit The pillar that is the exit from the temple
	 * @return A list of connected pillars 
	 */
	private static List<Pillar> assemblePath(Pillar start, Result[][] docTrue) {
		List<Pillar> path = new LinkedList<Pillar>();
		Result curr = docTrue[docTrue.length - 1][docTrue[0].length - 1];
		
		path.add(start);		//add the start to the path
		while (curr != docTrue[0][0]) {		//while we havent reached the exit
			Pillar next = curr.getPillar();		//consider the next pillar in the path
			path.add(next);		//add the next pillar in the path to the list
			curr = docTrue[next.getRow()][next.getColumn()];	//update current pillar
		}
		//once the exit has been reached, the shortest path will have been assembled
		return path;	//this path includes the exit
	}
	
	/**
	 * This reestablishes the base case after it is altered from the course of the algorithm
	 * @param exit The exit of the temple
	 * @param doc The array of values being assembled
	 * @return A result with value one that points to the exit
	 */
	private static Result baseCaseResult(Pillar exit, Result doc) {
		if (doc.getValue() != 1) {
			return new Result(1, exit);		//if the value is no longer 1
		}									//make it so
		else {								//otherwise leave it alone
			return doc;
		}
	}
	
	/**
	 * Nested class used for calling private methods from JUnit test cases
	 * @author Harry Nelken (hrn10@case.edu)
	 */
	public static class Test {
		public Test() {
			super();
		}
		
		public Pillar getExit(UndirectedGraph<Pillar, Plank> layout) {
			return DocJones.getExit(layout);
		}
		
		public Pillar getStart(int rows, int cols, UndirectedGraph<Pillar, Plank> layout) {
			return DocJones.getStart(rows, cols, layout);
		}
		
		public Result[][] assembleFalseValues(Pillar exit, 
				UndirectedGraph<Pillar, Plank> layout,
				Result[][] docFalse, Result[][] docTrue) {
			return DocJones.assembleFalseValues(exit, layout, docFalse, docTrue);
		}
		
		public Result[][] assembleTrueValues(Pillar exit,
				UndirectedGraph<Pillar, Plank> layout, 
				Result[][] docFalse, Result[][] docTrue) {
			return DocJones.assembleTrueValues(exit, layout, docFalse, docTrue);
		}
		
		public Result getResult(Pillar curr, Pillar neighbor, 
				Result[][] docTrue, Result[][] docFalse, boolean hasPlank) {
			return DocJones.getResult(curr, neighbor, docTrue, docFalse, hasPlank);
		}
		
		public Result pathLengthResult(List<Result> results) {
			return DocJones.pathLengthResult(results);
		}
		
		public List<Pillar> assemblePath(Pillar start, Result[][] docTrue) {
			return DocJones.assemblePath(start, docTrue);
		}
		
		public Result baseCaseResult(Pillar exit, Result doc) {
			return DocJones.baseCaseResult(exit, doc);
		}
	}
}
