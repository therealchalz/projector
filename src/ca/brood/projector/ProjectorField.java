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

import ca.brood.projector.base.BaseProjectorField;
import java.io.PrintStream;
import ca.brood.projector.util.Util;
import org.apache.log4j.Logger;

public class ProjectorField extends BaseProjectorField {
	public ProjectorField() {
		super();
	}
	public boolean hasOptionMultiple() {
		if (this.getOptions().hasOption(ProjectorOptions.Options.MULTIPLE)) {
			return true;
		}
		return false;
	}
	private boolean isArray() {
		boolean optionIsArray = false;
		if (this.getOptions().getOptions().size() != 0) {
			for (String option : this.getOptions().getOptions()) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		return optionIsArray;
	}
	public boolean generateDeclaration(PrintStream ps, String indent) {
		if (!verifyFields()) {
			return false;
		}
		
		boolean optionIsArray = isArray();
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"protected String "+getName()+" = \"\";");
			} else if ("integer".equalsIgnoreCase(getType())) {
				if (getSize() <= 4 && getSize() > 0) {
					ps.println(indent+"protected int "+getName()+" = 0;");
				} else if (getSize() > 4 && getSize() <= 8) {
					ps.println(indent+"protected long "+getName()+" = 0;");
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(getType())) {
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
				log.error("Bad type for field: "+getType());
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"protected ArrayList<String> "+getName()+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(getType())) {
				if (getSize() <= 4 && getSize() > 0) {
					ps.println(indent+"protected ArrayList<Integer> "+getName()+" = new ArrayList<Integer>();");
				} else if (getSize() > 4 && getSize() <= 8) {
					ps.println(indent+"protected ArrayList<Long> "+getName()+" = new ArrayList<Long>();");
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(getType())) {
				log.error("Decimal fields are not implement yet");
				/*if (getSize() <= 4 && getSize() > 0) {
					ps.println(indent+"protected float "+getName()+" = 0.0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println(indent+"protected double "+getName()+" = 0.0;");
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}*/
			} else {
				log.error("Bad type for field: "+getType());
				return false;
			}
		}
		return true;
	}
	private boolean verifyFields() {
		if (this.getElementName().equals("")) {
			this.setElementName(this.getName());
		}
		if (this.getName().length() == 0) {
			log.error("Bad name for field: ");
			return false;
		}
		return true;
	}
	public boolean generateGettersAndSetters(PrintStream ps, String indent) {
		if (!verifyFields()) {
			return false;
		}
		boolean optionIsArray = isArray();
		String fieldType = "error";
		
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(getType())) {
				fieldType = "String";
			} else if ("integer".equalsIgnoreCase(getType())) {
				if (getSize() <= 4 && getSize() > 0) {
					fieldType = "int";
				} else if (getSize() > 4 && getSize() <= 8) {
					fieldType = "long";
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(getType())) {
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
				log.error("Bad type for field: "+getType());
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(getType())) {
				fieldType = "ArrayList<String>";
			} else if ("integer".equalsIgnoreCase(getType())) {
				if (getSize() <= 4 && getSize() > 0) {
					fieldType = "ArrayList<Integer>";
				} else if (getSize() > 4 && getSize() <= 8) {
					fieldType = "ArrayList<Long>";
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}
			} else if ("decimal".equalsIgnoreCase(getType())) {
				log.error("Decimal fields are not implement yet");
				/*if (getSize() <= 4 && getSize() > 0) {
					ps.println(indent+"protected float "+getName()+" = 0.0;");
				} else if (gpf.size > 4 && gpf.size <= 8) {
					ps.println(indent+"protected double "+getName()+" = 0.0;");
				} else {
					log.error("Bad size for field: "+getSize());
					return false;
				}*/
			} else {
				log.error("Bad type for field: "+getType());
				return false;
			}
		}
		ps.println(indent+"public "+fieldType+" get"+Util.UppercaseFirstCharacter(getName())+"() {");
		ps.println(indent+"	return "+getName()+";");
		ps.println(indent+"}");
		ps.println(indent+"public void set"+Util.UppercaseFirstCharacter(getName())+"("+fieldType+" val) {");
		ps.println(indent+"	this."+getName()+" = val;");
		ps.println(indent+"}");
		return true;
	}
	public boolean generateReset(PrintStream ps, String indent) {
		boolean optionIsArray = false;
		if (getOptions().getOptions().size() != 0) {
			for (String option : getOptions().getOptions()) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+" = \"\";");
			} else if ("integer".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+" = 0;");
			} else if ("decimal".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+" = 0.0;");
			} else {
				log.error("Bad type for field: "+getType());
				return false;
			}
		} else {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+" = new ArrayList<String>();");
			} else if ("integer".equalsIgnoreCase(getType()) && getSize() <= 4) {
				ps.println(indent+"this."+getName()+" = new ArrayList<Integer>();");
			} else if ("integer".equalsIgnoreCase(getType()) && getSize() <= 8) {
				ps.println(indent+"this."+getName()+" = new ArrayList<Long>();");
			} else if ("decimal".equalsIgnoreCase(getType()) && getSize() <= 4) {
				ps.println(indent+"this."+getName()+" = new ArrayList<Float>();");
			} else if ("decimal".equalsIgnoreCase(getType()) && getSize() <= 8) {
				ps.println(indent+"this."+getName()+" = new ArrayList<Double>();");
			} else {
				log.error("Bad type for field: "+getType());
				return false;
			}
		}
		return true;
	}
	public boolean generateLoad(PrintStream ps, String indent) {
		boolean optionIsArray = false;
		if (getOptions().getOptions().size() != 0) {
			for (String option : getOptions().getOptions()) {
				if ("multiple".equalsIgnoreCase(option)) {
					optionIsArray = true;
				}
			}
		}
		ps.println(indent+"} else if (\""+getElementName()+"\".compareToIgnoreCase(currentConfigNode.getNodeName())==0){");
		indent += "\t";
		ps.println(indent+"configNode.removeChild(currentConfigNode);");
		if (!optionIsArray) {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+" = currentConfigNode.getFirstChild().getNodeValue();");
			} else if ("integer".equalsIgnoreCase(getType())) {
				ps.println(indent+"try {");
				ps.println(indent+"	this."+getName()+" = Util.parseInt(currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"} catch (Exception e) {");
				ps.println(indent+"	log.error(\"Error parsing "+getElementName()+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"}");
			} else if ("decimal".equalsIgnoreCase(getType())) {
				log.error("Decimal field loading not implemented yet...");
			}
		} else {
			if ("string".equalsIgnoreCase(getType())) {
				ps.println(indent+"this."+getName()+".add(currentConfigNode.getFirstChild().getNodeValue());");
			} else if ("integer".equalsIgnoreCase(getType())) {
				ps.println(indent+"try {");
				ps.println(indent+"	this."+getName()+".add(Util.parseInt(currentConfigNode.getFirstChild().getNodeValue()));");
				ps.println(indent+"} catch (Exception e) {");
				ps.println(indent+"	log.error(\"Error parsing "+getElementName()+": \"+currentConfigNode.getFirstChild().getNodeValue());");
				ps.println(indent+"}");
			} else if ("decimal".equalsIgnoreCase(getType())) {
				log.error("Decimal field loading not implemented yet...");
			}
		}
		return true;
	}
}
