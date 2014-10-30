package test;

import main.hrn10.docJones.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlankTest {

	@Test public void testConstructor() {
		Pillar src = new Pillar (0, 1);
		Pillar tgt = new Pillar(0, 2);
		Plank p = new Plank(src, tgt, true);
		
		//Structured Basis
		assertTrue("Constructor creates a plank", p instanceof Plank);
		
		//Data Flow
		assertTrue("Source pillar is stored correctly", p.containsPillar(src));
		assertTrue("Target pillar is stored correctly", p.containsPillar(tgt));
		assertTrue("Usable flag is stored correctly", p.isUsable());
	}

	@Test public void testContainsPillar() {
		Pillar src = new Pillar (0, 1);
		Plank p = new Plank(src, new Pillar(0, 2), true);
		
		//Structured Basis
		assertTrue("At least one side of || is satisfied, pillar is contained", p.containsPillar(src));
		assertFalse("Neither side of || is satisfied, pillar is not contained", p.containsPillar(new Pillar(2, 3)));
	}
	
	/**
	 * Tests the isUsable method
	 * Structured basis is covered
	 * Only one code path, no additional tests required
	 */
	@Test public void testIsUsable() {
		Plank p = new Plank(new Pillar(0, 0), new Pillar(1, 0), true);
		
		//Structured Basis
		assertTrue("Usable plank returns true", p.isUsable());
	}
	
	/**
	 * Tests the setUsable method
	 * Structured basis is covered
	 * This test covers the data flow simultaneously
	 */
	@Test public void testSetUsable() {
		Plank p = new Plank(new Pillar(0, 0), new Pillar(1, 0), false);

		p.setUsable(true);
		//Structured Basis
		assertTrue("Usability is changed when set", p.isUsable());
	}
	
	/**
	 * Tests the targetOfPillar method
	 * Structured basis tests are covered
	 * No additional tests needed
	 */
	@Test public void testTargetOfPillar() {
		Pillar src = new Pillar (0, 1);
		Pillar tgt = new Pillar(0, 2);
		Plank p = new Plank(src, tgt, true);
		
		//Structured Basis
		assertEquals("Given pillar is src, tgt returned", tgt, p.targetOfPillar(src));
		assertEquals("Given pillar is tgt, src returned", src, p.targetOfPillar(tgt));
		assertEquals("Given pillar is not contained, no target", null, p.targetOfPillar(null));
	}
}
