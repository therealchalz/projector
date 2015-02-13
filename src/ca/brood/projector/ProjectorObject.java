/*******************************************************************************
 * Copyright (c) 2015 Charles Hache <chalz@member.fsf.org>. All rights reserved. 
 * 
 * This file is part of the projector project.
 * projector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * projector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with projector.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Charles Hache <chalz@member.fsf.org> - initial API and implementation
 ******************************************************************************/
package ca.brood.projector;

import ca.brood.projector.base.BaseProjectorObject;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ProjectorObject extends BaseProjectorObject {
	public ProjectorObject() {
		super();
	}
	private boolean generateConfigureCode(PrintStream ps, String indent) {
		ps.println(indent+"@Override");
		ps.println(indent+"public boolean configure(Node configNode) {");
		indent+="\t";
		//Go through super first if we're a subclass
		if (!this.getSuperclass().equals("")) {
			ps.println(indent+"if (!super.configure(configNode)) {");
			ps.println(indent+"	return false;");
			ps.println(indent+"}");
		}
		//wipe fields, create new references
		for (ProjectorField pf : this.fields) {
			if (!pf.generateReset(ps, indent)) {
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
		for (ProjectorField pf : this.fields) {
			if (!pf.generateLoad(ps, indent)) {
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
		for (int i=0; i<this.getFields().size(); i++) {
			ProjectorField gpf = this.getFields().get(i);
			ps.println(indent+"log.debug(\""+this.getName()+" "+gpf.getName()+": \"+this."+gpf.getName()+");");
		}
		ps.println(indent+"return true;");
		indent = indent.substring(1);
		ps.println(indent+"}");
		return true;
	}
	private boolean generateLoadCode(PrintStream ps, ProjectorProject gpp) {
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
		ps.println("		if (\""+gpp.getRootElement()+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {");
		ps.println("			log.debug(\"Configuring project...\");");
		ps.println("			this.configure(currentConfigNode);");
		ps.println("		} else {");
		ps.println("			log.fatal(\"Bad XML file: root element isn't a "+gpp.getRootElement()+".\");");
		ps.println("			return false;");
		ps.println("		}");
		ps.println("		log.info(\"Done with \"+filename);");
		ps.println("		return true;");
		ps.println("	}");
		return true;
	}
	private boolean generateFileSaveCode(PrintStream ps, ProjectorProject gpp) {
		ps.println("	protected boolean save(String filename) {");
		ps.println("		log.info(\"Saving to file: \"+filename);");
		ps.println();
		ps.println("		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();");
		ps.println("		Document doc;");
		ps.println("		try {");
		ps.println("			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();");
		ps.println("			doc = dBuilder.newDocument();");
		ps.println("			doc.appendChild(this.save(doc, \""+gpp.getRootElement()+"\"));");
		ps.println("			TransformerFactory transformerFactory = TransformerFactory.newInstance();");
		ps.println("			Transformer transformer = transformerFactory.newTransformer();");
		ps.println("			DOMSource source = new DOMSource(doc);");
		ps.println("			StreamResult result = new StreamResult(new File(filename));");
		ps.println("			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, \""+gpp.getName().toLowerCase()+".dtd\");");
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

	private boolean generateSaveCode(PrintStream ps, ProjectorObject bpo) {
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
		for (ProjectorField bpf : bpo.fields) {
			String indent = "\t\t";
			boolean optionIsArray = false;
			if (bpf.getOptions().getOptions().size() != 0) {
				for (String option : bpf.getOptions().getOptions()) {
					if ("multiple".equalsIgnoreCase(option)) {
						optionIsArray = true;
					}
				}
			}
			if (optionIsArray){
				if (bpf.getType().equalsIgnoreCase("string")) {
					ps.println(indent+"for (String s : this."+bpf.getName()+") {");
				} else if (bpf.getType().equalsIgnoreCase("integer")) {
					ps.println(indent+"for (Integer s : this."+bpf.getName()+") {");
				} else {
					log.error("Error - bad type for multiple option");
				}
				indent+="\t";
			}
			if (bpf.getType().equalsIgnoreCase("string")) {
				ps.println(indent+"if (!"+(optionIsArray? "s" : bpf.getName())+".equals(\"\")) {");
				indent+= "\t";
			}
			ps.println(indent+"Element f"+(fieldCount)+" = doc.createElement(\""+(bpf.getElementName().equals("") ? bpf.getName() : bpf.getElementName())+"\");");
			if (bpf.getType().equalsIgnoreCase("string")){
				ps.println(indent+"f"+fieldCount+".appendChild(doc.createTextNode("+(optionIsArray? "s" : bpf.getName())+"));");
			} else if (bpf.getType().equalsIgnoreCase("integer")) {
				ps.println(indent+"f"+fieldCount+".appendChild(doc.createTextNode(Integer.toString("+(optionIsArray? "s" : bpf.getName())+")));");
			} else if (bpf.getType().equalsIgnoreCase("decimal")) {
				log.error("Error: saving decimal fields isn't supported yet");
			}
			ps.println(indent+"ret.appendChild(f"+fieldCount+");");
			if (bpf.getType().equalsIgnoreCase("string")) {
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
		for (ProjectorReference bpr : bpo.references) {
			if (bpr.getRelationship().equals("onetoone")) {
				if (bpr.getSubclassTypes().size() == 0) {
					ps.println("		ret.appendChild("+bpr.getName()+".save(doc, \""+(bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName() )+"\"));");
				} else {
					boolean firstSubclass = true;
					for (SubclassType bst : bpr.getSubclassTypes()) {
						if (firstSubclass) { //same as others except for "if" vs "else if"
							ps.println("		if ("+bpr.getName()+" instanceof Base"+bst.getTargetType()+") {");
							firstSubclass = false;
						} else {
							ps.println("		} else if ("+bpr.getName()+" instanceof Base"+bst.getTargetType()+") {");
						}
						ps.println("			ret.appendChild("+bpr.getName()+".save(doc, \""+bst.getElementName()+"\"));");
					}
					ps.println("		}");
				}
			} else if (bpr.getRelationship().equals("onetomany")) {
				if (bpr.getSubclassTypes().size() == 0) {
					ps.println("		for (Base"+bpr.getTargetType()+" base"+bpr.getTargetType()+" : "+bpr.getName()+") {");
					ps.println("			ret.appendChild(base"+bpr.getTargetType()+".save(doc, \""+(bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName() )+"\"));");
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
	public boolean generate(String location, String packageStr, ProjectorProject gpp) {
		boolean success = generateBaseClass(location+"/base", packageStr, gpp);
		if (success)
			success = generateStandardClass(location, packageStr, gpp);
		return success;
	}
	public boolean generateStandardClass(String location, String packageStr, ProjectorProject gpp) {
		PrintStream ps;
		String className = this.getName();
		String fileName = location+"/"+className+".java";
		java.io.File file = new java.io.File(fileName);
		if (file.exists())
			return true;
		try {
			ps = new PrintStream(fileName);
		} catch (FileNotFoundException e) {
			log.error("Error creating output file: "+fileName);
			return false;
		}
		log.debug("Generating: "+fileName);
		ps.println("package "+packageStr+";");
		ps.println("");
		ps.println("import "+packageStr+".base.Base"+className+";");
		ps.println("");
		ps.println("public class "+className+" extends Base"+className+" {");
		ps.println("	public "+className+"() {");
		ps.println("		super();");
		ps.println("	}");
		ps.println("}");
		ps.println("");
		return true;
	}
	private boolean generateBaseClass(String location, String packageStr, ProjectorProject gpp) {
		PrintStream ps;
		String className = "Base"+this.getName();
		String fileName = location+"/"+className+".java";
		try {
			ps = new PrintStream(fileName);
		} catch (FileNotFoundException e) {
			log.error("Error creating output file: "+fileName);
			return false;
		}
		log.debug("Generating: "+fileName);
		ps.println("package "+packageStr+".base;");
		ps.println("");
		ps.println("import "+packageStr+".*;");
		//if we have onetomany relations or 'multiple' option fields, import ArrayList
		boolean doneImport = false;
		for (ProjectorField pf : this.fields) {
			if (pf.hasOptionMultiple()) {
				ps.println("import java.util.ArrayList;");
				doneImport = true;
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
		if (this.getName().equals(gpp.getName())) {
			ps.println("import java.io.*;");
			ps.println("import javax.xml.parsers.*;");
			ps.println("import org.apache.log4j.PropertyConfigurator;");
			ps.println("import javax.xml.transform.*;");
			ps.println("import javax.xml.transform.dom.DOMSource;");
			ps.println("import javax.xml.transform.stream.StreamResult;");
		}
		//if this object has an integer/decimal field or is the main object, have to import util
		if (this.getName().equals(gpp.getName())) {
			ps.println("import "+gpp.getProjectPackage()+".util.*;");
		} else {
			for (int i=0; i<this.getFields().size(); i++) {
				if (("integer".equalsIgnoreCase(this.getFields().get(i).getType())) ||
						("decimal".equalsIgnoreCase(this.getFields().get(i).getType()))){
					ps.println("import "+gpp.getProjectPackage()+".util.*;");
					break;
				}
			}
		}
		ps.println("import org.apache.log4j.Logger;");
		ps.println("import org.w3c.dom.*;");
		ps.println("");
		if (this.getSuperclass().equals("")) {
			ps.println("public abstract class "+className+" extends BaseGenerated {");
		} else {
			ps.println("public abstract class "+className+" extends Base"+this.getSuperclass()+" {");
		}
		
		String indent = "\t";
		
		//generate fields and references
		for (ProjectorField pf : this.fields) {
			if (!pf.generateDeclaration(ps, indent)) {
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
		if (this.getName().equals(gpp.getName())) {
			ps.println(indent+"	PropertyConfigurator.configure(\"logger.config\");");
		}
		ps.println(indent+"	log = Logger.getLogger(\""+this.getName()+"\");");
		ps.println(indent+"}");
		//Getters and Setters
		for (ProjectorField pf: this.fields) {
			if (!pf.generateGettersAndSetters(ps,indent)) {
				return false;
			}
		}
		for (ProjectorReference pr : this.references) {
			if (!pr.generateGettersAndSetters(ps, indent)) {
				return false;
			}
		}
		//Configure override:
		if (!generateConfigureCode(ps, indent)) {
			log.error("Couldn't generate configure code for "+this.getName());
			return false;
		}
		//Save override:
		if (!generateSaveCode(ps, this)) {
			log.error("Couldn't generate save code for "+this.getName());
			return false;
		}
		if (this.getName().equals(gpp.getName())) {
			//This is the main object, so we add the XML loading and saving code
			if (!generateLoadCode(ps, gpp)) {
				log.error("Couldn't generate run code for "+this.getName());
				return false;
			}
			if (!generateFileSaveCode(ps, gpp)) {
				log.error("Couldn't generate file save code for "+this.getName());
				return false;
			}
		}
		ps.println("}");
		return true;
	}
}


