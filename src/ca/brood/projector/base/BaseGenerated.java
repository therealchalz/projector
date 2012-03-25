package ca.brood.projector.base;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseGenerated {
	protected Logger log;
	protected BaseGenerated() {
		log = Logger.getLogger("Generated");
	}
	public abstract boolean configure(Node configNode);
	public abstract Element save(Document doc, String root);
}