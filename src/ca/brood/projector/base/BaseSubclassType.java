package ca.brood.projector.base;

import ca.brood.projector.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseSubclassType extends BaseGenerated {
	protected String elementName = "";
	protected String targetType = "";
	protected BaseSubclassType() {
		super();
		log = Logger.getLogger("SubclassType");
	}
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String val) {
		this.elementName = val;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String val) {
		this.targetType = val;
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
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		if (!elementName.equals("")) {
			Element f1 = doc.createElement("elementName");
			f1.appendChild(doc.createTextNode(elementName));
			ret.appendChild(f1);
		}
		if (!targetType.equals("")) {
			Element f2 = doc.createElement("targetType");
			f2.appendChild(doc.createTextNode(targetType));
			ret.appendChild(f2);
		}
		return ret;
	}
}
