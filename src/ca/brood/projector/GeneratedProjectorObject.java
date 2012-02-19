package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ca.brood.projector.util.Util;

public class GeneratedProjectorObject extends Generated {
	protected String name = "";
	protected ArrayList<GeneratedProjectorField> fields = new ArrayList<GeneratedProjectorField>();
	protected ArrayList<GeneratedProjectorReference> references = new ArrayList<GeneratedProjectorReference>();
	protected GeneratedProjectorObject() {
		super();
		log = Logger.getLogger("ProjectorObject");
	}
	@Override
	public boolean configure(Node configNode) {
		this.name = "";
		this.fields = new ArrayList<GeneratedProjectorField>();
		this.references = new ArrayList<GeneratedProjectorReference>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("name".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.name = currentConfigNode.getFirstChild().getNodeValue();
			} else if ("field".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				GeneratedProjectorField generatedProjectorField = new GeneratedProjectorField();
				if (generatedProjectorField.configure(currentConfigNode)){
					this.fields.add(generatedProjectorField);
				}
			} else if ("reference".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				GeneratedProjectorReference generatedProjectorReference = new GeneratedProjectorReference();
				if (generatedProjectorReference.configure(currentConfigNode)){
					this.references.add(generatedProjectorReference);
				}
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorObject name: "+this.name);
		return true;
	}
}
