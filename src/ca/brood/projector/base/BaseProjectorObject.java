package ca.brood.projector.base;

import ca.brood.projector.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseProjectorObject extends BaseGenerated {
	protected String name = "";
	protected String superclass = "";
	protected ArrayList<ProjectorField> fields = new ArrayList<ProjectorField>();
	protected ArrayList<ProjectorReference> references = new ArrayList<ProjectorReference>();
	protected BaseProjectorObject() {
		super();
		log = Logger.getLogger("ProjectorObject");
	}
	public String getName() {
		return name;
	}
	public void setName(String val) {
		this.name = val;
	}
	public String getSuperclass() {
		return superclass;
	}
	public void setSuperclass(String val) {
		this.superclass = val;
	}
	public ArrayList<ProjectorField> getFields() {
		return fields;
	}
	public void setFields(ArrayList<ProjectorField> val) {
		this.fields = val;
	}
	public ArrayList<ProjectorReference> getReferences() {
		return references;
	}
	public void setReferences(ArrayList<ProjectorReference> val) {
		this.references = val;
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.superclass = "";
		this.fields = new ArrayList<ProjectorField>();
		this.references = new ArrayList<ProjectorReference>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("superclass".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.superclass = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("field".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				ProjectorField projectorField = new ProjectorField();
				if (projectorField.configure(currentConfigNode)){
					this.fields.add(projectorField);
				}
			} else if ("reference".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				ProjectorReference projectorReference = new ProjectorReference();
				if (projectorReference.configure(currentConfigNode)){
					this.references.add(projectorReference);
				}
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorObject name: "+this.name);
		log.debug("ProjectorObject superclass: "+this.superclass);
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
		if (!superclass.equals("")) {
			Element f2 = doc.createElement("superclass");
			f2.appendChild(doc.createTextNode(superclass));
			ret.appendChild(f2);
		}
		for (BaseProjectorField baseProjectorField : fields) {
			ret.appendChild(baseProjectorField.save(doc, "field"));
		}
		for (BaseProjectorReference baseProjectorReference : references) {
			ret.appendChild(baseProjectorReference.save(doc, "reference"));
		}
		return ret;
	}
}
