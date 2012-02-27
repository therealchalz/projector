package ca.brood.projector;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;



import org.apache.log4j.Logger;

public class ProjectorObject {
	private Logger log;
	private BaseProjectorObject gpo;
	protected ArrayList<ProjectorReference> references = new ArrayList<ProjectorReference>();
	public ProjectorObject() {
		log = Logger.getLogger("ProjectorObject");
	}
	public ProjectorObject(BaseProjectorObject o) {
		log = Logger.getLogger("ProjectorObject");
		gpo = o;
		references = new ArrayList<ProjectorReference>();
		for (BaseProjectorReference bpr : gpo.references) {
			references.add(new ProjectorReference(bpr));
		}
	}
	
	//this function also does error checking for subsequent field code generation methods
	private boolean generateFieldDeclaration(PrintStream ps, BaseProjectorField gpf) {
		if (gpf.name.length() == 0) {
			log.error("Bad name for field: ");
			return false;
		}
		if (gpf.elementName.equals("")) {
			gpf.elementName = gpf.name;
		}
		boolean optionIsArray = false;
		if (gpf.options.options.size() != 0) {
			for (String option : gpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("	protected String "+gpf.name+" = \"\";");
			} else if ("integer".equalsIgnoreCase(gpf.type)) {
				if (gpf.size <= 4 && gpf.size > 0) {
					ps.println("	protected int "+gpf.name+" = 0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println("	protected long "+gpf.name+" = 0;");
				} else {
					log.error("Bad size for field: "+gpf.size);
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(gpf.type)) {
				log.error("Decimal fields are not implement yet");
				/*if (gpf.size <= 4 && gpf.size > 0) {
					ps.println("	protected float "+gpf.name+" = 0.0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println("	protected double "+gpf.name+" = 0.0;");
				} else {
					log.error("Bad size for field: "+gpf.size);
					return false;
				}*/
			} else {
				log.error("Bad type for field: "+gpf.type);
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("	protected ArrayList<String> "+gpf.name+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(gpf.type)) {
				if (gpf.size <= 4 && gpf.size > 0) {
					ps.println("	protected ArrayList<Integer> "+gpf.name+" = new ArrayList<Integer>();");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println("	protected ArrayList<Long> "+gpf.name+" = new ArrayList<Long>();");
				} else {
					log.error("Bad size for field: "+gpf.size);
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(gpf.type)) {
				log.error("Decimal fields are not implement yet");
				/*if (gpf.size <= 4 && gpf.size > 0) {
					ps.println("	protected float "+gpf.name+" = 0.0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println("	protected double "+gpf.name+" = 0.0;");
				} else {
					log.error("Bad size for field: "+gpf.size);
					return false;
				}*/
			} else {
				log.error("Bad type for field: "+gpf.type);
				return false;
			}
		}
		return true;
	}
	private boolean generateFieldReset(PrintStream ps, BaseProjectorField gpf) {
		boolean optionIsArray = false;
		if (gpf.options.options.size() != 0) {
			for (String option : gpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("		this."+gpf.name+" = \"\";");
			} else if ("integer".equalsIgnoreCase(gpf.type)) {
				ps.println("		this."+gpf.name+" = 0;");
			} else if ("decimal".equalsIgnoreCase(gpf.type)) {
				ps.println("		this."+gpf.name+" = 0.0;");
			} else {
				log.error("Bad type for field: "+gpf.type);
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("		this."+gpf.name+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(gpf.type) && gpf.size <= 4) {
				ps.println("		this."+gpf.name+" = new ArrayList<Integer>();");
			} else if ("integer".equalsIgnoreCase(gpf.type) && gpf.size <= 8) {
				ps.println("		this."+gpf.name+" = new ArrayList<Long>();");
			} else if ("decimal".equalsIgnoreCase(gpf.type) && gpf.size <= 4) {
				ps.println("		this."+gpf.name+" = new ArrayList<Float>();");
			} else if ("decimal".equalsIgnoreCase(gpf.type) && gpf.size <= 8) {
				ps.println("		this."+gpf.name+" = new ArrayList<Double>();");
			} else {
				log.error("Bad type for field: "+gpf.type);
				return false;
			}
		}
		return true;
	}
	private boolean generateFieldLoad(PrintStream ps, BaseProjectorField gpf) {
		boolean optionIsArray = false;
		if (gpf.options.options.size() != 0) {
			for (String option : gpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		ps.println("			} else if (\""+gpf.elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		ps.println("				configNode.removeChild(currentConfigNode);");
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("				this."+gpf.name+" = currentConfigNode.getFirstChild().getNodeValue();");
			} else if ("integer".equalsIgnoreCase(gpf.type)) {
				ps.println("				try {");
				ps.println("					this."+gpf.name+" = Util.parseInt(currentConfigNode.getFirstChild().getNodeValue());");
				ps.println("				} catch (Exception e) {");
				ps.println("					log.error(\"Error parsing "+gpf.elementName+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println("				}");
			} else if ("decimal".equalsIgnoreCase(gpf.type)) {
				log.error("Decimal field loading not implemented yet...");
			}
		} else {
			if ("string".equalsIgnoreCase(gpf.type)) {
				ps.println("				this."+gpf.name+".add(currentConfigNode.getFirstChild().getNodeValue());");
			} else if ("integer".equalsIgnoreCase(gpf.type)) {
				ps.println("				try {");
				ps.println("					this."+gpf.name+".add(Util.parseInt(currentConfigNode.getFirstChild().getNodeValue()));");
				ps.println("				} catch (Exception e) {");
				ps.println("					log.error(\"Error parsing "+gpf.elementName+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println("				}");
			} else if ("decimal".equalsIgnoreCase(gpf.type)) {
				log.error("Decimal field loading not implemented yet...");
			}
		}
		return true;
	}
	private boolean generateConfigureCode(PrintStream ps, String indent) {
		ps.println(indent+"@Override");
		ps.println(indent+"public boolean configure(Node configNode) {");
		indent+="\t";
		//Go through super first if we're a subclass
		if (!gpo.superclass.equals("")) {
			ps.println(indent+"if (!super.configure(configNode)) {");
			ps.println(indent+"	return false;");
			ps.println(indent+"}");
		}
		//wipe fields, create new references
		for (int i=0; i<this.gpo.fields.size(); i++) {
			BaseProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldReset(ps, gpf)) {
				return false;
			}
		}
		for (ProjectorReference pr : this.references) {
			if (!pr.generateReset(ps, indent)) {
				return false;
			}
		}
		ps.println(indent+"NodeList configNodes = configNode.getChildNodes();");
		ps.println(indent+"for (int i=0; i<configNodes.getLength(); i++) {");
		indent+="\t";
		ps.println(indent+"Node currentConfigNode = configNodes.item(i);");
		ps.println(indent+"if ((\"#comment\".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||");
		ps.println(indent+"(\"#text\".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{");
		ps.println(indent+"	continue;");
		//Generate loading of fields from XML
		for (int i=0; i<this.gpo.fields.size(); i++) {
			BaseProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldLoad(ps, gpf)) {
				return false;
			}
		}
		for (ProjectorReference pr : this.references) {
			if (!pr.generateLoad(ps, indent)) {
				return false;
			}
		}
		ps.println(indent+"} else {");
		ps.println(indent+"	log.warn(\"Got unknown node in config: \"+currentConfigNode.getNodeName());");
		ps.println(indent+"}");
		indent = indent.substring(1);
		ps.println(indent+"}");
		//Print debug info for loaded fields
		for (int i=0; i<this.gpo.fields.size(); i++) {
			BaseProjectorField gpf = this.gpo.fields.get(i);
			ps.println(indent+"log.debug(\""+this.gpo.name+" "+gpf.name+": \"+this."+gpf.name+");");
		}
		ps.println(indent+"return true;");
		indent = indent.substring(1);
		ps.println(indent+"}");
		return true;
	}
	private boolean generateLoadCode(PrintStream ps, BaseProjectorProject gpp) {
		ps.println("	protected boolean load(String filename) {");
		ps.println("		log.info(\"Loading file: \"+filename);");
		ps.println();
		ps.println("		File xmlFile = new File(filename);");
		ps.println("		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();");
		ps.println("		dbFactory.setValidating(true);");
		ps.println("		dbFactory.setNamespaceAware(true);");
		ps.println("		XmlErrorCallback error = new XmlErrorCallback();");
		ps.println("		Document doc;");
		ps.println("		try {");
		ps.println("			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();");
		ps.println("			dBuilder.setErrorHandler(new SimpleXmlErrorHandler(this.log, error));");
		ps.println("			doc = dBuilder.parse(xmlFile);");
		ps.println("			doc.getDocumentElement().normalize();");
		ps.println("			if (!error.isConfigValid()) {");
		ps.println("				throw new Exception(\"Config doesn't conform to schema.\");");
		ps.println("			}");
		ps.println("		} catch (Exception e) {");
		ps.println("			log.fatal(\"Exception while trying to load config file: \"+filename);");
		ps.println("			log.fatal(e.getMessage());");
		ps.println("			return false;");
		ps.println("		}");
		ps.println("		Node currentConfigNode = doc.getDocumentElement();");
		ps.println("		log.debug(\"Reading configuration now\");");
		ps.println("		if (\""+gpp.rootElement+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {");
		ps.println("			log.debug(\"Configuring project...\");");
		ps.println("			this.configure(currentConfigNode);");
		ps.println("		} else {");
		ps.println("			log.fatal(\"Bad XML file: root element isn't a "+gpp.rootElement+".\");");
		ps.println("			return false;");
		ps.println("		}");
		ps.println("		log.info(\"Done with \"+filename);");
		ps.println("		return true;");
		ps.println("	}");
		return true;
	}
	private boolean generateFileSaveCode(PrintStream ps, BaseProjectorProject gpp) {
		ps.println("	protected boolean save(String filename) {");
		ps.println("		log.info(\"Saving to file: \"+filename);");
		ps.println();
		ps.println("		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();");
		ps.println("		Document doc;");
		ps.println("		try {");
		ps.println("			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();");
		ps.println("			doc = dBuilder.newDocument();");
		ps.println("			doc.appendChild(this.save(doc, \""+gpp.rootElement+"\"));");
		ps.println("			TransformerFactory transformerFactory = TransformerFactory.newInstance();");
		ps.println("			Transformer transformer = transformerFactory.newTransformer();");
		ps.println("			DOMSource source = new DOMSource(doc);");
		ps.println("			StreamResult result = new StreamResult(new File(filename));");
		ps.println("			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, \""+gpp.name.toLowerCase()+".dtd\");");
		ps.println("			transformer.setOutputProperty(OutputKeys.INDENT, \"yes\");");
		ps.println("			transformer.setOutputProperty(\"{http://xml.apache.org/xslt}indent-amount\", \"2\");");
		ps.println("			transformer.transform(source, result);");
		ps.println("		} catch (Exception e) {");
		ps.println("			log.fatal(\"Exception while trying to build config file: \"+filename);");
		ps.println("			log.fatal(e.getMessage());");
		ps.println("			return false;");
		ps.println("		}");
		ps.println("		log.info(\"Done with \"+filename);");
		ps.println("		return true;");
		ps.println("	}");
		return true;
	}

	private boolean generateSaveCode(PrintStream ps, BaseProjectorObject bpo) {
		ps.println("	@Override");
		ps.println("	public Element save(Document doc, String root) {");
		//Generate save for superclass if applicable
		if (bpo.superclass.equals("")) {
			ps.println("		Element ret = doc.createElement(root);");
		} else {
			ps.println("		Element ret = super.save(doc, root);");
		}
		//Generate saves for fields
		int fieldCount = 1;
		for (BaseProjectorField bpf : bpo.fields) {
			String indent = "\t\t";
			boolean optionIsArray = false;
			if (bpf.options.options.size() != 0) {
				for (String option : bpf.options.options) {
					if ("multiple".equalsIgnoreCase(option)) {
						optionIsArray = true;
					}
				}
			}
			if (optionIsArray){
				if (bpf.type.equalsIgnoreCase("string")) {
					ps.println(indent+"for (String s : this."+bpf.name+") {");
				} else if (bpf.type.equalsIgnoreCase("integer")) {
					ps.println(indent+"for (Integer s : this."+bpf.name+") {");
				} else {
					log.error("Error - bad type for multiple option");
				}
				indent+="\t";
			}
			if (bpf.type.equalsIgnoreCase("string")) {
				ps.println(indent+"if (!"+(optionIsArray? "s" : bpf.name)+".equals(\"\")) {");
				indent+= "\t";
			}
			ps.println(indent+"Element f"+(fieldCount)+" = doc.createElement(\""+(bpf.elementName.equals("") ? bpf.name : bpf.elementName)+"\");");
			if (bpf.type.equalsIgnoreCase("string")){
				ps.println(indent+"f"+fieldCount+".appendChild(doc.createTextNode("+(optionIsArray? "s" : bpf.name)+"));");
			} else if (bpf.type.equalsIgnoreCase("integer")) {
				ps.println(indent+"f"+fieldCount+".appendChild(doc.createTextNode(Integer.toString("+(optionIsArray? "s" : bpf.name)+")));");
			} else if (bpf.type.equalsIgnoreCase("decimal")) {
				log.error("Error: saving decimal fields isn't supported yet");
			}
			ps.println(indent+"ret.appendChild(f"+fieldCount+");");
			if (bpf.type.equalsIgnoreCase("string")) {
				indent = indent.substring(1);
				ps.println(indent+"}");
			}
			if (optionIsArray) {
				indent = indent.substring(1);
				ps.println(indent+"}");
			}
			fieldCount++;
		}
		//Generate saves for references
		for (BaseProjectorReference bpr : bpo.references) {
			if (bpr.relationship.equals("onetoone")) {
				if (bpr.subclassTypes.size() == 0) {
					ps.println("		ret.appendChild("+bpr.name+".save(doc, \""+(bpr.elementName.equals("") ? bpr.name : bpr.elementName )+"\"));");
				} else {
					boolean firstSubclass = true;
					for (BaseSubclassType bst : bpr.subclassTypes) {
						if (firstSubclass) { //same as others except for "if" vs "else if"
							ps.println("		if ("+bpr.name+" instanceof Base"+bst.targetType+") {");
							firstSubclass = false;
						} else {
							ps.println("		} else if ("+bpr.name+" instanceof Base"+bst.targetType+") {");
						}
						ps.println("			ret.appendChild("+bpr.name+".save(doc, \""+bst.elementName+"\"));");
					}
					ps.println("		}");
				}
			} else if (bpr.relationship.equals("onetomany")) {
				if (bpr.subclassTypes.size() == 0) {
					ps.println("		for (Base"+bpr.targetType+" base"+bpr.targetType+" : "+bpr.name+") {");
					ps.println("			ret.appendChild(base"+bpr.targetType+".save(doc, \""+(bpr.elementName.equals("") ? bpr.name : bpr.elementName )+"\"));");
					ps.println("		}");
				} else {
					log.error("One to many subclass types saving to xml is not implemented yet");
				}
			}
		}
		ps.println("		return ret;");
		ps.println("	}");

		return true;
	}
	public boolean generate(String location, String packageStr, BaseProjectorProject gpp) {
		PrintStream ps;
		String className = "Base"+this.gpo.name;
		String fileName = location+"/"+className+".java";
		try {
			ps = new PrintStream(fileName);
		} catch (FileNotFoundException e) {
			log.error("Error creating output file: "+fileName);
			return false;
		}
		log.debug("Generating: "+fileName);
		ps.println("package "+packageStr+";");
		ps.println("");
		//if we have onetomany relations or 'multiple' option fields, import ArrayList
		boolean doneImport = false;
		for (BaseProjectorField bpf : this.gpo.fields) {
			if (bpf.options.options.size() > 0) {
				for (String opt : bpf.options.options) {
					if ("multiple".equalsIgnoreCase(opt)) {
						ps.println("import java.util.ArrayList;");
						doneImport = true;
					}
				}
				if (doneImport)
					break;
			}
		}
		for (int i=0; i<this.references.size() && !doneImport; i++) {
			if (this.references.get(i).isOnetoMany()) {
				ps.println("import java.util.ArrayList;");
				break;
			}
		}
		//if this is the main object, import the extra stuff:
		if (this.gpo.name.equals(gpp.name)) {
			ps.println("import java.io.*;");
			ps.println("import javax.xml.parsers.*;");
			ps.println("import org.apache.log4j.PropertyConfigurator;");
			ps.println("import javax.xml.transform.*;");
			ps.println("import javax.xml.transform.dom.DOMSource;");
			ps.println("import javax.xml.transform.stream.StreamResult;");
		}
		//if this object has an integer/decimal field or is the main object, have to import util
		if (this.gpo.name.equals(gpp.name)) {
			ps.println("import "+gpp.projectPackage+".util.*;");
		} else {
			for (int i=0; i<this.gpo.fields.size(); i++) {
				if (("integer".equalsIgnoreCase(this.gpo.fields.get(i).type)) ||
						("decimal".equalsIgnoreCase(this.gpo.fields.get(i).type))){
					ps.println("import "+gpp.projectPackage+".util.*;");
					break;
				}
			}
		}
		ps.println("import org.apache.log4j.Logger;");
		ps.println("import org.w3c.dom.*;");
		ps.println("");
		if (gpo.superclass.equals("")) {
			ps.println("public class "+className+" extends BaseGenerated {");
		} else {
			ps.println("public class "+className+" extends Base"+gpo.superclass+" {");
		}
		
		String indent = "\t";
		
		//generate fields and references
		for (int i=0; i<this.gpo.fields.size(); i++) {
			BaseProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldDeclaration(ps, gpf)) {
				return false;
			}
		}
		for (ProjectorReference pr : this.references) {
			if (!pr.generateDeclaration(ps, indent)) {
				return false;
			}
		}
		//Constructor:
		ps.println(indent+"protected "+className+"() {");
		ps.println(indent+"	super();");
		if (this.gpo.name.equals(gpp.name)) {
			ps.println(indent+"	PropertyConfigurator.configure(\"logger.config\");");
		}
		ps.println(indent+"	log = Logger.getLogger(\""+this.gpo.name+"\");");
		ps.println(indent+"}");
		//Configure override:
		if (!generateConfigureCode(ps, indent)) {
			log.error("Couldn't generate configure code for "+this.gpo.name);
			return false;
		}
		//Save override:
		if (!generateSaveCode(ps, this.gpo)) {
			log.error("Couldn't generate save code for "+this.gpo.name);
			return false;
		}
		if (this.gpo.name.equals(gpp.name)) {
			//This is the main object, so we add the XML loading and saving code
			if (!generateLoadCode(ps, gpp)) {
				log.error("Couldn't generate run code for "+this.gpo.name);
				return false;
			}
			if (!generateFileSaveCode(ps, gpp)) {
				log.error("Couldn't generate file save code for "+this.gpo.name);
				return false;
			}
		}
		ps.println("}");
		return true;
	}
}
