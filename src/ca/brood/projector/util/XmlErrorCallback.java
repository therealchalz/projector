package ca.brood.projector.util;

public class XmlErrorCallback {
	private boolean isConfigValid = true;
	public synchronized void setConfigValid(boolean isValid) {
		isConfigValid = isValid;
	}
	public synchronized boolean isConfigValid() {
		return isConfigValid;
	}
}
