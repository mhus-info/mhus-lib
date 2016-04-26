package de.mhus.lib.core.pojo;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

import de.mhus.lib.core.AbstractProperties;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.cast.Caster;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.Base64;

public class MPojo {

	private static final int MAX_LEVEL = 10;
	private static Log log = Log.getLog(MPojo.class);
	private static PojoModelFactory defaultModelFactory;

	public static synchronized PojoModelFactory getDefaultModelFactory() {
		if (defaultModelFactory == null)
			defaultModelFactory = new PojoModelFactory() {
			
			@Override
			public PojoModel createPojoModel(Class<?> pojoClass) {
				PojoModel model = new PojoParser().parse(pojoClass,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();
				return model;
			}
		};
		return defaultModelFactory;
	}
	
	public static void pojoToJson(Object from, ObjectNode to) throws IOException {
		pojoToJson(from, to, getDefaultModelFactory());
	}
	
	public static void pojoToJson(Object from, ObjectNode to, PojoModelFactory factory) throws IOException {
		pojoToJson(from, to, factory, 0);
	}
	
	public static void pojoToJson(Object from, ObjectNode to, PojoModelFactory factory, int level) throws IOException {
		if (level > MAX_LEVEL) return;
		PojoModel model = factory.createPojoModel(from.getClass());
		for (PojoAttribute<?> attr : model) {
			
			if (!attr.canRead()) continue;

			Object value = attr.get(from);
			String name = attr.getName();
			setJsonValue(to, name, value, factory, false, level+1);
		}
	}

	@SuppressWarnings("unchecked")
	public static void addJsonValue(ArrayNode to, Object value, PojoModelFactory factory, boolean deep, int level) throws IOException {
		if (level > MAX_LEVEL) return;
		
		if (value == null)
			to.addNull();
		else
		if (value instanceof Boolean)
			to.add((boolean)value);
		else
		if (value instanceof Integer)
			to.add((int)value);
		else
		if (value instanceof String)
			to.add((String)value);
		else
		if (value instanceof Long)
			to.add((Long)value);
		else
		if (value instanceof byte[])
			to.add((byte[])value);
		else
		if (value instanceof Float)
			to.add((Float)value);
		else
		if (value instanceof BigDecimal)
			to.add((BigDecimal)value);
		else
		if (value instanceof JsonNode)
			to.add((JsonNode)value);
		else
		if (value.getClass().isEnum()) {
			to.add(((Enum<?>)value).ordinal());
//			to.put(name + "_", ((Enum<?>)value).name());
		} else
		if (value instanceof Map) {
			ObjectNode obj = to.objectNode();
			to.add(obj);
			for (Map.Entry<Object, Object> entry : ((Map<Object,Object>)value).entrySet()) {
				setJsonValue(obj, String.valueOf(entry.getKey()), entry.getValue(), factory, true, level+1 );
			}
		} else
		if (value instanceof Collection) {
			ArrayNode array = to.arrayNode();
			to.add(array);
			for (Object o : ((Collection<Object>)value)) {
				addJsonValue(array,o,factory,true,level+1);
			}
		} else {
			if (deep) {
				ObjectNode too = to.objectNode();
				to.add(too);
				pojoToJson(value, too, null, level+1);
			} else {
				to.add(String.valueOf(value));
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static void setJsonValue(ObjectNode to, String name, Object value, PojoModelFactory factory, boolean deep, int level) throws IOException {
		if (level > MAX_LEVEL) return;
		if (value == null)
			to.putNull(name);
		else
		if (value instanceof Boolean)
			to.put(name, (boolean)value);
		else
		if (value instanceof Integer)
			to.put(name, (int)value);
		else
		if (value instanceof String)
			to.put(name, (String)value);
		else
		if (value instanceof Long)
			to.put(name, (Long)value);
		else
		if (value instanceof byte[])
			to.put(name, (byte[])value);
		else
		if (value instanceof Float)
			to.put(name, (Float)value);
		else
		if (value instanceof Date) {
			to.put(name, ((Date)value).getTime() );
			to.put(name + "_", MDate.toIso8601((Date)value) );
		} else
		if (value instanceof BigDecimal)
			to.put(name, (BigDecimal)value);
		else
		if (value instanceof JsonNode)
			to.put(name, (JsonNode)value);
		else
		if (value.getClass().isEnum()) {
			to.put(name, ((Enum<?>)value).ordinal());
			to.put(name + "_", ((Enum<?>)value).name());
		} else
		if (value instanceof Map) {
			ObjectNode obj = to.objectNode();
			to.put(name, obj);
			for (Map.Entry<Object, Object> entry : ((Map<Object,Object>)value).entrySet()) {
				setJsonValue(obj, String.valueOf(entry.getKey()), entry.getValue(), factory, true, level+1 );
			}
		} else
		if (value instanceof Collection) {
			ArrayNode array = to.arrayNode();
			to.put(name, array);
			for (Object o : ((Collection<Object>)value)) {
				addJsonValue(array,o,factory,true,level+1);
			}
		} else {
			if (deep) {
				ObjectNode too = to.objectNode();
				to.put(name, too);
				pojoToJson(value, too, factory, level+1);
			} else {
				to.put(name, String.valueOf(value));
			}
		}
	}

	public static void jsonToPojo(JsonNode from, Object to) throws IOException {
		jsonToPojo(from, to, getDefaultModelFactory());
	}
	
	@SuppressWarnings("unchecked")
	public static void jsonToPojo(JsonNode from, Object to, PojoModelFactory factory) throws IOException {
		PojoModel model = factory.createPojoModel(to.getClass());
		for (PojoAttribute<Object> attr : model) {
			
			if (!attr.canWrite()) continue;

			String name = attr.getName();
			Class<?> type = attr.getType();
			JsonNode json = from.get(name);
			
			try {
				if (json == null || !attr.canWrite() ) {
					
				} else
				if (type == Boolean.class || type == boolean.class)
					attr.set(to, json.getValueAsBoolean(false));
				else
				if (type == Integer.class || type == int.class)
					attr.set(to, json.getValueAsInt(0));
				else
				if (type == String.class)
					attr.set(to, json.getValueAsText());
				else
				if (type == UUID.class)
					try {
						attr.set(to, UUID.fromString(json.getValueAsText()));
					} catch (IllegalArgumentException e) {
						attr.set(to, null);
					}
				else
				if (type.isEnum()) {
					Object[] cons=type.getEnumConstants();
					int ord = json.getValueAsInt(0);
					Object c = cons.length > 0 ? cons[0] : null;
					if (ord >=0 && ord < cons.length) c = cons[ord];
					attr.set(to, c );
				}
				else
					attr.set(to, json.getValueAsText());
			} catch (Throwable t) {
				log.d(MSystem.getClassName(to), name, t);
			}
		}
	}

	public static void pojoToXml(Object from, Element to) throws IOException {
		pojoToXml(from, to, getDefaultModelFactory());
	}

	public static void pojoToXml(Object from, Element to, PojoModelFactory factory) throws IOException {
		pojoToXml(from, to, factory, 0);
	}
	
	public static void pojoToXml(Object from, Element to, PojoModelFactory factory, int level) throws IOException {
		if (level > MAX_LEVEL) return;
		PojoModel model = factory.createPojoModel(from.getClass());
		for (PojoAttribute<?> attr : model) {
			
			try {
				if (!attr.canRead()) continue;
	
				Object value = attr.get(from);
				String name = attr.getName();
				
				Element a = to.getOwnerDocument().createElement("attribute");
				to.appendChild(a);
				a.setAttribute("name", name);
				
				if (value == null) {
					a.setAttribute("null", "true");
					//to.setAttribute(name, (String)null);
				} else
				if (value instanceof Boolean)
					a.setAttribute("boolean", MCast.toString((boolean)value));
				else
				if (value instanceof Integer)
					a.setAttribute("int", MCast.toString((int)value));
				else
				if (value instanceof Long)
					a.setAttribute("long", MCast.toString((long)value));
				else
				if (value instanceof Date)
					a.setAttribute("date", MCast.toString( ((Date)value).getTime() ));
				else
				if (value instanceof String) {
					if (hasValidChars((String)value))
						a.setAttribute("string", (String)value);
					else {
						a.setAttribute("encoding", "base64");
						a.setAttribute("string", Base64.encode( (String)value));
					}
				} else
				if (value.getClass().isEnum()) {
					a.setAttribute("enum", MCast.toString( ((Enum<?>)value).ordinal() ) );
					a.setAttribute("value", ((Enum<?>)value).name());
				}
				else
				if (value instanceof UUID) {
					a.setAttribute("uuid", ((UUID)value).toString() );
				}
				else 
				if (value instanceof Serializable) {
					a.setAttribute("serializable","true");
					
					CDATASection cdata = a.getOwnerDocument().createCDATASection("");
					String data = MCast.toBinaryString( MCast.toBinary(value) );
					cdata.setData(data);
					a.appendChild(cdata);
				} else
				{
					a.setAttribute("type", value.getClass().getCanonicalName());
					pojoToXml(value, a, factory, level+1);
				}
				
			} catch (Throwable t) {
				log.d(MSystem.getClassName(from), attr.getName(), t);
			}

		}
	}
	
	private static boolean hasValidChars(String value) {
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\n' || c == '\r' || c == '\t' || c >= 32 && c <= 55295 ) {
			} else {
				return false;
			}
		}
		return true;
	}

	public static void xmlToPojo(Element from, Object to, MActivator act) throws IOException {
		xmlToPojo(from, to, getDefaultModelFactory(), act);
	}
	
	@SuppressWarnings("unchecked")
	public static void xmlToPojo(Element from, Object to, PojoModelFactory factory, MActivator act) throws IOException {
		PojoModel model = factory.createPojoModel(to.getClass());
		
		HashMap<String, Element> index = new HashMap<>();
		for (Element e : MXml.getLocalElementIterator(from, "attribute"))
			index.put(e.getAttribute("name"), e);
		
		for (PojoAttribute<Object> attr : model) {
			
			try {
				if (!attr.canWrite()) continue;
	
				String name = attr.getName();
	//			Class<?> type = attr.getType();
				Element a = index.get(name);
				if (a == null) {
					log.d("attribute not found",name,to.getClass());
					continue;
				}
				{
					String value = a.getAttribute("null");
					if (MString.isSet(value) && value.equals("true")) {
						attr.set(to, null);
						continue;
					}
				}
				if (a.hasAttribute("string")) {
					String data = a.getAttribute("encoding");
					if ("base64".equals(data)) {
						String value = new String( Base64.decode(a.getAttribute("string")) );
						attr.set(to, value);
					} else {
						String value = a.getAttribute("string");
						attr.set(to, value);
					}
					continue;
				}
				if (a.hasAttribute("boolean")) {
					String value = a.getAttribute("boolean");
					attr.set(to, MCast.toboolean(value, false));
					continue;
				}
				if (a.hasAttribute("int")) {
					String value = a.getAttribute("int");
					attr.set(to, MCast.toint(value,0));
					continue;
				}
				if (a.hasAttribute("long")) {
					String value = a.getAttribute("long");
					attr.set(to, MCast.tolong(value,0));
					continue;
				}
				if (a.hasAttribute("date")) {
					String value = a.getAttribute("date");
					Date obj = new Date();
					obj.setTime( MCast.tolong(value,0) );
					attr.set(to, obj);
					continue;
				}
				if (a.hasAttribute("uuid")) {
					String value = a.getAttribute("uuid");
					try {
						attr.set(to, UUID.fromString(value));
					} catch (Throwable t) {
						log.d(name,t);
					}
					continue;
				}
				if (a.hasAttribute("enum")) {
					String value = a.getAttribute("enum");
					attr.set(to, MCast.toint(value, 0));
					continue;
				}
				if ("true".equals(a.getAttribute("serializable"))) {
					CDATASection cdata = MXml.findCDataSection(a);
					if (cdata != null) {
						String data = cdata.getData();
						try {
							Object obj = MCast.fromBinary( MCast.fromBinaryString(data) );
							attr.set(to, obj);
						} catch (ClassNotFoundException e1) {
							throw new IOException(e1);
						}
					}
				}
				if (a.hasAttribute("type")) {
					String value = a.getAttribute("type");
					try {
						Object obj = act.createObject(value);
						xmlToPojo(a,obj,factory,act);
						attr.set(to, obj);
					} catch (Exception e1) {
						log.d(name,to.getClass(),e1);
					}
					continue;
				}

			} catch (Throwable t) {
				log.d(MSystem.getClassName(to), attr.getName(), t);
			}

		}
	}
	
	/**
	 * Functionize a String. Remove bad names and set first characters to upper. Return def if the name
	 * can't be created, e.g. only numbers.
	 * 
	 * @param in
	 * @param firstUpper 
	 * @param def
	 * @return
	 */
	public static String toFunctionName(String in, boolean firstUpper,String def) {
		if (MString.isEmpty(in)) return def;
		boolean first = firstUpper;
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (c >= 'a' && c <= 'z' || c >='A' && c <='Z' || c == '_') {
				if (first)
					c = Character.toUpperCase(c);
				first = false;
				out.append(c);
			} else
			if (!first && c >='0' && c <= '9') {
				out.append(c);
			} else {
				first = true;
			}
		}
		
		if (out.length() == 0) return def;
		return out.toString();
	}
	
	public static IProperties pojoToProperties(Object from) throws IOException {
		return pojoToProperties(from, getDefaultModelFactory());
	}
	
	public static IProperties pojoToProperties(Object from, PojoModelFactory factory) throws IOException {
		MProperties out = new MProperties();
		PojoModel model = factory.createPojoModel(from.getClass());

		for (PojoAttribute<?> attr : model) {
			
			try {
				if (!attr.canRead()) continue;
				
				Object value = attr.get(from);
				
				String name = attr.getName();
				Class<?> type = attr.getType();
				if (type == int.class) out.setInt(name, (int)value);
				else
				if (type == Integer.class) out.setInt(name, (Integer)value);
				else
				if (type == long.class)  out.setLong(name, (long)value);
				else
				if (type == Long.class)  out.setLong(name, (Long)value);
				else
				if (type == float.class)  out.setFloat(name, (float)value);
				else
				if (type == Float.class)  out.setFloat(name, (Float)value);
				else
				if (type == double.class)  out.setDouble(name, (double)value);
				else
				if (type == Double.class)  out.setDouble(name, (Double)value);
				else
				if (type == boolean.class)  out.setBoolean(name, (boolean)value);
				else
				if (type == Boolean.class)  out.setBoolean(name, (Boolean)value);
				else
				if (type == String.class)  out.setString(name, (String)value);
				else
				if (type == Date.class)  out.setDate(name, (Date)value);
				else
					out.setString(name, String.valueOf(value));
				
			} catch (Throwable t) {
				log.d(MSystem.getClassName(from), attr.getName(), t);
			}
		}
		return out;
	}

	
	public static void propertiesToPojo(IProperties from, Object to) throws IOException {
		propertiesToPojo(from, to, getDefaultModelFactory(), null);
	}
	
	public static void propertiesToPojo(IProperties from, Object to, PojoModelFactory factory) throws IOException {
		propertiesToPojo(from, to, factory, null);
	}
	
	@SuppressWarnings("unchecked")
	public static void propertiesToPojo(IProperties from, Object to, PojoModelFactory factory, Caster<Object,Object> unknownHadler) throws IOException {
		PojoModel model = factory.createPojoModel(to.getClass());
		for (PojoAttribute<Object> attr : model) {
			
			if (!attr.canWrite()) continue;
			
			String name = attr.getName();
			Class<?> type = attr.getType();
			try {
				if (!from.isProperty(name) || !attr.canWrite() ) {
					
				} else
				if (type == Boolean.class || type == boolean.class)
					attr.set(to, from.getBoolean(name, false));
				else
				if (type == Integer.class || type == int.class)
					attr.set(to, from.getInt(name, 0));
				else
				if (type == String.class)
					attr.set(to, from.getString(name, null));
				else
				if (type == UUID.class)
					try {
						attr.set(to, UUID.fromString(from.getString(name)));
					} catch (IllegalArgumentException e) {
						attr.set(to, null);
					}
				else
				if (type.isEnum()) {
					Object[] cons=type.getEnumConstants();
					int ord = from.getInt(name, 0);
					Object c = cons.length > 0 ? cons[0] : null;
					if (ord >=0 && ord < cons.length) c = cons[ord];
					attr.set(to, c );
				}
				else
					attr.set(to, unknownHadler == null ? from.getString(name) : unknownHadler.cast(from.get(name), null) );
			} catch (Throwable t) {
				log.d(MSystem.getClassName(to), name, t);
			}
		}
	}

}
