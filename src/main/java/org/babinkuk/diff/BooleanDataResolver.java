package org.babinkuk.diff;


/**
 * for params that receive boolean vales
 * if param is true return YES, if false return NO 
 * 
 * @author BabinKuk
 *
 */
public class BooleanDataResolver implements DataResolver<Boolean, String> {

	@Override
	public String resolve(Boolean param) {

		if (param != null) {
			if (Boolean.TRUE.equals(param)) {
				return Constants.YESNO.YES.getName();
			}
			if (Boolean.FALSE.equals(param)) {
				return Constants.YESNO.NO.getName();
			}
		}
		return null;
	}

}
