package ca.brood.projector.util;

public class Util {
	private Util() {	
	}
	public static int parseInt(String number) throws NumberFormatException {
		int ret;
		if (number.matches("0[xX][0-9a-fA-F]+")) { //Hex number
			ret = Integer.parseInt(number.substring(3), 16);
		} else {
			ret = Integer.parseInt(number);
		}
		return ret;
	}
}
