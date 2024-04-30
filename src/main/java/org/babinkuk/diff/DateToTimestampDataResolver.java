package org.babinkuk.diff;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * for params that receive date vales
 * 
 * @author BabinKuk
 *
 */
public class DateToTimestampDataResolver implements DataResolver<Date, String> {

	@Override
	public String resolve(Date param) {

		if (param != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
			return sdf.format(param);
		}
		return null;
	}

}
