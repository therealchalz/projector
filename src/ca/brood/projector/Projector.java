package ca.brood.projector;

import java.io.*;

public class Projector extends BaseProjector {
	
	public Projector() {
		super();
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
		
		if (!testDir("projects/"+this.project.name+"/lib/"))
			return;

		String javaPath = "projects/"+this.project.name+"/src/"+this.project.projectPackage.replace(".", "/");
		if (!testDir(javaPath))
			return;
		
		if (!testDir(javaPath+"/util"))
			return;
		
		log.info("Starting file generation");
		
		for (int i=0; i<this.projectObjects.size(); i++) {
			ProjectorObject gpo = new ProjectorObject(this.projectObjects.get(i));
			gpo.generate(javaPath, this.project.projectPackage, this.project);
		}
		String[] stdFiles = {"BaseGenerated.java","SimpleXmlErrorHandler.java", "Util.java", "XmlErrorCallback.java"};
		copyStdFiles("src/ca/brood/projector/", "projects/"+this.project.name+"/src/"+this.project.projectPackage.replaceAll("\\.", "/")+"/", stdFiles, "ca.brood.projector", this.project.projectPackage);
		//Hack: install a default logger.config file
		String[] loggerFile = {"logger.config"};
		copyStdFiles(".", "projects/"+this.project.name+"/", loggerFile, "projector",this.project.name.toLowerCase());
		
		//Copy logger libs over now
		copyFile(new File("lib/log4j-1.2.16.jar"), new File("projects/"+this.project.name+"/lib/log4j-1.2.16.jar"));
	}
	
	private void copyStdFiles(String checkDir, String installDir, String[] stdFiles, String oldPackage, String newPackage) {
		log.debug("Std file copy: Working on "+checkDir+" with install dir of "+installDir);
		log.debug("Std file copy: Old package: "+oldPackage+" New Package: "+newPackage);
		File pwd = new File(checkDir);
		File[] pwdFiles = pwd.listFiles();
		if (pwdFiles == null)
			return;
		for (int i=0; i<pwdFiles.length; i++) {
			if (pwdFiles[i].isDirectory()) {
				if (!oldPackage.endsWith(".") && oldPackage.length() > 0)
					oldPackage = oldPackage+".";
				if (!newPackage.endsWith(".") && newPackage.length() > 0)
					newPackage = newPackage+".";
				String fn = pwdFiles[i].getName();
				copyStdFiles(checkDir+fn+"/", installDir+fn+"/", stdFiles, oldPackage+fn, newPackage+fn);
			} else {
				for (int j=0; j<stdFiles.length; j++) {
					if (stdFiles[j].equals(pwdFiles[i].getName())) {
						log.debug("Copying "+pwdFiles[i].getName()+" from "+checkDir+" to "+installDir);
						copyFileWithPackageReplacement(pwdFiles[i], new File(installDir+pwdFiles[i].getName()), oldPackage, newPackage);
					}
				}
			}
		}
	}
	
	private void run(String filename) {
		if (!this.load(filename))
			return;
		
		log.info("Generating...");
		
		this.generate();
		
		log.info("Done with "+filename);
	}
	
	private void installProjectorForTesting() {
		//recurse into the projects/Projector directory, and copy generated source files
		// over to the src directory, while renaming the old ones by appending .old just
		// to not break it too bad.
		log.debug("Starting install of projector generated classes...");
		installDir("projects/Projector/src/", "src/");
		
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
		return copyFileWithPackageReplacement(in, out, "", "");
	}
	private boolean copyFileWithPackageReplacement(File in, File out, String oldP, String newP) {
		InputStream is;
		OutputStream os;
		try {
			is = new FileInputStream(in);
			os = new FileOutputStream(out);
		} catch (Exception e) {
			log.error("Error copying "+in.getName()+" to "+out.getName());
			log.error(e.getMessage());
			return false;
		}
		
		boolean packageCheck = (oldP.length() > 0 ? true : false);
		
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0){
				if (packageCheck) {
					packageCheck = false; //only expect package in first 1024
					String inLine = new String(buf).substring(0, len);
					inLine = inLine.replaceFirst(oldP.replaceAll("\\.", "\\\\."), newP);
					buf = inLine.getBytes();
					len = inLine.length();
				}
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			log.error("Error during copy of "+in.getName()+" to "+out.getName());
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	public void run() {
		run("sampleproject.xml");
		
		this.save("sampleprojectOut.xml");
		
		run("projector.xml");
		
		this.save("projectorOut.xml");
		
		installProjectorForTesting();
		
		log.info("Projector is finished.");
	}
	
}
