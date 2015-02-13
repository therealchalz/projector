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

import ca.brood.projector.base.BaseProjectorReference;
import java.io.PrintStream;
import ca.brood.projector.util.*;

public class ProjectorReference extends BaseProjectorReference {
	public ProjectorReference() {
		super();
	}


	public boolean isOnetoMany() {
		return "onetomany".equalsIgnoreCase(getRelationship());
	}
	public boolean isOnetoOne() {
		return "onetoone".equalsIgnoreCase(getRelationship());
	}
	
	public boolean generateDeclaration(PrintStream ps, String indent) {
		if (!verifyFields()) {
			return false;
		}
		String objType = getTargetType();
		if (this.isOnetoOne()) {
			ps.println(indent+"protected "+objType+" "+getName()+" = new "+objType+"();");
		} else if (this.isOnetoMany()) {
			ps.println(indent+"protected ArrayList<"+objType+"> "+getName()+" = new ArrayList<"+objType+">();");
		} else {
			log.error("Invalid reference relationship: "+getRelationship());
			return false;
		}
		return true;
	}
	private boolean verifyFields() {
		if (getElementName().equals("")) {
			setElementName(getName());
		}
		if (getTargetType().equals("")) {
			log.error("Invalid reference targetType: ");
			return false;
		}
		if (getName().equals("")) {
			log.error("Invalid reference name: ");
			return false;
		}
		return true;
	}
	public boolean generateGettersAndSetters(PrintStream ps, String indent) {
		if (!verifyFields()) {
			return false;
		}
		String objType = getTargetType();
		if (this.isOnetoOne()) {
			objType = getTargetType();
		} else if (this.isOnetoMany()) {
			objType = "ArrayList<"+getTargetType()+">";
		} else {
			log.error("Invalid reference relationship: "+getRelationship());
			return false;
		}
		
		ps.println(indent+"public "+objType+" get"+Util.UppercaseFirstCharacter(getName())+"() {");
		ps.println(indent+"	return "+getName()+";");
		ps.println(indent+"}");
		ps.println(indent+"public void set"+Util.UppercaseFirstCharacter(getName())+"("+objType+" val) {");
		ps.println(indent+"	this."+getName()+" = val;");
		ps.println(indent+"}");
		
		return true;
	}
	
	public boolean generateReset(PrintStream ps, String indent) {
		String objType = getTargetType();
		if (this.isOnetoOne()) {
			ps.println(indent+"this."+getName()+" = new "+objType+"();");
		} else if (this.isOnetoMany()) {
			ps.println(indent+"this."+getName()+" = new ArrayList<"+objType+">();");
		}
		return true;
	}
	private boolean generateLoadText(PrintStream ps, String indent, String elementName, String targetType, boolean reinstantiate) {
		ps.println(indent+"} else if (\""+elementName+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		indent += "\t";
		if (this.isOnetoOne()) {
			if (reinstantiate) {
				ps.println(indent+"this."+getName()+" = new "+targetType+"();");
			}
			ps.println(indent+"this."+getName()+".configure(currentConfigNode);");
		} else if (this.isOnetoMany()) {
			String objType = targetType;
			String variable = targetType.substring(0, 1).toLowerCase()+targetType.substring(1);
			ps.println(indent+objType+" "+variable+" = new "+objType+"();");
			ps.println(indent+"if ("+variable+".configure(currentConfigNode)){");
			ps.println(indent+"	this."+getName()+".add("+variable+");");
			ps.println(indent+"}");
		} else {
			log.error("Bad relationship: "+this.getRelationship());
			return false;
		}
		return true;
	}
	public boolean generateLoad(PrintStream ps, String indent) {
		if (getSubclassTypes().size() == 0) {
			generateLoadText(ps, indent, getElementName(), getTargetType(), false);
		} else {
			for (SubclassType bst : getSubclassTypes()) {
				generateLoadText(ps, indent, bst.getElementName(), bst.getTargetType(), true);
			}
		}
		return true;
	}
}

