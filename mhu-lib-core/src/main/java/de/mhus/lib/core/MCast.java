/*
 * ./core/de/mhu/lib/ACast.java
 *  Copyright (C) 2002-2004 Mike Hummel
 *
 *  This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package de.mhus.lib.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.mhus.lib.core.cast.Caster;
import de.mhus.lib.core.cast.DoubleToString;
import de.mhus.lib.core.cast.FloatToString;
import de.mhus.lib.core.cast.ObjectToBoolean;
import de.mhus.lib.core.cast.ObjectToCalendar;
import de.mhus.lib.core.cast.ObjectToDouble;
import de.mhus.lib.core.cast.ObjectToFloat;
import de.mhus.lib.core.cast.ObjectToInteger;
import de.mhus.lib.core.cast.ObjectToLong;
import de.mhus.lib.core.cast.ObjectToSqlDate;
import de.mhus.lib.core.cast.ObjectToString;
import de.mhus.lib.core.cast.ObjectToUUID;
import de.mhus.lib.core.cast.ObjectToDate;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.VectorMap;


/**
 * 
 * Smplifies casts between java classes. Some functions in this class only make
 * the code readable. e.g. from string to int.
 * <p>
 * All Funktions are static.
 * 
 * @author jesus
 */
public final class MCast {

	private static Log log = Log.getLog(MCast.class);
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
//			"yyyy-MM-dd_HH:mm:ss.SSS z");

	private static final char[] HEX_CHAR_TABLE = {
	    '0', '1', '2', '3','4', '5', '6', '7',
	    '8', '9', 'a', 'b','c', 'd', 'e', 'f'
	};
	
	private static VectorMap<Class<?>, Class<?>, Caster<?, ?>> casters = new VectorMap<>();
	
	private final static ObjectToBoolean OBJECT_TO_BOOLEAN = new ObjectToBoolean();
	private final static ObjectToInteger OBJECT_TO_INTEGER = new ObjectToInteger();
	private final static ObjectToLong OBJECT_TO_LONG = new ObjectToLong();
	private final static ObjectToDouble OBJECT_TO_DOUBLE = new ObjectToDouble();
	private final static ObjectToFloat OBJECT_TO_FLOAT = new ObjectToFloat();
	private final static ObjectToCalendar OBJECT_TO_CALENDAR = new ObjectToCalendar();
	private final static ObjectToDate OBJECT_TO_DATE = new ObjectToDate();
	private final static ObjectToSqlDate OBJECT_TO_SQLDATE = new ObjectToSqlDate();
	private final static DoubleToString DOUBLE_TO_STRING = new DoubleToString();
	private final static FloatToString FLOAT_TO_STRING = new FloatToString();
	private final static ObjectToString OBJECT_TO_STRING = new ObjectToString();
	
	static {
		addCaster(OBJECT_TO_BOOLEAN, true);
		addCaster(OBJECT_TO_INTEGER, true);
		addCaster(OBJECT_TO_LONG, true);
		addCaster(OBJECT_TO_DOUBLE, true);
		addCaster(OBJECT_TO_FLOAT, true);
		addCaster(OBJECT_TO_STRING, true);
		addCaster(OBJECT_TO_DATE, true);
		addCaster(OBJECT_TO_SQLDATE, true);
		addCaster(OBJECT_TO_CALENDAR, true);
		addCaster(DOUBLE_TO_STRING, true);
		addCaster(FLOAT_TO_STRING, true);
		addCaster(new ObjectToUUID(), true);
	}
	/**
	 * Will round the value mathematically and
	 * return every time a comma as separator and
	 * two digits after comma.
	 * 
	 * @param _in
	 * @return
	 */
	public static String toCurrencyString(double _in) {

		// round
		_in = Math.round(_in * 100d) / 100d;
		// out
		StringBuffer out = new StringBuffer();
		out.append(_in);

		// change "." to ","
		int pos = out.indexOf( "." );
		if ( pos >= 0 )
			out.setCharAt(pos, ',');
		else
			pos = out.indexOf(",");
		
		if ( pos <= 0 )
			out.append(",00");
		else
		if ( out.length() - pos <= 2 )
			out.append("0");

		return out.toString();

	}

	public static void addCaster(Caster<?,?> caster, boolean overwrite) {
		if (!overwrite && casters.containsKey(caster.getFromClass(), caster.getToClass())) return;
		casters.put(caster.getFromClass(), caster.getToClass(), caster);
	}

	/**
	 * Try to parse a String and return the equivalent Date object. The string
	 * should contain a iso date string like "yyyy-mm-dd" syntax:
	 * "yyyy-mm-dd[[ HH:MM:SS].XXX]" where XXX is Millisecond. Milliseconds are
	 * ignored. For the date part there are alternative syntax: "dd.mm.yyyy" or
	 * "mm/dd/yyyy". Or a timestamp.
	 * <p>
	 * If the time is not in the string, it will be set to "00:00:00". It is
	 * possible to leave year, in this case it will be replaced with the actuall
	 * year. If you leave month, it will be replaced with the actuall month.
	 * 
	 * @param in
	 * @param def
	 * @return In all cases an Date() object. Is getTime() is 0, it occurs an
	 *         error: ACast.toDate( in ).getTime == 0.
	 */

	public static Date toDate(String in,Date def) {
		return OBJECT_TO_DATE.cast(in, def);
	}

	public static Calendar toCalendar(Date in) {
		return OBJECT_TO_CALENDAR.cast(in, null);
	}
	
	public static Calendar toCalendar(Date _in, TimeZone tz, Locale l) {
		Calendar calendar = Calendar.getInstance(tz,l);
		calendar.setTime(_in);
		return calendar;
	}
	
	/**
	 * Parse a time date string.
	 * 
	 * @param in
	 * @return
	 */
	public static Calendar toCalendar(String in) {
		return OBJECT_TO_CALENDAR.cast(in, null);
	}

	/**
	 * Parse time and date and return calendar.
	 * 
	 * @param in
	 * @param def
	 * @return
	 */
	public static Calendar toCalendar(String in, Calendar def) {
		return OBJECT_TO_CALENDAR.cast(in, def);
	}
	
	/**
	 * Return the date as string with format: yyyy-MM-dd_HH:mm:ss.SSS
	 * using a date formater.
	 * 
	 * @param _in
	 * @return
	 */
//	public static String toString(Date _in) {
//		if (_in == null) return null;
//		// return _in.getDate() + "." + (_in.getMonth()+1) + "." +
//		// (_in.getYear() + 1900 );
//		synchronized (dateFormat) {
//			return dateFormat.format(_in);
//		}
//	}

	/**
	 * Return the date as string with format: yyyy-MM-dd_HH:mm:ss.SSS
	 * using a date formater.
	 * 
	 * @param _in
	 * @return
	 */
//	public static String toString(Calendar _in) {
//		if (_in == null) return null;
//		synchronized (dateFormat) {
//			return dateFormat.format(_in.getTime());
//		}
//	}
		
	/**
	 * Convert the byte array to a string representation. It stores for every
	 * byte a two letter hex value in the string.
	 * 
	 * @param in
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toBinaryString(byte[] in ) {
		char[] hex = new char[2 * in.length];
	    int index = 0;

	    for (byte b : in) {
	      int v = b & 0xFF;
	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	    }
	    return new String(hex);
	}
	
	/**
	 * Convert a string with hex values in a byte array.
	 * 
	 * @see toBinaryString
	 * @param in
	 * @return
	 */
	public static byte[] fromBinaryString(String in) {
		byte[] out = new byte[ in.length() / 2 ];
		for ( int i = 0; i < out.length; i++ )
			out[i] = byteFromHex( in, i*2 );
		return out;
	}

	/**
	 * Convert a two letter hex value to a single byte value.
	 * 
	 * @param in
	 * @param offset
	 * @return
	 */
	public static byte byteFromHex( String in, int offset ) {
		int i = Integer.parseInt(in.substring(offset, offset+2), 16);
		byte b =(byte)( i & 0xFF );
        return b;
	}
	
	/**
	 * Convert a byte to a two letter hex value.
	 * 
	 * @param in
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toHex2String( byte in ) {
		char[] hex = new char[2];

       int v = in & 0xFF;
       hex[0] = HEX_CHAR_TABLE[v >>> 4];
       hex[1] = HEX_CHAR_TABLE[v & 0xF];
       
	   return new String(hex);
	}
	
	/**
	 * Convert String to boolean. If the conversion was not possible it returns
	 * "_default".
	 * 
	 * Valide true values: yes, true, ja, 1, t
	 * 
	 * valide false values: no, falsae, nein, 0, f
	 * @param _in
	 * @param _default
	 * @return
	 */
	public static boolean toboolean(Object _in, boolean _default) {
		return OBJECT_TO_BOOLEAN.toBoolean(_in,_default,null);
	}

	/**
	 * Convert a string to float. If the string is malformed it returns
	 * "_def".
	 * 
	 * @param in
	 * @param def
	 * @return
	 */
	public static float tofloat(Object in, float def) {
		return OBJECT_TO_FLOAT.toFloat(in, def, null);
	}

	/**
	 * Convert a string to double. If the string is malformed it returns
	 * "def".
	 * @param in 
	 * @param def 
	 * @return 
	 */
	public static double todouble(Object in, double def) {
		return OBJECT_TO_DOUBLE.toDouble(in, def, null);
	}

	/**
	 * Converts String to int. If the string is malformed then it returns "_def". 
	 * A valid format is also the hex 0x (e.g. 0xFFFF) variant.
	 * 
	 * @param in
	 * @param def
	 * @return
	 */
	public static int toint(Object in, int def) {
		return OBJECT_TO_INTEGER.toInt(in, def, null);
	}

	/**
	 * Converts a string to long. If the string is malformed then it returns "_def". 
	 * A valid format is also the hex 0x (e.g. 0xFFFF) variant.
	 * 
	 * @param in
	 * @param def
	 * @return
	 */
	public static long tolong(Object in, long def) {
		return OBJECT_TO_LONG.toLong(in, def, null);
	}

	/**
	 * Convert a double to string. The separator is
	 * a dot.
	 * 
	 * @param in
	 * @return
	 */
	public static String toString(double in) {
		return DOUBLE_TO_STRING.toString(in);
	}

	/**
	 * Convert a double to string. The separator is
	 * a dot.
	 * 
	 * @param in
	 * @return
	 */
	public static String toString(float in) {
		return FLOAT_TO_STRING.toString(in);
	}
	
	/**
	 * Convert a boolean to string. Values are "true", "false".
	 * 
	 * @param _in
	 * @return
	 */
	public static String toString(boolean _in) {
		if (_in)
			return "true";
		else
			return "false";
	}

	/**
	 * Converts integer to String.
	 * 
	 * @param _in
	 * @return
	 */
	public static String toString(int _in) {
		return Integer.toString(_in);
	}

	/**
	 * Converts integer to string with the minimum digits.
	 * 
	 * @param _in
	 * @param _numbers
	 * @return
	 */
	public static String toString(int _in, int _digits) {
		// FIXME performance please !
		String out = Integer.toString(_in);
		while (out.length() < _digits)
			out = "0" + out;
		return out;
	}

	/**
	 * Convert long to string.
	 * 
	 * @param _in
	 * @return
	 */
	public static String toString(long _in) {
		return String.valueOf(_in);
	}

	
	/**
	 * Convert integer to two letter hex code. Ignores negative values.
	 * 
	 * @param _in
	 * @return
	 */
	public static String toHex2String(int _in) {
		String out = Integer.toHexString(_in).toUpperCase();
		if (out.length() == 1)
			out = "0" + out;
		return out;
	}

	/**
	 * Convert integer to four letter hex code. Ignores negative values.
	 * 
	 * @param _in
	 * @return
	 */
	public static String toHex4String(int _in) {
		return toHex2String(_in / 256) + toHex2String(_in % 256);
	}

	/**
	 * Put all list elements in a string list. Use the toString method.
	 * 
	 * @param _v
	 * @return
	 */
	public static String[] toStringArray(List<?> _v) {

		String[] out = new String[_v.size()];
		for (int i = 0; i < _v.size(); i++) {
			Object o = _v.get(i);
			if (o == null)
				out[i] = null;
			else
				out[i] = o.toString();
		}
		return out;
	}


	public static int tointFromHex(String _in) {

		int out = 0;
		for (int i = 0; i < _in.length(); i++) {
			int x = 0;
			char c = _in.charAt(i);
			if (c >= '0' && c <= '9')
				x = (c - '0');
			else if (c >= 'a' && c <= 'f')
				x = (c - 'a' + 10);
			else if (c >= 'A' && c <= 'F')
				x = (c - 'A' + 10);
			else
				throw new NumberFormatException(_in);
			out = out * 16 + x;
		}

		return out;
	}

	public static String toString(byte[] in) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < in.length; i++) {
			if (i != 0)
				sb.append(',');
			sb.append(Byte.toString(in[i]));
		}
		return sb.toString();

	}

	public static byte[] toByteArray(String in) {

		if (in.length() == 0)
			return new byte[0];

		int offset = 0;
		int cnt = 0;

		while ((offset = in.indexOf(',', offset + 1)) >= 0) {
			cnt++;
		}

		offset = 0;
		int old = 0;
		byte[] out = new byte[cnt + 1];
		cnt = 0;
		while ((offset = in.indexOf(',', offset + 1)) >= 0) {
			out[cnt] = Byte.parseByte(in.substring(old, offset));
			cnt++;
			old = offset + 1;
		}

		out[cnt] = Byte.parseByte(in.substring(old));

		return out;
	}

	public static String toString(String firstLine, StackTraceElement[] trace) {
		StringBuffer sb = new StringBuffer();
		if (firstLine != null)
			sb.append(firstLine).append('\n');
		if (trace == null)
			return sb.toString();

		for (int i = 0; i < trace.length; i++)
			sb.append("\tat ").append(trace[i].getClassName()).append('.')
					.append(trace[i].getMethodName()).append('(').append(
							trace[i].getFileName()).append(':').append(
							trace[i].getLineNumber()).append(")\n");
		return sb.toString();
	}
	
	/**
	 * Return an indexed map of the values. The first value has the index "0" and so on.
	 * 
	 * @param values
	 * @return
	 */
	public static Map<String,Object> toIndexedMap(Object ... values) {
		HashMap<String,Object> out = new HashMap<String, Object>();
		for (int i = 0; i < values.length; i++) {
			out.put(toString(i), values[i]);
		}
		return out;
	}

	public static String objectToString(Object value) {
		
		if (value == null) return null;
		
		if (value instanceof Integer)
			return toString((Integer)value);
		if (value instanceof Long)
			return toString((Long)value);
		if (value instanceof Double)
			return toString((Double)value);
		if (value instanceof Float)
			return toString((Float)value);
		if (value instanceof Date)
			return toString((Date)value);
		if (value instanceof Calendar)
			return toString((Calendar)value);

		return value.toString();
	}

	public static Date objectToDate(Object value) {
		if (value == null) return null;
		if (value instanceof Date)
			return (Date)value;
		if (value instanceof Calendar)
			return ((Calendar)value).getTime();
		return toDate(String.valueOf(value), null);
	}

	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static String toString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
	
	public static String toString(Object in) {
		if (in == null) return "";
		return OBJECT_TO_STRING.cast(in, "");
	}
	
	public static String toString(Object in, String def) {
		if (in == null) return def;
		return OBJECT_TO_STRING.cast(in, def);
	}
	
	@SuppressWarnings("unchecked")
	public static Object toType(Object in, Class<?> type, Object def) {
		if (in == null) return def;
		if (type.isInstance(in)) return in;
		
		if (String.class.isAssignableFrom(type))
			return toString(in);
		if (boolean.class.isAssignableFrom(type))
			return toboolean(in,def == null ? false : (boolean)def);
		if (int.class.isAssignableFrom(type))
			return toint(in,def == null ? 0 : (int)def);
		if (long.class.isAssignableFrom(type))
			return tolong(in,def == null ? 0 : (long)def);

		Caster<?, ?> caster = casters.get(in.getClass(), type);
		if (caster == null)
			caster = casters.get(Object.class, type);
		if (caster == null)
			return def;
		
		return ((Caster<Object,Object>)caster).cast(in, def);
	}

}