package ca.brood.projector;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;



public class ProjectorDtdGenerator {
	private Logger log;
	private ArrayList<String> definedElements;
	private ArrayList<BaseProjectorObject> allObjects;
	public ProjectorDtdGenerator() {
		log = Logger.getLogger("ProjectorDtdGenerator");
		definedElements = new ArrayList<String>();
		allObjects = new ArrayList<BaseProjectorObject>();
	}
	private boolean writeObject(PrintStream ps, String rootElement, BaseProjectorObject bpo) {
		ArrayList<BaseProjectorField> fieldsToWrite = new ArrayList<BaseProjectorField>();
		ArrayList<BaseProjectorReference> objectsToWrite = new ArrayList<BaseProjectorReference>();
		
		ps.print("<!ELEMENT "+rootElement+" (");
		boolean firstField = true;
		for (BaseProjectorField bpf : bpo.fields) {
			if (firstField) {
				firstField = false;
			} else {
				ps.print(",");
			}
			boolean optionIsOptional = false;
			boolean optionIsArray = false;
			if (bpf.options.options.size() != 0) {
				for (String option : bpf.options.options) {
					if ("optional".equalsIgnoreCase(option)) {
						optionIsOptional = true;
					}
					if ("multiple".equalsIgnoreCase(option)) {
						optionIsArray = true;
					}
				}
			}
			ps.print((bpf.elementName.equals("") ? bpf.name : bpf.elementName));
			if (optionIsOptional && optionIsArray) {
				log.error("Error: "+bpf.name+" has both optional and multiple options");
			}
			if (optionIsOptional) {
				ps.print("?");
			} else if (optionIsArray) {
				ps.print("*");
			}
			if (!definedElements.contains((bpf.elementName.equals("") ? bpf.name : bpf.elementName))) {
				definedElements.add((bpf.elementName.equals("") ? bpf.name : bpf.elementName));
				fieldsToWrite.add(bpf);
			}
		}
		
		for (BaseProjectorReference bpr : bpo.references) {
			if (firstField) {
				firstField = false;
			} else {
				ps.print(",");
			}
			boolean optionIsOptional = false;
			if (bpr.options.options.size() != 0) {
				for (String option : bpr.options.options) {
					if ("optional".equalsIgnoreCase(option)) {
						optionIsOptional = true;
					}
				}
			}
			ps.print((bpr.elementName.equals("") ? bpr.name : bpr.elementName));
			if (bpr.relationship.equalsIgnoreCase("onetomany")) {
				ps.print("*");
			} else if (bpr.relationship.equalsIgnoreCase("onetoone")) {
				if (optionIsOptional) {
					ps.print("?");
				}
			} else {
				log.fatal("Unsupported relationship in DTD save: "+bpr.relationship);
				return false;
			}
			if (!definedElements.contains((bpr.elementName.equals("") ? bpr.name : bpr.elementName))) {
				definedElements.add((bpr.elementName.equals("") ? bpr.name : bpr.elementName));
				objectsToWrite.add(bpr);
			}
		}
		
		ps.println(")>");
		
		//Define the fields now
		for (BaseProjectorField bpf : fieldsToWrite) {
			ps.println("<!ELEMENT "+(bpf.elementName.equals("") ? bpf.name : bpf.elementName)+" (#PCDATA)>");
		}
		//Recurse on the objects here
		for (BaseProjectorReference bpr : objectsToWrite) {
			for (BaseProjectorObject nextObject : this.allObjects) {
				if (nextObject.name.equals(bpr.targetType)) {
					if (!writeObject(ps, (bpr.elementName.equals("") ? bpr.name : bpr.elementName), nextObject)) {
						return false;
					}
					break;
				}
			}
		}
		
		return true;
	}
	public boolean writeDtd(String filename, BaseProjector bp) {
		PrintStream ps;
		try {
			ps = new PrintStream(filename);
		} catch (FileNotFoundException e) {
			log.error("Error creating output file: "+filename);
			return false;
		}
		log.debug("Generating: "+filename);
		
		//Get project info and all the objects
		BaseProjectorProject bpp = bp.project;
		this.allObjects = bp.projectObjects;
		
		//Start with root element/root object for DTD, then recurse
		BaseProjectorObject rootObject = null;
		for (BaseProjectorObject bpo : this.allObjects) {
			if (bpo.name.equals(bpp.rootObject)) {
				rootObject = bpo;
				break;
			}
		}
		if (rootObject == null) {
			log.fatal("Couldn't find root object: "+bpp.rootObject);
			ps.close();
			return false;
		}
		writeObject(ps, bpp.rootElement, rootObject);
		
		
		log.debug("Done with "+filename);
		ps.close();
		return true;
	}
}
