package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeneratedProjector extends Generated {
	protected GeneratedProjectorProject project = new GeneratedProjectorProject();
	protected ArrayList<GeneratedProjectorObject> projectObjects = new ArrayList<GeneratedProjectorObject>();
	protected GeneratedProjector() {
		super();
		log = Logger.getLogger("Projector");
	}
	@Override
	public boolean configure(Node configNode) {
		this.projectObjects = new ArrayList<GeneratedProjectorObject>();
		this.project = new GeneratedProjectorProject();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) || 
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("project".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.project.configure(currentConfigNode);
			} else if ("object".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				GeneratedProjectorObject generatedProjectObject = new GeneratedProjectorObject();
				if (generatedProjectObject.configure(currentConfigNode)) {
					this.projectObjects.add(generatedProjectObject);
				}
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		return true;
	}
}
