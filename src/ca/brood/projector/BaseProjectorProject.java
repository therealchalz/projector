package ca.brood.projector;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseProjectorProject extends BaseGenerated {
	protected String rootElement = "";
	protected String rootObject = "";
	protected String name = "";
	protected String projectPackage = "";
	protected BaseProjectorProject() {
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
				configNode.removeChild(currentConfigNode);
				this.rootElement = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("rootObject".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.rootObject = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("projectPackage".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
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
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		if (!rootElement.equals("")) {
			Element f1 = doc.createElement("rootElement");
			f1.appendChild(doc.createTextNode(rootElement));
			ret.appendChild(f1);
		}
		if (!rootObject.equals("")) {
			Element f2 = doc.createElement("rootObject");
			f2.appendChild(doc.createTextNode(rootObject));
			ret.appendChild(f2);
		}
		if (!name.equals("")) {
			Element f3 = doc.createElement("name");
			f3.appendChild(doc.createTextNode(name));
			ret.appendChild(f3);
		}
		if (!projectPackage.equals("")) {
			Element f4 = doc.createElement("projectPackage");
			f4.appendChild(doc.createTextNode(projectPackage));
			ret.appendChild(f4);
		}
		return ret;
	}
}
