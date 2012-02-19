package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ca.brood.projector.util.*;

public class GeneratedProjectorField extends Generated {
	protected String name = "";
	protected String type = "";
	protected int size = 0;
	
	public GeneratedProjectorField() {
		super();
		log = Logger.getLogger("GeneratedProjectorField");
	}
	public boolean configure(Node configNode) {
		name = "";
		type = "";
		size = 0;
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("type".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.type = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("size".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				try {
					this.size = Util.parseInt(currentConfigNode.getFirstChild().getNodeValue());
				} catch (Exception e) {
					log.error("Error parsing size: "+currentConfigNode.getFirstChild().getNodeValue());
					return false;
				}
			} else if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) || 
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		return true;
	}

}
