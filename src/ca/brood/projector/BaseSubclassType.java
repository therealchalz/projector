package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseSubclassType extends BaseGenerated {
	protected String elementName = "";
	protected String targetType = "";
	protected BaseSubclassType() {
		super();
		log = Logger.getLogger("SubclassType");
	}
	@Override
	public boolean configure(Node configNode) {
		this.elementName = "";
		this.targetType = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("elementName".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.elementName = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("targetType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.targetType = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("SubclassType elementName: "+this.elementName);
		log.debug("SubclassType targetType: "+this.targetType);
		return true;
	}
}
