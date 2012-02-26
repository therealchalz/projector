package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseProjectorObject extends BaseGenerated {
	protected String name = "";
	protected String superclass = "";
	protected ArrayList<BaseProjectorField> fields = new ArrayList<BaseProjectorField>();
	protected ArrayList<BaseProjectorReference> references = new ArrayList<BaseProjectorReference>();
	protected BaseProjectorObject() {
		super();
		log = Logger.getLogger("ProjectorObject");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.superclass = "";
		this.fields = new ArrayList<BaseProjectorField>();
		this.references = new ArrayList<BaseProjectorReference>();
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
				BaseProjectorField baseProjectorField = new BaseProjectorField();
				if (baseProjectorField.configure(currentConfigNode)){
					this.fields.add(baseProjectorField);
				}
			} else if ("reference".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				BaseProjectorReference baseProjectorReference = new BaseProjectorReference();
				if (baseProjectorReference.configure(currentConfigNode)){
					this.references.add(baseProjectorReference);
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
