package ca.brood.projector;

import java.io.PrintStream;

import org.apache.log4j.Logger;

public class ProjectorReference {
	private BaseProjectorReference bpr;
	private Logger log;
	
	public ProjectorReference() {
		log = Logger.getLogger("ProjectorReference");
	}
	public ProjectorReference(BaseProjectorReference o) {
		this();
		bpr = o;
	}
	public boolean isOnetoMany() {
		return "onetomany".equalsIgnoreCase(bpr.relationship);
	}
	public boolean isOnetoOne() {
		return "onetoone".equalsIgnoreCase(bpr.relationship);
	}
	
	public boolean generateDeclaration(PrintStream ps, String indent) {
		if (bpr.targetType.equals("")) {
			log.error("Invalid reference targetType: ");
			return false;
		}
		if (bpr.name.equals("")) {
			log.error("Invalid reference name: ");
			return false;
		}
		if (bpr.elementName.equals("")) {
			bpr.elementName = bpr.name;
		}
		String objType = "Base"+bpr.targetType;
		if (this.isOnetoOne()) {
			ps.println(indent+"protected "+objType+" "+bpr.name+" = new "+objType+"();");
		} else if (this.isOnetoMany()) {
			ps.println(indent+"protected ArrayList<"+objType+"> "+bpr.name+" = new ArrayList<"+objType+">();");
		} else {
			log.error("Invalid reference relationship: "+bpr.relationship);
			return false;
		}
		return true;
	}
	
	public boolean generateReset(PrintStream ps, String indent) {
		String objType = "Base"+bpr.targetType;
		if (this.isOnetoOne()) {
			ps.println(indent+"this."+bpr.name+" = new "+objType+"();");
		} else if (this.isOnetoMany()) {
			ps.println(indent+"this."+bpr.name+" = new ArrayList<"+objType+">();");
		}
		return true;
	}
	private boolean generateLoadText(PrintStream ps, String indent, String elementName, String targetType, boolean reinstantiate) {
		ps.println(indent+"} else if (\""+elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		indent += "\t";
		if (this.isOnetoOne()) {
			if (reinstantiate) {
				ps.println(indent+"this."+bpr.name+" = new "+targetType+"();");
			}
			ps.println(indent+"this."+bpr.name+".configure(currentConfigNode);");
		} else if (this.isOnetoMany()) {
			String objType = targetType;
			String variable = targetType.substring(0, 1).toLowerCase()+targetType.substring(1);
			ps.println(indent+objType+" "+variable+" = new "+objType+"();");
			ps.println(indent+"if ("+variable+".configure(currentConfigNode)){");
			ps.println(indent+"	this."+bpr.name+".add("+variable+");");
			ps.println(indent+"}");
		} else {
			log.error("Bad relationship: "+this.bpr.relationship);
			return false;
		}
		return true;
	}
	public boolean generateLoad(PrintStream ps, String indent) {
		if (bpr.subclassTypes.size() == 0) {
			generateLoadText(ps, indent, bpr.elementName, "Base"+bpr.targetType, false);
		} else {
			for (BaseSubclassType bst : bpr.subclassTypes) {
				generateLoadText(ps, indent, bst.elementName, "Base"+bst.targetType, true);
			}
		}
		return true;
	}
}
