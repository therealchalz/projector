package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class GeneratedProjectorProject extends Generated {
	protected String rootElement = "";
	protected String rootObject = "";
	protected String name = "";
	protected String projectPackage = "";
	protected GeneratedProjectorProject() {
		super();
		log = Logger.getLogger("ProjectorProject");
	}
	@Override
	public boolean configure(Node configNode) {
		this.rootElement = "";
		this.rootObject = "";
		this.name = "";
		this.projectPackage = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("rootElement".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.rootElement = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("rootObject".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.rootObject = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("projectPackage".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.projectPackage = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorProject rootElement: "+this.rootElement);
		log.debug("ProjectorProject rootObject: "+this.rootObject);
		log.debug("ProjectorProject name: "+this.name);
		log.debug("ProjectorProject projectPackage: "+this.projectPackage);
		return true;
	}
}
