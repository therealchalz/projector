package ca.brood.projector.base;

import ca.brood.projector.*;
import java.util.ArrayList;
import java.io.*;
import javax.xml.parsers.*;
import org.apache.log4j.PropertyConfigurator;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import ca.brood.projector.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public abstract class BaseProjector extends BaseGenerated {
	protected ProjectorProject project = new ProjectorProject();
	protected ArrayList<ProjectorObject> projectObjects = new ArrayList<ProjectorObject>();
	protected BaseProjector() {
		super();
		PropertyConfigurator.configure("logger.config");
		log = Logger.getLogger("Projector");
	}
	public ProjectorProject getProject() {
		return project;
	}
	public void setProject(ProjectorProject val) {
		this.project = val;
	}
	public ArrayList<ProjectorObject> getProjectObjects() {
		return projectObjects;
	}
	public void setProjectObjects(ArrayList<ProjectorObject> val) {
		this.projectObjects = val;
	}
	@Override
	public boolean configure(Node configNode) {
		this.project = new ProjectorProject();
		this.projectObjects = new ArrayList<ProjectorObject>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("project".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				this.project.configure(currentConfigNode);
			} else if ("object".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				ProjectorObject projectorObject = new ProjectorObject();
				if (projectorObject.configure(currentConfigNode)){
					this.projectObjects.add(projectorObject);
				}
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		return true;
	}
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		ret.appendChild(project.save(doc, "project"));
		for (BaseProjectorObject baseProjectorObject : projectObjects) {
			ret.appendChild(baseProjectorObject.save(doc, "object"));
		}
		return ret;
	}
	protected boolean load(String filename) {
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
			log.fatal("Exception while trying to load config file: "+filename);
			log.fatal(e.getMessage());
			return false;
		}
		Node currentConfigNode = doc.getDocumentElement();
		log.debug("Reading configuration now");
		if ("projector".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {
			log.debug("Configuring project...");
			this.configure(currentConfigNode);
		} else {
			log.fatal("Bad XML file: root element isn't a projector.");
			return false;
		}
		log.info("Done with "+filename);
		return true;
	}
	protected boolean save(String filename) {
		log.info("Saving to file: "+filename);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
			doc.appendChild(this.save(doc, "projector"));
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "projector.dtd");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
		} catch (Exception e) {
			log.fatal("Exception while trying to build config file: "+filename);
			log.fatal(e.getMessage());
			return false;
		}
		log.info("Done with "+filename);
		return true;
	}
}
