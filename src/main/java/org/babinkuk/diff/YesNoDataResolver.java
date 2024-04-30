package org.babinkuk.diff;

public class YesNoDataResolver implements DataResolver<String, String> {

	@Override
	public String resolve(String param) {

		if (param != null) {
			for (Constants.YESNO item : Constants.YESNO.values()) {
				if (item.getId().equalsIgnoreCase(param.toString())) {
					return item.getName();
				}
			}
		}
		return null;
	}

}
