package ca.brood.projector.base;

import ca.brood.projector.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseProjectorReference extends BaseGenerated {
	protected String name = "";
	protected String elementName = "";
	protected String targetType = "";
	protected String relationship = "";
	protected ArrayList<SubclassType> subclassTypes = new ArrayList<SubclassType>();
	protected ProjectorOptions options = new ProjectorOptions();
	protected BaseProjectorReference() {
		super();
		log = Logger.getLogger("ProjectorReference");
	}
	public String getName() {
		return name;
	}
	public void setName(String val) {
		this.name = val;
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
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String val) {
		this.relationship = val;
	}
	public ArrayList<SubclassType> getSubclassTypes() {
		return subclassTypes;
	}
	public void setSubclassTypes(ArrayList<SubclassType> val) {
		this.subclassTypes = val;
	}
	public ProjectorOptions getOptions() {
		return options;
	}
	public void setOptions(ProjectorOptions val) {
		this.options = val;
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.elementName = "";
		this.targetType = "";
		this.relationship = "";
		this.subclassTypes = new ArrayList<SubclassType>();
		this.options = new ProjectorOptions();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("elementName".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.elementName = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("targetType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.targetType = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("relationship".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.relationship = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("subclassType".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				SubclassType subclassType = new SubclassType();
				if (subclassType.configure(currentConfigNode)){
					this.subclassTypes.add(subclassType);
				}
			} else if ("options".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.options.configure(currentConfigNode);
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorReference name: "+this.name);
		log.debug("ProjectorReference elementName: "+this.elementName);
		log.debug("ProjectorReference targetType: "+this.targetType);
		log.debug("ProjectorReference relationship: "+this.relationship);
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
		if (!elementName.equals("")) {
			Element f2 = doc.createElement("elementName");
			f2.appendChild(doc.createTextNode(elementName));
			ret.appendChild(f2);
		}
		if (!targetType.equals("")) {
			Element f3 = doc.createElement("targetType");
			f3.appendChild(doc.createTextNode(targetType));
			ret.appendChild(f3);
		}
		if (!relationship.equals("")) {
			Element f4 = doc.createElement("relationship");
			f4.appendChild(doc.createTextNode(relationship));
			ret.appendChild(f4);
		}
		for (BaseSubclassType baseSubclassType : subclassTypes) {
			ret.appendChild(baseSubclassType.save(doc, "subclassType"));
		}
		ret.appendChild(options.save(doc, "options"));
		return ret;
	}
}
