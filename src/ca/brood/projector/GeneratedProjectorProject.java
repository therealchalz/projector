package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeneratedProjectorProject extends Generated {
	protected String name = "";
	protected String projectPackage = "";
	public GeneratedProjectorProject() {
		super();
		log = Logger.getLogger("GeneratedProjectorProject");
	}
	@Override
	public boolean configure(Node configNode) {
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("projectpackage".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.projectPackage = currentConfigNode.getFirstChild().getNodeValue();
			} else if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) || 
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("Project name: "+this.name);
		log.debug("Project projectPackage: "+this.projectPackage);
		return true;
	}

}
