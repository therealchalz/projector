package ca.brood.projector;

import ca.brood.projector.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseProjectorField extends BaseGenerated {
	protected String name = "";
	protected String type = "";
	protected int size = 0;
	protected String elementName = "";
	protected BaseProjectorField() {
		super();
		log = Logger.getLogger("ProjectorField");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.type = "";
		this.size = 0;
		this.elementName = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("type".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.type = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("size".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				try {
					this.size = Util.parseInt(currentConfigNode.getFirstChild().getNodeValue());
				} catch (Exception e) {
					log.error("Error parsing size: "+currentConfigNode.getFirstChild().getNodeValue());
				}
			} else if ("elementName".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.elementName = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorField name: "+this.name);
		log.debug("ProjectorField type: "+this.type);
		log.debug("ProjectorField size: "+this.size);
		log.debug("ProjectorField elementName: "+this.elementName);
		return true;
	}
}
