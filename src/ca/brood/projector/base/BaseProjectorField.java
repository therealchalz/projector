package ca.brood.projector.base;

import ca.brood.projector.*;
import ca.brood.projector.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseProjectorField extends BaseGenerated {
	protected String name = "";
	protected String elementName = "";
	protected String type = "";
	protected int size = 0;
	protected ProjectorOptions options = new ProjectorOptions();
	protected BaseProjectorField() {
		super();
		log = Logger.getLogger("ProjectorField");
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
	public String getType() {
		return type;
	}
	public void setType(String val) {
		this.type = val;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int val) {
		this.size = val;
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
		this.type = "";
		this.size = 0;
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
			} else if ("options".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.options.configure(currentConfigNode);
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorField name: "+this.name);
		log.debug("ProjectorField elementName: "+this.elementName);
		log.debug("ProjectorField type: "+this.type);
		log.debug("ProjectorField size: "+this.size);
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
		if (!type.equals("")) {
			Element f3 = doc.createElement("type");
			f3.appendChild(doc.createTextNode(type));
			ret.appendChild(f3);
		}
		Element f4 = doc.createElement("size");
		f4.appendChild(doc.createTextNode(Integer.toString(size)));
		ret.appendChild(f4);
		ret.appendChild(options.save(doc, "options"));
		return ret;
	}
}
