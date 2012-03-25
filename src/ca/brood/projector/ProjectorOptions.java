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

