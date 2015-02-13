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

import ca.brood.projector.base.BaseProjectorOptions;

public class ProjectorOptions extends BaseProjectorOptions {

	public enum Options {
		MULTIPLE("multiple"), 
		OPTIONAL("optional");
		private String text;
		private Options(String s) {
			text = s;
		}
	};
	public ProjectorOptions() {
		super();
	}

	public boolean hasOption(Options o) {
		for (String s : getOptions()) {
			if (o.text.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
}

