package com.duykien.usc.GIS.DP;

public class PertubationMethodFactory {
	public enum NoisePertubationMethod {
		BASELINE(0), LIMIT(1), LIMIT_SS(2), LIMIT_CROWD(3), UNKNOWN(Integer.MIN_VALUE);

		public final int value;

		private NoisePertubationMethod(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}

		public static NoisePertubationMethod fromIntValue(int X) { // iterate through and pick }
			switch (X) {
			case 0:
				return NoisePertubationMethod.BASELINE;
			case 1:
				return NoisePertubationMethod.LIMIT;
			case 2:
				return NoisePertubationMethod.LIMIT_SS;
			case 3: 
				return NoisePertubationMethod.LIMIT_CROWD;
			default:
				return NoisePertubationMethod.UNKNOWN;
			}
		}
		
		public String toString() {
			NoisePertubationMethod method = fromIntValue(value);
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
