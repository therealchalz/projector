package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

public abstract class BaseGenerated {
	protected Logger log;
	protected BaseGenerated() {
		log = Logger.getLogger("Generated");
	}
	public abstract boolean configure(Node configNode);

}