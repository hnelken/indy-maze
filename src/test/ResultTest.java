package test;

import main.hrn10.docJones.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the Result class used in the DocJones algorithm
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class ResultTest {

	/**
	 * Tests the constructor
	 * Covers the structured basis and data flow tests
	 */
	@Test public void testConstructor() {
		Result r = new Result(2, new Pillar(0, 1));
		
		//Structured Basis
		assertTrue("An object is created", r != null);
		
		//Data Flow
		assertEquals("Value is stored correctly", 2, r.getValue());
		assertEquals("Pillar is stored correctyly", new Pillar(0, 1), r.getPillar());
	}
	
	/**
	 * Tests the getValue method
	 * Covers the structured basis test
	 * Only one possible code path, no additional tests needed
	 */
	@Test public void testGetValue() {
		Result r = new Result(3, new Pillar(0, 2));
		
		//Structured Basis
		assertEquals("Value used in creation is returned", 3, r.getValue());
	}
	
	/**
	 * Tests the getPillar method
	 * Covers the structured basis test
	 * Only one possible code path, no additional tests needed
	 */
	@Test public void testGetPillar() {
		Result r = new Result(3, new Pillar(0, 2));
		
		//Structured Basis
		assertEquals("Value used in creation is returned", new Pillar(0, 2), r.getPillar());
	}

	/**
	 * Tests the overridden compareTo method
	 * Covers the structured basis tests. This in turn covers boundary tests.
	 */
	@Test(expected = NullPointerException.class)
	public void testCompareTo() {
		Result r = new Result(1, new Pillar(0, 0));
		Result eq = new Result(1, new Pillar(0, 2));
		Result valLT = new Result(0, new Pillar(2, 1));
		Result valGT = new Result(2, new Pillar(0, 1));
		
		//Structured Basis (throws expected exception)
		r.compareTo(null);
		
		//Structured Basis / Boundary
		assertEquals("Compared value is equal to tested", 0, r.compareTo(eq));
		assertEquals("Compared value is less than tested", 1, r.compareTo(valLT));
		assertEquals("Compared value is greater than tested", -1, r.compareTo(valGT));
	}
}
