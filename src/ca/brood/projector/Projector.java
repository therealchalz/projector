package ca.brood.projector;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ca.brood.projector.util.SimpleXmlErrorHandler;
import ca.brood.projector.util.XmlErrorCallback;

public class Projector extends GeneratedProjector {
	
	public Projector() {
		super();
		PropertyConfigurator.configure("logger.config");
		log.info("Projector is starting...");
	}
	public static void main(String[] args) {
		Projector p = new Projector();
		
		p.run();
		
	}
	private boolean testDir(String dir) {
		File f = new File(dir);
		if (!f.isDirectory()) {
			if (!f.mkdir()) {
				if (!f.mkdirs()) {
					log.fatal("Couldn't create "+dir+" directory...");
					return false;
				}
			}
		}
		return true;
	}
	private void generate() {
		if (!testDir("projects"))
			return;
		
		if (!testDir("projects/"+this.project.name))
			return;

		String javaPath = "projects/"+this.project.name+"/"+this.project.projectPackage.replace(".", "/");
		if (!testDir(javaPath))
			return;
		
		log.info("Starting file generation");
		
		for (int i=0; i<this.projectObjects.size(); i++) {
			ProjectorObject gpo = new ProjectorObject(this.projectObjects.get(i));
			gpo.generate(javaPath, this.project.projectPackage);
		}
 
	}
	
	private void run(String filename) {
		log.info("Starting project: "+filename);
		
		File xmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(true);
		dbFactory.setNamespaceAware(true);
		XmlErrorCallback error = new XmlErrorCallback();
		Document doc;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.setErrorHandler(new SimpleXmlErrorHandler(this.log, error));
			doc = dBuilder.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			if (!error.isConfigValid()) {
				throw new Exception("Config doesn't conform to schema.");
			}
		} catch (Exception e) {
			log.fatal("Exception while trying to load config file: "+filename + e.getMessage());
			return;
		}
		Node currentConfigNode = doc.getDocumentElement();

		log.debug("Reading project configuration now");
		if ("projector".compareToIgnoreCase(currentConfigNode.getNodeName())==0) {
			log.debug("Configuring projector...");
			this.configure(currentConfigNode);
		} else {
			log.fatal("Bad XML file: root element isn't a projector.");
		}
		
		log.info("Generating...");
		
		this.generate();
		
		log.info("Done with "+filename);
	}
	
	private void installProjectorForTesting() {
		//recurse into the projects/Projector directory, and copy generated source files
		// over to the src directory, while renaming the old ones by appending .old just
		// to not break it too bad.
		log.debug("Starting install of projector generated classes...");
		installDir("projects/Projector/", "src/");
		log.debug("Done installing newly generated projector classes... does it still work?");
	}
	private void installDir(String checkDir, String installDir) {
		log.debug("Working on "+checkDir+" with install dir of "+installDir);
		File pwd = new File(checkDir);
		File[] pwdFiles = pwd.listFiles();
		for (int i=0; i<pwdFiles.length; i++) {
			if (pwdFiles[i].isDirectory()) {
				installDir(checkDir+pwdFiles[i].getName()+"/", installDir+pwdFiles[i].getName()+"/");
			} else {
				if (pwdFiles[i].getName().endsWith(".java")) {
					log.debug("Copying "+pwdFiles[i].getName()+" from "+checkDir+" to "+installDir);
					renameAndCopy(pwdFiles[i], installDir+pwdFiles[i].getName());
				}
			}
		}
	}
	private boolean renameAndCopy(File inFile, String outFile) {
		File of = new File(outFile);
		if (of.exists()) {
			File rename = new File(outFile+".old");
			of.renameTo(rename);
		}
		copyFile(inFile, of);
		return true;
	}
	private boolean copyFile(File in, File out) {
		InputStream is;
		OutputStream os;
		try {
			is = new FileInputStream(in);
			os = new FileOutputStream(out);
		} catch (FileNotFoundException e) {
			log.error("Error copying "+in.getName()+" to "+out.getName());
			return false;
		}
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0){
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			log.error("Error during copy of "+in.getName()+" to "+out.getName());
			return false;
		}
		return true;
	}

	public void run() {
		
		run("sampleproject.xml");
		run("projector.xml");
		
		installProjectorForTesting();
		
		log.info("Projector is finished.");
	}
	
}
