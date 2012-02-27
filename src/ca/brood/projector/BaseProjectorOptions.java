package ca.brood.projector;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

public class BaseProjectorOptions extends BaseGenerated {
	protected ArrayList<String> options = new ArrayList<String>();
	protected BaseProjectorOptions() {
		super();
		log = Logger.getLogger("ProjectorOptions");
	}
	@Override
	public boolean configure(Node configNode) {
		this.options = new ArrayList<String>();
		NodeList configNodes = configNode.getChildNodes();
		for (int i=0; i<configNodes.getLength(); i++) {
			Node currentConfigNode = configNodes.item(i);
			if (("#comment".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||
			("#text".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{
				continue;
			} else if ("option".compareToIgnoreCase(currentConfigNode.getNodeName())==0){
				configNode.removeChild(currentConfigNode);
				this.options.add(currentConfigNode.getFirstChild().getNodeValue());
			} else {
				log.warn("Got unknown node in config: "+currentConfigNode.getNodeName());
			}
		}
		log.debug("ProjectorOptions options: "+this.options);
		return true;
	}
	@Override
	public Element save(Document doc, String root) {
		Element ret = doc.createElement(root);
		for (String s : this.options) {
			if (!s.equals("")) {
				Element f1 = doc.createElement("option");
				f1.appendChild(doc.createTextNode(s));
				ret.appendChild(f1);
			}
		}
		return ret;
	}
}
