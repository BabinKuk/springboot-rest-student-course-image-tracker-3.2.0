package org.babinkuk.diff;

public class Constants {
	
	public static enum YESNO {
		YES("Y", "D", "DA", true),
		NO("N", "N", "NE", false);
		
		private String id;
		private String id2;
		private String name;
		
		private boolean bin;
		
		YESNO(String id, String id2, String name, boolean bin) {
			this.id = id;
			this.id2 = id2;
			this.name = name;
			this.bin = bin;
		}
		
		public boolean getBin() {
			return bin;
		}

		public String getId() {
			return id;
		}

		public String getId2() {
			return id2;
		}

		public String getName() {
			return name;
		}
		
		public static YESNO findById(String id) {
			for (YESNO type : YESNO.values()) {
				if (type.getId().equals(id)) {
					return type; 
				}
			}
			return null;
		}
		
		public static YESNO findById2(String id2) {
			for (YESNO type : YESNO.values()) {
				if (type.getId2().equals(id2)) {
					return type; 
				}
			}
			return null;
		}
		
		public static YESNO findByName(String name) {
			for (YESNO type : YESNO.values()) {
				if (type.getName().equals(name)) {
					return type; 
				}
			}
			return null;
		}
		
		public static YESNO findByBin(boolean bin) {
			for (YESNO type : YESNO.values()) {
				if (type.getBin() == bin) {
					return type; 
				}
			}
			return null;
		}

	}

}
