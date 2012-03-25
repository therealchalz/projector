package ca.brood.projector.base;

import ca.brood.projector.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseProjectorProject extends BaseGenerated {
	protected String name = "";
	protected String projectPackage = "";
	protected String rootElement = "";
	protected String rootObject = "";
	protected BaseProjectorProject() {
		super();
		log = Logger.getLogger("ProjectorProject");
	}
	public String getName() {
		return name;
	}
	public void setName(String val) {
		this.name = val;
	}
	public String getProjectPackage() {
		return projectPackage;
	}
	public void setProjectPackage(String val) {
		this.projectPackage = val;
	}
	public String getRootElement() {
		return rootElement;
	}
	public void setRootElement(String val) {
		this.rootElement = val;
	}
	public String getRootObject() {
		return rootObject;
	}
	public void setRootObject(String val) {
		this.rootObject = val;
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.projectPackage = "";
		this.rootElement = "";
		this.rootObject = "";
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("projectPackage".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.projectPackage = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("rootElement".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.rootElement = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("rootObject".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.rootObject = currentConfigNode.getFirstChild().getNodeValue();
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorProject name: "+this.name);
		log.debug("ProjectorProject projectPackage: "+this.projectPackage);
		log.debug("ProjectorProject rootElement: "+this.rootElement);
		log.debug("ProjectorProject rootObject: "+this.rootObject);
		return true;
	}
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		if (!name.equals("")) {
			Element f1 = doc.createElement("name");
			f1.appendChild(doc.createTextNode(name));
			ret.appendChild(f1);
		}
		if (!projectPackage.equals("")) {
			Element f2 = doc.createElement("projectPackage");
			f2.appendChild(doc.createTextNode(projectPackage));
			ret.appendChild(f2);
		}
		if (!rootElement.equals("")) {
			Element f3 = doc.createElement("rootElement");
			f3.appendChild(doc.createTextNode(rootElement));
			ret.appendChild(f3);
		}
		if (!rootObject.equals("")) {
			Element f4 = doc.createElement("rootObject");
			f4.appendChild(doc.createTextNode(rootObject));
			ret.appendChild(f4);
		}
		return ret;
	}
}
