package de.mhus.lib.core.directory;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.mhus.lib.core.AbstractProperties;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.parser.CompiledString;
import de.mhus.lib.core.parser.DefaultScriptPart;
import de.mhus.lib.core.parser.StringCompiler;
import de.mhus.lib.core.parser.StringPart;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotSupportedException;

/**
 * This interface represent a generic Directory Node. Nodes are
 * sets of definitions. The definitions can be bound together to a inner Nodes.
 * @param <T> 
 * 
 */

public abstract class ResourceNode<T extends ResourceNode<?>> extends AbstractProperties {

	private static final long serialVersionUID = 1L;
	protected ConfigStringCompiler compiler;
	protected HashMap<String, CompiledString> compiledCache;

	/**
	 * Return all existing keys. A property key is unique.
	 *  
	 * @return all keys
	 */
	public abstract Collection<String> getPropertyKeys();

	/**
	 * Returns a inner configuration by the name. Inner configurations
	 * are not unique. If more then one configurations exists it will
	 * return the first one. if no configuration exists it returns null.
	 * 
	 * @param key
	 * @return the node with the name
	 */
	public abstract T getNode(String key);

	/**
	 * Return all inner configurations ignoring the name. The order
	 * is like in the configuration file. This never returns null.
	 * 
	 * @return all nodes
	 */
	public abstract Collection<T> getNodes();

	/**
	 * Return all inner configurations by the given name. The order
	 * is like in the configuration file. This never returns null.
	 * 
	 * @param key
	 * @return all nodes with the name
	 */
	public abstract Collection<T> getNodes(String key);

	/**
	 * Return all possible, existing inner configuration names.
	 * @return keys from nodes
	 */
	public abstract Collection<String> getNodeKeys();

	/**
	 * Return a name of this config element could also be null.
	 * The name most time is the name of a sub config.
	 * @return node of this node
	 * @throws MException 
	 */
	public abstract String getName() throws MException;

	/**
	 * Return the default content input stream.
	 * 
	 * @return input stream
	 */
	public InputStream getInputStream() {
		return getInputStream(null);
	}
	
	/**
	 * Return the input stream of a content resource.
	 * @param rendition Name of a rendition or null for the default content
	 * @return input stream
	 */
	public abstract InputStream getInputStream(String rendition);
	
	/**
	 * Returns a list of available renditions. Will not return the default rendition.
	 * 
	 * @return The list or null if hasContent() is false
	 */
	public abstract Collection<String> getRenditions();
	
	public IProperties getRenditionProperties() {
		return getRenditionProperties(null);
	}
	
	/**
	 * Returns a set of properties to define the rendition.
	 * 
	 * @param rendition
	 * @return properties
	 */
	public abstract IProperties getRenditionProperties(String rendition);
	
	/**
	 * Returns a compiled and executed string. The string is compiled with StringCompiler and is cached. Example
	 * is "this is the value of another $anothername,default$" or with the prefix "root.": "This is a root attribute $root.name$
	 * 
	 * User the "../" prefix to go one back ...
	 * 
	 * @see StringCompiler
	 * @param key
	 * @return extracted values
	 */
	public String getExtracted(String key) {
		return getExtracted(key, null);
	}
	
	public String getExtracted(String key, String def) {
		return getExtracted(key,def,0);
	}
	
	/**
	 * return the parent config if possible.
	 * 
	 * @return parent node
	 */
	public abstract ResourceNode<?> getParent();

	@Override
	public Set<String> keys() {
		return MCollection.toSet(getPropertyKeys());
	}

	protected String getExtracted(String key, String def,int level) {
		
		if (level > 10) return def;
		
		String value = getString(key,null);
		
		if (value == null) return def;
		if (value.indexOf('$') < 0) return value;
		
		synchronized (this) {
			if (compiler == null) {
				compiler = new ConfigStringCompiler();
				compiledCache = new HashMap<String, CompiledString>();
			}
			CompiledString cached = compiledCache.get(key);
			if (cached == null) {
				cached = compiler.compileString(value);
				compiledCache.put(key, cached);
			}
			try {
				return cached.execute(level == 0 ?null : new ConfigMap(level));
			} catch (MException e) {
				throw new MRuntimeException(key,e);
			}
		}
	}
	
	private class ConfigStringCompiler extends StringCompiler {
		
		@Override
		protected StringPart createDefaultAttributePart(String part) {
			if (part.startsWith("root."))				
				return new RootAttributePart(part);
			if (part.startsWith(">>>"))
				return new DefaultScriptPart(part);
			return new ConfigAttributePart(part);
		}
	}

	private class RootAttributePart implements StringPart {
		private String name;
		private String def;
		private ResourceNode<?> root;

		public RootAttributePart(String part) {
			name = MString.afterIndex(part,'.');
			root = ResourceNode.this;
			while (root.getParent() != null && root.getParent() != root) root = root.getParent();
			int pos = name.indexOf(',');
			if (pos > 0) {
				def = name.substring(pos+1);
				name = name.substring(0,pos);
			}
		}

		@Override
		public void execute(StringBuffer out, Map<String, Object> attributes) throws MException {
			out.append(root.getString(name,def));
		}		
		
		@Override
		public void dump(int level, StringBuffer out) {
			MString.appendRepeating(level, ' ', out);
			out.append(getClass().getCanonicalName()).append(name).append("(").append(def).append(")");
		}

	}
	
	private class ConfigAttributePart implements StringPart {

		private String name;
		private String def;
		private ResourceNode<?> config;

		public ConfigAttributePart(String part) {
			name = part;
			int pos = name.indexOf(',');
			if (pos > 0) {
				def = name.substring(pos+1);
				name = name.substring(0,pos);
			}
			config = ResourceNode.this;
			while (name.startsWith("../")) {
				config = config.getParent();
				name = name.substring(3);
				if (config == null ) break;
			}
		}

		@Override
		public void execute(StringBuffer out, Map<String, Object> attributes) throws MException {
			int level = 0;
			if (attributes != null && attributes instanceof ConfigMap) {
				level = ((ConfigMap)attributes).getLevel();
			}
			if (config== null)
				out.append(def);
			else
				out.append(config.getExtracted(name,def,level));
		}
		
		@Override
		public void dump(int level, StringBuffer out) {
			MString.appendRepeating(level, ' ', out);
			out.append(getClass().getCanonicalName()).append(name).append("(").append(def).append(")");
		}

	}
	
	private static class ConfigMap implements Map<String,Object> {

		private int level;

		private ConfigMap(int level) {
			this.level = level;
		}
		
		@Override
		public int size() {
			return 0;
		}

		public int getLevel() {
			return level;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public Object get(Object key) {
			return null;
		}

		@Override
		public Object put(String key, Object value) {
			return null;
		}

		@Override
		public Object remove(Object key) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
		}

		@Override
		public void clear() {
		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public Collection<Object> values() {
			return null;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return null;
		}
		
	}

	public abstract URL getUrl();

	public abstract boolean isValid();
	
	public abstract boolean hasContent();

	@SuppressWarnings("unchecked")
	public T getNodeByPath(String path) {
		if (path == null) return null;
		while (path.startsWith("/")) path = path.substring(1);
		if (path.length() == 0) return (T) this;
		int p = path.indexOf('/');
		if (p < 0) return getNode(path);
		T next = getNode(path.substring(0, p));
		if (next == null) return null;
		return (T) next.getNodeByPath(path.substring(p+1));
	}
	
	public String dump() throws MException {
		StringBuffer sb = new StringBuffer();
		dump(sb,0);
		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	void dump(StringBuffer sb, int level) throws MException {
		sb.append(MString.getRepeatig(level, ' '));
		sb.append('<').append(getName());
		for (String key : keys())
			sb.append('\n').append(MString.getRepeatig(level+1, ' ')).append(key).append("='").append(getString(key)).append("'");
		sb.append(">\n");
		for (T node : getNodes())
			node.dump(sb, level+1);
		sb.append(MString.getRepeatig(level, ' '));
		sb.append("</").append(getName()).append(">\n");
		
	}
	
	@Override
	public int size() {
		Collection<String> keys = getPropertyKeys();
		if (keys == null) return 0;
		return keys.size();
	}
	
	@Override
	public boolean containsValue(Object value) {
		throw new NotSupportedException();
	}

	@Override
	public Collection<Object> values() {
		HashMap<String, Object> out = new HashMap<>();
		for (String key : getPropertyKeys())
			out.put(key, getProperty(key) );
		return out.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		HashMap<String, Object> out = new HashMap<>();
		for (String key : getPropertyKeys())
			out.put(key, getProperty(key) );
		return out.entrySet();
	}

}
