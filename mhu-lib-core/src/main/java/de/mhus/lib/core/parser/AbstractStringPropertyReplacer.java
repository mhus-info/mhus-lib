package de.mhus.lib.core.parser;

/**
 * This class will search and replace properties in the format like ${key}
 * 
 * @author mikehummel
 *
 */
public abstract class AbstractStringPropertyReplacer {

	public String process(String in) {
		StringBuilder out = null;
		while (true) {
			int p = in.indexOf("${");
			if (p < 0) break;
			if (out == null) out = new StringBuilder();
			out.append(in.substring(0, p));
			int p2 = in.indexOf('}',p);
			if (p2 < 0 ) break;
			String key = in.substring(p+2,p);
			String val = findValueFor(key);
			if (val != null)
				out.append(val);
			// reduce 'in'
			in = in.substring(p+1);
		}
		
		if (out != null) {
			out.append(in);
			return out.toString();
		}
		
		return in;
		
	}
	
	public abstract String findValueFor(String key);
	
}