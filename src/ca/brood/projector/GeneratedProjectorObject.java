package ca.brood.projector;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeneratedProjectorObject extends Generated {
	protected String name = "";
	protected ArrayList<GeneratedProjectorField> fields = new ArrayList<GeneratedProjectorField>();
	protected ArrayList<GeneratedProjectorReference> references = new ArrayList<GeneratedProjectorReference>();
	public GeneratedProjectorObject() {
		super();
		log = Logger.getLogger("GeneratedProjectorObject");
	}
	@Override
	public boolean configure(Node configNode) {
		NodeList configNodes = configNode.getChildNodes();
		this.fields = new ArrayList<GeneratedProjectorField>();
		this.references = new ArrayList<GeneratedProjectorReference>();
		this.name = "";
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("field".compareToIgnoreCase(currentConfigNode.getNodeName())==0)	{
				GeneratedProjectorField generatedProjectorField = new GeneratedProjectorField();
				if (!generatedProjectorField.configure(currentConfigNode)) {
					return false;
				}
				this.fields.add(generatedProjectorField);
				continue;
			} else if ("reference".compareToIgnoreCase(currentConfigNode.getNodeName())==0)	{
				GeneratedProjectorReference generatedProjectorReference = new GeneratedProjectorReference();
				if (!generatedProjectorReference.configure(currentConfigNode)) {
					return false;
				}
				this.references.add(generatedProjectorReference);
				continue;
			} else if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) || 
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("Object name: "+this.name);
		return true;
	}

}
