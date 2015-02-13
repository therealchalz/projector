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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;



public class ProjectorDtdGenerator {
	private Logger log;
	private ArrayList<String> definedElements;
	private ArrayList<ProjectorObject> allObjects;
	public ProjectorDtdGenerator() {
		log = Logger.getLogger("ProjectorDtdGenerator");
		definedElements = new ArrayList<String>();
		allObjects = new ArrayList<ProjectorObject>();
	}
	private boolean writeObject(PrintStream ps, String rootElement, ProjectorObject bpo) {
		ArrayList<ProjectorField> fieldsToWrite = new ArrayList<ProjectorField>();
		ArrayList<ProjectorReference> objectsToWrite = new ArrayList<ProjectorReference>();
		
		ps.print("<!ELEMENT "+rootElement+" (");
		boolean firstField = true;
		for (ProjectorField bpf : bpo.getFields()) {
			if (firstField) {
				firstField = false;
			} else {
				ps.print(",");
			}
			boolean optionIsOptional = false;
			boolean optionIsArray = false;
			if (bpf.getOptions().getOptions().size() != 0) {
				for (String option : bpf.getOptions().getOptions()) {
					if ("optional".equalsIgnoreCase(option)) {
						optionIsOptional = true;
					}
					if ("multiple".equalsIgnoreCase(option)) {
						optionIsArray = true;
					}
				}
			}
			ps.print((bpf.getElementName().equals("") ? bpf.getName() : bpf.getElementName()));
			if (optionIsOptional && optionIsArray) {
				log.error("Error: "+bpf.getName()+" has both optional and multiple options");
			}
			if (optionIsOptional) {
				ps.print("?");
			} else if (optionIsArray) {
				ps.print("*");
			}
			if (!definedElements.contains((bpf.getElementName().equals("") ? bpf.getName() : bpf.getElementName()))) {
				definedElements.add((bpf.getElementName().equals("") ? bpf.getName() : bpf.getElementName()));
				fieldsToWrite.add(bpf);
			}
		}
		
		for (ProjectorReference bpr : bpo.getReferences()) {
			if (firstField) {
				firstField = false;
			} else {
				ps.print(",");
			}
			boolean optionIsOptional = false;
			if (bpr.getOptions().getOptions().size() != 0) {
				for (String option : bpr.getOptions().getOptions()) {
					if ("optional".equalsIgnoreCase(option)) {
						optionIsOptional = true;
					}
				}
			}
			ps.print((bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName()));
			if (bpr.getRelationship().equalsIgnoreCase("onetomany")) {
				ps.print("*");
			} else if (bpr.getRelationship().equalsIgnoreCase("onetoone")) {
				if (optionIsOptional) {
					ps.print("?");
				}
			} else {
				log.fatal("Unsupported relationship in DTD save: "+bpr.getRelationship());
				return false;
			}
			if (!definedElements.contains((bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName()))) {
				definedElements.add((bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName()));
				objectsToWrite.add(bpr);
			}
		}
		
		ps.println(")>");
		
		//Define the fields now
		for (ProjectorField bpf : fieldsToWrite) {
			ps.println("<!ELEMENT "+(bpf.getElementName().equals("") ? bpf.getName() : bpf.getElementName())+" (#PCDATA)>");
		}
		//Recurse on the objects here
		for (ProjectorReference bpr : objectsToWrite) {
			for (ProjectorObject nextObject : this.allObjects) {
				if (nextObject.getName().equals(bpr.getTargetType())) {
					if (!writeObject(ps, (bpr.getElementName().equals("") ? bpr.getName() : bpr.getElementName()), nextObject)) {
						return false;
					}
					break;
				}
			}
		}
		
		return true;
	}
	public boolean writeDtd(String filename, Projector bp) {
		PrintStream ps;
		try {
			ps = new PrintStream(filename);
		} catch (FileNotFoundException e) {
			log.error("Error creating output file: "+filename);
			return false;
		}
		log.debug("Generating: "+filename);
		
		//Get project info and all the objects
		ProjectorProject bpp = bp.getProject();
		this.allObjects = bp.getProjectObjects();
		
		//Start with root element/root object for DTD, then recurse
		ProjectorObject rootObject = null;
		for (ProjectorObject bpo : this.allObjects) {
			if (bpo.getName().equals(bpp.getRootObject())) {
				rootObject = bpo;
				break;
			}
		}
		if (rootObject == null) {
			log.fatal("Couldn't find root object: "+bpp.getRootObject());
			ps.close();
			return false;
		}
		writeObject(ps, bpp.getRootElement(), rootObject);
		
		
		log.debug("Done with "+filename);
		ps.close();
		return true;
	}
}
