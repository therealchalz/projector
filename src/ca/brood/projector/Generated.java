package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

public abstract class Generated {
	protected Logger log;
	protected Generated() {
		log = Logger.getLogger("Generated");
	}
	public abstract boolean configure(Node configNode);
}
