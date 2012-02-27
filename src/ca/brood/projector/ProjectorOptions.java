package ca.brood.projector;

import org.apache.log4j.Logger;

public class ProjectorOptions {
	private Logger log;
	private BaseProjectorOptions bpo;
	public enum Options {
		MULTIPLE("multiple"), 
		OPTIONAL("optional");
		private String text;
		private Options(String s) {
			text = s;
		}
	};
	
	public ProjectorOptions() {
		log = Logger.getLogger("ProjectorOptions");
	}
	public ProjectorOptions(BaseProjectorOptions o) {
		this();
		bpo = o;
	}
	
	public boolean hasOption(Options o) {
		for (String s : bpo.options) {
			if (o.text.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
}
