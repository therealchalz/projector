package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class GeneratedProjectorReference extends Generated {
	protected String name = "";
	protected String targetType = "";
	protected String relationship = "";
	protected String elementName = "";
	protected GeneratedProjectorReference() {
		super();
		log = Logger.getLogger("ProjectorReference");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.targetType = "";
		this.relationship = "";
		this.elementName = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("targetType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.targetType = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("relationship".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.relationship = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("elementName".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.elementName = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorReference name: "+this.name);
		log.debug("ProjectorReference targetType: "+this.targetType);
		log.debug("ProjectorReference relationship: "+this.relationship);
		log.debug("ProjectorReference elementName: "+this.elementName);
		return true;
	}
}
