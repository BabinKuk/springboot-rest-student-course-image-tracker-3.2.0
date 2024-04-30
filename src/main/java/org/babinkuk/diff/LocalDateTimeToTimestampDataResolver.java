package org.babinkuk.diff;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * for params that receive LocalDateTime vales
 * 
 * @author BabinKuk
 *
 */
public class LocalDateTimeToTimestampDataResolver implements DataResolver<LocalDateTime, String> {

	@Override
	public String resolve(LocalDateTime param) {
		Date date = DateHelper.localDateTimeToDate(param);
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
			return sdf.format(param);
		}
		return null;
	}

}
