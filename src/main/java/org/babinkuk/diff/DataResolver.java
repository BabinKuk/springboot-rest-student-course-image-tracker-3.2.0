package org.babinkuk.diff;

/**
 * implementing this interface allows an object to resolve data, given a certain input
 * 
 * @author BabinKuk
 *
 */
public interface DataResolver<I, O> {

	/**
	 * resolves data a given prameter
	 * for example, it could look up an id in database and return the results
	 * 
	 * @param param input data
	 * @return resolved data
	 */
	public O resolve(I param);
}
