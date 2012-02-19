package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ca.brood.projector.util.Util;

public class GeneratedProjectorProject extends Generated {
	protected String name = "";
	protected String projectPackage = "";
	protected GeneratedProjectorProject() {
		super();
		log = Logger.getLogger("ProjectorProject");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.projectPackage = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("projectPackage".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.projectPackage = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorProject name: "+this.name);
		log.debug("ProjectorProject projectPackage: "+this.projectPackage);
		return true;
	}
}
