package org.babinkuk.diff;

/**
 * return empty string
 * 
 * @author BabinKuk
 *
 */
public class NoDataResolver implements DataResolver<Object, String> {

	@Override
	public String resolve(Object param) {

		return "";
	}

}
