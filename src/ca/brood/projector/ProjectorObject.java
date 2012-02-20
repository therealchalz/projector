package ca.brood.projector;

import java.io.FileNotFoundException;
import java.io.PrintStream;


import org.apache.log4j.Logger;

public class ProjectorObject {
	private Logger log;
	private GeneratedProjectorObject gpo;
	public ProjectorObject() {
		super();
		log = Logger.getLogger("ProjectorObject");
	}
	public ProjectorObject(GeneratedProjectorObject o) {
		super();
		log = Logger.getLogger("ProjectorObject");
		gpo = o;
	}
	
	private boolean generateFieldDeclaration(PrintStream ps, GeneratedProjectorField gpf) {
		if (gpf.name.length() == 0) {
			log.error("Bad name for field: ");
			return false;
		}
			
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
			if (gpf.size <= 4 && gpf.size > 0) {
				ps.println("	protected float "+gpf.name+" = 0.0;");
			} else if (gpf.size > 4 && gpf.size <= 8) {
				ps.println("	protected double "+gpf.name+" = 0.0;");
			} else {
				log.error("Bad size for field: "+gpf.size);
				return false;
			}
		} else {
			log.error("Bad type for field: "+gpf.type);
			return false;
		}
		return true;
	}
	private boolean generateReferenceDeclaration(PrintStream ps, GeneratedProjectorReference gpr) {
		if (gpr.targetType.equals("")) {
			log.error("Invalid reference targetType: ");
			return false;
		}
		if (gpr.name.equals("")) {
			log.error("Invalid reference name: ");
			return false;
		}
		String objType = "Generated"+gpr.targetType;
		if ("onetoone".compareToIgnoreCase(gpr.relationship)==0) {
			ps.println("	protected "+objType+" "+gpr.name+" = new "+objType+"();");
		} else if ("onetomany".compareToIgnoreCase(gpr.relationship)==0) {
			ps.println("	protected ArrayList<"+objType+"> "+gpr.name+" = new ArrayList<"+objType+">();");
		} else {
			log.error("Invalid reference relationship: "+gpr.relationship);
		}
		return true;
	}
	private boolean generateFieldReset(PrintStream ps, GeneratedProjectorField gpf) {
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
		return true;
	}
	private boolean generateReferenceReset(PrintStream ps, GeneratedProjectorReference gpr) {
		String objType = "Generated"+gpr.targetType;
		if ("onetoone".compareToIgnoreCase(gpr.relationship)==0) {
			ps.println("		this."+gpr.name+" = new "+objType+"();");
		} else if ("onetomany".compareToIgnoreCase(gpr.relationship)==0) {
			ps.println("		this."+gpr.name+" = new ArrayList<"+objType+">();");
		}
		return true;
	}
	private boolean generateFieldLoad(PrintStream ps, GeneratedProjectorField gpf) {
		ps.println("			} else if (\""+gpf.elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
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
		return true;
	}
	private boolean generateReferenceLoad(PrintStream ps, GeneratedProjectorReference gpr) {
		ps.println("			} else if (\""+gpr.elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		if ("onetoone".compareToIgnoreCase(gpr.relationship)==0) {
			ps.println("				this."+gpr.name+".configure(currentConfigNode);");
		} else if ("onetomany".compareToIgnoreCase(gpr.relationship)==0) {
			String objType = "Generated"+gpr.targetType;
			String variable = "generated"+gpr.targetType;
			ps.println("				"+objType+" "+variable+" = new "+objType+"();");
			ps.println("				if ("+variable+".configure(currentConfigNode)){");
			ps.println("					this."+gpr.name+".add("+variable+");");
			ps.println("				}");
		} 
		return true;
	}
	private boolean generateConfigureCode(PrintStream ps) {
		ps.println("	@Override");
		ps.println("	public boolean configure(Node configNode) {");
		//TODO:wipe fields, create new references
		for (int i=0; i<this.gpo.fields.size(); i++) {
			GeneratedProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldReset(ps, gpf)) {
				return false;
			}
		}
		for (int i=0; i<this.gpo.references.size(); i++) {
			GeneratedProjectorReference gpr = this.gpo.references.get(i);
			if (!generateReferenceReset(ps, gpr)) {
				return false;
			}
		}
		ps.println("		NodeList configNodes = configNode.getChildNodes();");
		ps.println("		for (int i=0; i<configNodes.getLength(); i++) {");
		ps.println("			Node currentConfigNode = configNodes.item(i);");
		ps.println("			if ((\"#comment\".compareToIgnoreCase(currentConfigNode.getNodeName())==0) ||");
		ps.println("			(\"#text\".compareToIgnoreCase(currentConfigNode.getNodeName())==0))	{");
		ps.println("				continue;");
		//Generate loading of fields from XML
		for (int i=0; i<this.gpo.fields.size(); i++) {
			GeneratedProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldLoad(ps, gpf)) {
				return false;
			}
		}
		for (int i=0; i<this.gpo.references.size(); i++) {
			GeneratedProjectorReference gpr = this.gpo.references.get(i);
			if (!generateReferenceLoad(ps, gpr)) {
				return false;
			}
		}
		ps.println("			} else {");
		ps.println("				log.warn(\"Got unknown node in config: \"+currentConfigNode.getNodeName());");
		ps.println("			}");
		ps.println("		}");
		//Print debug info for loaded fields
		for (int i=0; i<this.gpo.fields.size(); i++) {
			GeneratedProjectorField gpf = this.gpo.fields.get(i);
			ps.println("		log.debug(\""+this.gpo.name+" "+gpf.name+": \"+this."+gpf.name+");");
		}
		ps.println("		return true;");
		ps.println("	}");
		return true;
	}
	private boolean generateLoadCode(PrintStream ps, GeneratedProjectorProject gpp) {
		ps.println("	protected void load(String filename) {");
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
		ps.println("			log.fatal(\"Exception while trying to load config file: \"+filename + e.getMessage());");
		ps.println("			return;");
		ps.println("		}");
		ps.println("		Node currentConfigNode = doc.getDocumentElement();");
		ps.println("		log.debug(\"Reading configuration now\");");
		ps.println("		if (\""+gpp.rootElement+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {");
		ps.println("			log.debug(\"Configuring project...\");");
		ps.println("			this.configure(currentConfigNode);");
		ps.println("		} else {");
		ps.println("			log.fatal(\"Bad XML file: root element isn't a "+gpp.rootElement+".\");");
		ps.println("		}");
		ps.println("		log.info(\"Done with \"+filename);");
		ps.println("	}");
		return true;
	}
	public boolean generate(String location, String packageStr, GeneratedProjectorProject gpp) {
		PrintStream ps;
		String className = "Generated"+this.gpo.name;
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
		//if we have onetomany relations, import ArrayList
		for (int i=0; i<this.gpo.references.size(); i++) {
			if ("onetomany".equalsIgnoreCase(this.gpo.references.get(i).relationship)) {
				ps.println("import java.util.ArrayList;");
				break;
			}
		}
		//if this is the main object, import the extra stuff:
		if (this.gpo.name.equals(gpp.name)) {
			ps.println("import java.io.*;");
			ps.println("import javax.xml.parsers.*;");
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
		ps.println("public class "+className+" extends Generated {");
		//TODO: generate fields and references
		for (int i=0; i<this.gpo.fields.size(); i++) {
			GeneratedProjectorField gpf = this.gpo.fields.get(i);
			if (!generateFieldDeclaration(ps, gpf)) {
				return false;
			}
		}
		for (int i=0; i<this.gpo.references.size(); i++) {
			GeneratedProjectorReference gpr = this.gpo.references.get(i);
			if (!generateReferenceDeclaration(ps, gpr)) {
				return false;
			}
		}
		//Constructor:
		ps.println("	protected "+className+"() {");
		ps.println("		super();");
		ps.println("		log = Logger.getLogger(\""+this.gpo.name+"\");");
		ps.println("	}");
		//Configure override:
		if (!generateConfigureCode(ps)) {
			log.error("Couldn't generate configure code for "+this.gpo.name);
			return false;
		}
		if (this.gpo.name.equals(gpp.name)) {
			//This is the main object, so we add the XML loading code
			if (!generateLoadCode(ps, gpp)) {
				log.error("Couldn't generate run code for "+this.gpo.name);
				return false;
			}
		}
		ps.println("}");
		return true;
	}
}
