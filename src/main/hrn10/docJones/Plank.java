package main.hrn10.docJones;

/**
 * Represents a plank (edge) between two pillars (vertexes)
 * in the temple grid used in the DocJones algorithm
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class Plank {
	private Pillar target1;		//one of the pillars connected by this plank
	private Pillar target2;		//the other pillar connected by this plank
	private boolean usable;		//whether this plank can be used by docJones
	
	/**
	 * Constructor for a plank between two target pillars, potentially usable by DocJones
	 * @param target1 One of the target pillars
	 * @param target2 The other target pillar
	 * @param usable Whether or not this plank can be used by DocJones
	 */
	public Plank(Pillar target1, Pillar target2, boolean usable) {
		this.target1 = target1;
		this.target2 = target2;
		this.usable = usable;
	}
	
	/**
	 * Determines whether a given pillar is connected by this plank
	 * @param pillar The pillar to be searched for
	 * @return True if this plank connects to the pillar, false otherwise
	 */
	public boolean containsPillar(Pillar pillar) {
		return ((target1.equals(pillar))||(target2.equals(pillar)));
	}
	
	/**
	 * Returns whether or not this plank can be used by DocJones
	 * @return true if it can be used, false if not
	 */
	public boolean isUsable() {
		return usable;
	}
	
	/**
	 * Sets whether a plank can be used by DocJones
	 * @param usable Whether or not the plank can be used
	 */
	public void setUsable(boolean usable) {
		this.usable = usable;
	}
	
	/**
	 * Finds the target pillar this plank connects from the given pillar
	 * @param pillar The pillar whose target is to be found
	 * @return The target pillar associated with the given pillar if it is in a plank, null otherwise
	 */
	public Pillar targetOfPillar(Pillar pillar) {
		if (target1.equals(pillar)) {
			return target2;			//checks if either of this planks targets are the given pillar
		}							//and returns the other pillar connected to by this plank
		else if (target2.equals(pillar)) {
			return target1;
		}
		else {
			return null;
		}
	}
}
