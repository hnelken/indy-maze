package main.hrn10.docJones;

/**
 * Represents the result of a recursive call in the DocJones algorithm
 * @author Harry Nelken (hrn10@case.edu)
 * For EECS 293 - Vincenzo Liberatore
 * Software Craftsmanship
 */
public class Result implements Comparable<Result> {
	private int value;		//the value of the result
	private Pillar pillar;	//the pillar associated with the recursive call
	
	/**
	 * Constructor for a result object with a value and an associated pillar
	 * @param value The value of this result
	 * @param pillar The pillar associated with this result
	 */
	public Result(int value, Pillar pillar) {
		this.value = value;
		this.pillar = pillar;
	}
	
	/**
	 * Returns the value of this result
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the pillar associated with this result
	 * @return the associated pillar
	 */
	public Pillar getPillar() {
		return pillar;
	}

	@Override
	public int compareTo(Result r) {
		if (null == r) {
			throw new NullPointerException();
		}
		return (this.getValue() < r.getValue()) ? -1 : (this.getValue() > r.getValue()) ? 1 : 0;
	}
}
