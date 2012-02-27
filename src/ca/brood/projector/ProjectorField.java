package ca.brood.projector;

import java.io.PrintStream;

import org.apache.log4j.Logger;

public class ProjectorField {
	private Logger log;
	private BaseProjectorField bpf;
	
	public ProjectorField() {
		log = Logger.getLogger("ProjectorField");
	}
	public ProjectorField(BaseProjectorField o) {
		this();
		bpf = o;
	}
	
	public boolean generateDeclaration(PrintStream ps, String indent) {
		if (bpf.name.length() == 0) {
			log.error("Bad name for field: ");
			return false;
		}
		if (bpf.elementName.equals("")) {
			bpf.elementName = bpf.name;
		}
		boolean optionIsArray = false;
		if (bpf.options.options.size() != 0) {
			for (String option : bpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"protected String "+bpf.name+" = \"\";");
			} else if ("integer".equalsIgnoreCase(bpf.type)) {
				if (bpf.size <= 4 && bpf.size > 0) {
					ps.println(indent+"protected int "+bpf.name+" = 0;");
				} else if (bpf.size > 4 && bpf.size <= 8) {
					ps.println(indent+"protected long "+bpf.name+" = 0;");
				} else {
					log.error("Bad size for field: "+bpf.size);
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(bpf.type)) {
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
				log.error("Bad type for field: "+bpf.type);
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"protected ArrayList<String> "+bpf.name+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(bpf.type)) {
				if (bpf.size <= 4 && bpf.size > 0) {
					ps.println(indent+"protected ArrayList<Integer> "+bpf.name+" = new ArrayList<Integer>();");
				} else if (bpf.size > 4 && bpf.size <= 8) {
					ps.println(indent+"protected ArrayList<Long> "+bpf.name+" = new ArrayList<Long>();");
				} else {
					log.error("Bad size for field: "+bpf.size);
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(bpf.type)) {
				log.error("Decimal fields are not implement yet");
				/*if (bpf.size <= 4 && bpf.size > 0) {
					ps.println(indent+"protected float "+bpf.name+" = 0.0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println(indent+"protected double "+bpf.name+" = 0.0;");
				} else {
					log.error("Bad size for field: "+bpf.size);
					return false;
				}*/
			} else {
				log.error("Bad type for field: "+bpf.type);
				return false;
			}
		}
		return true;
	}
	public boolean generateReset(PrintStream ps, String indent) {
		boolean optionIsArray = false;
		if (bpf.options.options.size() != 0) {
			for (String option : bpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+" = \"\";");
			} else if ("integer".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+" = 0;");
			} else if ("decimal".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+" = 0.0;");
			} else {
				log.error("Bad type for field: "+bpf.type);
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(bpf.type) && bpf.size <= 4) {
				ps.println(indent+"this."+bpf.name+" = new ArrayList<Integer>();");
			} else if ("integer".equalsIgnoreCase(bpf.type) && bpf.size <= 8) {
				ps.println(indent+"this."+bpf.name+" = new ArrayList<Long>();");
			} else if ("decimal".equalsIgnoreCase(bpf.type) && bpf.size <= 4) {
				ps.println(indent+"this."+bpf.name+" = new ArrayList<Float>();");
			} else if ("decimal".equalsIgnoreCase(bpf.type) && bpf.size <= 8) {
				ps.println(indent+"this."+bpf.name+" = new ArrayList<Double>();");
			} else {
				log.error("Bad type for field: "+bpf.type);
				return false;
			}
		}
		return true;
	}
	public boolean generateLoad(PrintStream ps, String indent) {
		boolean optionIsArray = false;
		if (bpf.options.options.size() != 0) {
			for (String option : bpf.options.options) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		ps.println(indent+"} else if (\""+bpf.elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		indent += "\t";
		ps.println(indent+"configNode.removeChild(currentConfigNode);");
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+" = currentConfigNode.getFirstChild().getNodeValue();");
			} else if ("integer".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"try {");
				ps.println(indent+"	this."+bpf.name+" = Util.parseInt(currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"} catch (Exception e) {");
				ps.println(indent+"	log.error(\"Error parsing "+bpf.elementName+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"}");
			} else if ("decimal".equalsIgnoreCase(bpf.type)) {
				log.error("Decimal field loading not implemented yet...");
			}
		} else {
			if ("string".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"this."+bpf.name+".add(currentConfigNode.getFirstChild().getNodeValue());");
			} else if ("integer".equalsIgnoreCase(bpf.type)) {
				ps.println(indent+"try {");
				ps.println(indent+"	this."+bpf.name+".add(Util.parseInt(currentConfigNode.getFirstChild().getNodeValue()));");
				ps.println(indent+"} catch (Exception e) {");
				ps.println(indent+"	log.error(\"Error parsing "+bpf.elementName+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"}");
			} else if ("decimal".equalsIgnoreCase(bpf.type)) {
				log.error("Decimal field loading not implemented yet...");
			}
		}
		return true;
	}
}
