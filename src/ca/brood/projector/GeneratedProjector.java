package ca.brood.projector;

import java.util.ArrayList;
import java.io.*;
import javax.xml.parsers.*;
import ca.brood.projector.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class GeneratedProjector extends Generated {
	protected GeneratedProjectorProject project = new GeneratedProjectorProject();
	protected ArrayList<GeneratedProjectorObject> projectObjects = new ArrayList<GeneratedProjectorObject>();
	protected GeneratedProjector() {
		super();
		log = Logger.getLogger("Projector");
	}
	@Override
	public boolean configure(Node configNode) {
		this.project = new GeneratedProjectorProject();
		this.projectObjects = new ArrayList<GeneratedProjectorObject>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("project".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.project.configure(currentConfigNode);
			} else if ("object".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				GeneratedProjectorObject generatedProjectorObject = new GeneratedProjectorObject();
				if (generatedProjectorObject.configure(currentConfigNode)){
					this.projectObjects.add(generatedProjectorObject);
				}
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		return true;
	}
	protected void load(String filename) {
		log.info("Loading file: "+filename);

		File xmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(true);
		dbFactory.setNamespaceAware(true);
		XmlErrorCallback error = new XmlErrorCallback();
		Document doc;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.setErrorHandler(new SimpleXmlErrorHandler(this.log, error));
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			if (!error.isConfigValid()) {
				throw new Exception("Config doesn't conform to schema.");
			}
		} catch (Exception e) {
			log.fatal("Exception while trying to load config file: "+filename + e.getMessage());
			return;
		}
		Node currentConfigNode = doc.getDocumentElement();
		log.debug("Reading configuration now");
		if ("projector".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {
			log.debug("Configuring project...");
			this.configure(currentConfigNode);
		} else {
			log.fatal("Bad XML file: root element isn't a projector.");
		}
		log.info("Done with "+filename);
	}
}
