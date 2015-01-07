package de.mhus.lib.core;

public class MValidator {

	public static boolean isEmailAddress(String email) {
		if (email == null) return false;
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	}
	
	public static boolean isFirstName(String in) {
		if (in == null) return false;
		if (in.length() < 2) return false;
		return in.matches( "[A-Z][a-zA-Z]*" );
	}
	
	public static boolean isLastName(String in) {
		if (in == null) return false;
		if (in.length() < 2) return false;
		return in.matches( "[a-zA-z]+([ '-][a-zA-Z]+)*" );
	}
	
	public static boolean isAddress( String in ) {
		if (in == null) return false;
		if (in.length() < 2) return false;
	      return in.matches( "\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)" );
	}
	
	public static boolean isPassword(String in, int maxLen, boolean needNumbers, boolean needSpecials) {
		if (in == null) return false;
		if (in.length() < maxLen) return false;
		if (in.length() == 0) return true;
		
		char c = in.charAt(0);
		if (!(c >='a' && c <= 'z' || c >='A' && c <= 'Z')) return false; // need to start with a letter
		if (!needNumbers && !needSpecials) return true;
		
		boolean hasNumber = false;
		boolean hasSpecial = false;
		for (int i = 0; i < in.length(); i++) {
			c = in.charAt(i);
			if (c >= '0' && c <= '9') hasNumber = true;
			else
			if (!(c >='a' && c <= 'z' || c >='A' && c <= 'Z')) hasSpecial = true;
		}
		
		if (needNumbers && !hasNumber) return false;
		if (needSpecials && !hasSpecial) return false;
		return true;
		
	}
	
	
}