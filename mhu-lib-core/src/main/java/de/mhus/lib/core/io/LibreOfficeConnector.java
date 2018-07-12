package de.mhus.lib.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.errors.NotFoundException;

public class LibreOfficeConnector {

	private String binary = "soffice";
	private boolean valid = false;
	private String version;
	
	public LibreOfficeConnector() {
		findVersion();
	}	
	
	private void findVersion() {
		valid = false;
		version = null;
		if (binary == null || binary.indexOf("soffice") < 0) return;
		try {
			String[] res = MSystem.execute(binary,"--version");
			version = res[0];
			valid = MString.isSet(version) && version.startsWith("LibreOffice ");
		} catch (IOException e) {
		}
		
	}

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
		findVersion();
	}

	public boolean isValid() {
		return valid;
	}

	public String getVersion() {
		return version;
	}

	public String convertToPdf(String in, String outDir) throws NotFoundException, IOException {
		return convertTo("pdf", in, outDir);
	}
	
	/**
	 * Convert from in file format to 'format'.
	 * 
	 * @param format The resulting format and filter, see https://ask.libreoffice.org/en/question/2641/convert-to-command-line-parameter/
	 * @param in Input file
	 * @param outDir output directory or null for the same location as the input file
	 * @return Path to the generated file
	 * 
	 * @throws NotFoundException
	 * @throws IOException
	 */
	public String convertTo(String format, String in, String outDir) throws NotFoundException, IOException {
		if (!valid)
			throw new NotFoundException("LibreOffice not found");
		
		File inFile = new File(in);
		if (!inFile.exists() || !inFile.isFile())
			throw new FileNotFoundException(in);
		
		if (outDir != null) {
			File outFile = new File(outDir);
			if (outFile.exists() && !outFile.isDirectory())
				throw new IOException("out directory is not a directory: " + outDir);

			if (!outFile.exists())
				if (!outFile.mkdirs())
					throw new IOException("can't create out directory: " + outDir);
		}
		
		String[] res = null;
		if (outDir == null)
			res = MSystem.execute(binary,"--headless","-convert-to",format,in);
		else
			res = MSystem.execute(binary,"--headless","-convert-to",format,"-outdir",outDir,in);
			
		for (String line : res[0].split("\n")) {
			line = line.trim();
			if (line.startsWith("convert ")) {
				int p1 = line.indexOf(" -> ");
				int p2 = line.indexOf(" using", p1);
				if (p1 > 0 && p2 > 0)
					return line.substring(p1+4, p2);
			}
		}
		return null;
	}
	
	/*
	 * This is for testing purposes ... output should be like ...
	 * 
	 *  LibreOffice 6.0.5.2 54c8cbb85f300ac59db32fe8a675ff7683cd5a16
	 *  /private/tmp/Devices.pdf
	 * 
	 */
	public static void main(String args[]) throws NotFoundException, IOException {
		LibreOfficeConnector inst = new LibreOfficeConnector();
		inst.setBinary("/Users/mikehummel/dev/LibreOffice.app/Contents/MacOS/soffice");
		System.out.println(inst.getVersion());
		String to = inst.convertToPdf("/Users/mikehummel/Devices.ods", "/tmp");
		System.out.println(to);
	}
}