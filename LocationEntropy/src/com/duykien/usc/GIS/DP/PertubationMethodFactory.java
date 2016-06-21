package com.duykien.usc.GIS.DP;

public class PertubationMethodFactory {
	public enum EPertubationMethod {
		BASELINE(0), LIMIT(1), LIMIT_SS(2), LIMIT_CROWD(3), UNKNOWN(Integer.MIN_VALUE);

		public final int value;

		private EPertubationMethod(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}

		public static EPertubationMethod fromIntValue(int X) { // iterate through and pick }
			switch (X) {
			case 0:
				return EPertubationMethod.BASELINE;
			case 1:
				return EPertubationMethod.LIMIT;
			case 2:
				return EPertubationMethod.LIMIT_SS;
			case 3: 
				return EPertubationMethod.LIMIT_CROWD;
			default:
				return EPertubationMethod.UNKNOWN;
			}
		}
		
		public String toString() {
			EPertubationMethod method = fromIntValue(value);
			switch (method) {
			case BASELINE:
				return "BASELINE";
			case LIMIT:
				return "LIMIT";
			case LIMIT_SS:
				return "LIMIT_SS";
			case LIMIT_CROWD:
				return "LIMIT_CROWD";

			default:
				return "UNKNOWN";
			}
		}
	}
}
