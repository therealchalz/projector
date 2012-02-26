package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseProjectorReference extends BaseGenerated {
	protected String name = "";
	protected String targetType = "";
	protected String relationship = "";
	protected String elementName = "";
	protected ArrayList<BaseSubclassType> subclassTypes = new ArrayList<BaseSubclassType>();
	protected BaseProjectorReference() {
		super();
		log = Logger.getLogger("ProjectorReference");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.targetType = "";
		this.relationship = "";
		this.elementName = "";
		this.subclassTypes = new ArrayList<BaseSubclassType>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("targetType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.targetType = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("relationship".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.relationship = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("elementName".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.elementName = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("subclassType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				BaseSubclassType baseSubclassType = new BaseSubclassType();
				if (baseSubclassType.configure(currentConfigNode)){
					this.subclassTypes.add(baseSubclassType);
				}
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
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		if (!name.equals("")) {
			Element f1 = doc.createElement("name");
			f1.appendChild(doc.createTextNode(name));
			ret.appendChild(f1);
		}
		if (!targetType.equals("")) {
			Element f2 = doc.createElement("targetType");
			f2.appendChild(doc.createTextNode(targetType));
			ret.appendChild(f2);
		}
		if (!relationship.equals("")) {
			Element f3 = doc.createElement("relationship");
			f3.appendChild(doc.createTextNode(relationship));
			ret.appendChild(f3);
		}
		if (!elementName.equals("")) {
			Element f4 = doc.createElement("elementName");
			f4.appendChild(doc.createTextNode(elementName));
			ret.appendChild(f4);
		}
		for (BaseSubclassType baseSubclassType : subclassTypes) {
			ret.appendChild(baseSubclassType.save(doc, "subclassType"));
		}
		return ret;
	}
}
