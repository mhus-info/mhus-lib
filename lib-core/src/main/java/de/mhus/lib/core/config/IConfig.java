package de.mhus.lib.core.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.errors.TooDeepStructuresException;

/**
 * A IConfig extends the concept of properties to a object oriented structure. A property can also
 * be an object or array of objects. The IConfig will not really separate objects and arrays. If you
 * require an array and it's only a single objects you will get a list with a single object and vies
 * versa.
 *
 * @author mikehummel
 */
public interface IConfig extends IProperties {

    public static final String NAMELESS_VALUE = "";

    /**
     * Returns true if the key is an object.
     *
     * @param key
     * @return If the property is an object or array
     */
    boolean isObject(String key);

    IConfig getObjectOrNull(String key);

    IConfig getObject(String key) throws NotFoundException;

    boolean isArray(String key);

    ConfigList getArray(String key) throws NotFoundException;

    IConfig getObjectByPath(String path);

    String getExtracted(String key, String def);

    String getExtracted(String key);

    List<IConfig> getObjects();

    void setObject(String key, IConfig object);

    void addObject(String key, IConfig object);

    IConfig createObject(String key);

    List<String> getPropertyKeys();

    String getName();

    IConfig getParent();

    List<String> getObjectKeys();

    /**
     * Return in every case a list. An Array List or list with a single Object or a object with
     * nameless value or an empty list.
     *
     * @param key
     * @return A list
     */
    ConfigList getList(String key);

    /**
     * Return a iterator over a array or a single object. Return an empty iterator if not found. Use
     * this function to iterate over arrays or objects.
     *
     * @param key
     * @return Never null.
     */
    List<IConfig> getObjectList(String key);

    List<String> getObjectAndArrayKeys();

    List<String> getArrayKeys();

    ConfigList getArrayOrNull(String key);

    ConfigList getArrayOrCreate(String key);

    ConfigList createArray(String key);

    //    IConfig cloneObject(IConfig node);

    /**
     * Return a config or null if the string is not understand.
     *
     * @param configString
     * @return A config object if the config is found or null. If no config is recognized it returns
     *     null
     * @throws MException
     */
    static IConfig readConfigFromString(String configString) throws MException {
        if (MString.isEmptyTrim(configString)) return new MConfig();
        if (configString.startsWith("[") || configString.startsWith("{")) {
            try {
                return readFromJsonString(configString);
            } catch (Exception e) {
                throw new MException(configString, e);
            }
        }
        if (configString.startsWith("<?")) {
            try {
                return readFromXmlString(MXml.loadXml(configString).getDocumentElement());
            } catch (Exception e) {
                throw new MException(configString, e);
            }
        }

        if (configString.contains("=")) {
            if (configString.contains("&"))
                return readFromProperties(new HashMap<>(MUri.explode(configString)));
            else return readFromProperties(IProperties.explodeToMProperties(configString));
        }

        return null;
    }

    /**
     * Return a config or null if the string is not understand.
     *
     * @param configStrings
     * @return IConfig, never null
     * @throws MException
     */
    static IConfig readConfigFromString(String[] configStrings) throws MException {
        if (configStrings == null || configStrings.length == 0) return new MConfig();
        if (configStrings.length == 1) return readConfigFromString(configStrings[0]);
        return readFromProperties(IProperties.explodeToMProperties(configStrings));
    }

    static IConfig readFromProperties(Map<String, Object> lines) {
        return new PropertiesConfigBuilder().readFromMap(lines);
    }

    static IConfig readFromJsonString(String json) throws MException {
        return new JsonConfigBuilder().readFromString(json);
    }

    static IConfig readFromXmlString(Element documentElement) throws MException {
        return new XmlConfigBuilder().readFromElement(documentElement);
    }

    static IConfig readFromYamlString(String yaml) throws MException {
        return new YamlConfigBuilder().readFromString(yaml);
    }

    static String toCompactJsonString(IConfig config) throws MException {
        try {
            MJson.toString(new JsonConfigBuilder().writeToJsonNode(config));
        } catch (IOException e) {
            throw new MException(e);
        }
        return null;
    }

    static String toPrettyJsonString(IConfig config) throws MException {
        try {
            MJson.toPrettyString(new JsonConfigBuilder().writeToJsonNode(config));
        } catch (IOException e) {
            throw new MException(e);
        }
        return null;
    }

    public static void merge(IConfig from, IConfig to) throws MException {
        merge(from, to, 0);
    }

    private static void merge(IConfig from, IConfig to, int level) throws MException {
        if (level > 100) throw new TooDeepStructuresException();
        for (IConfig node : from.getObjects()) {
            IConfig n = to.createObject(node.getName());
            for (String name : node.getPropertyKeys()) {
                n.put(name, node.get(name));
            }
            merge(node, (IConfig) n, level + 1);
        }
        for (String key : from.getArrayKeys()) {
            ConfigList toArray = to.createArray(key);
            for (IConfig node : from.getArrayOrNull(key)) {
                IConfig n = toArray.createObject();
                for (String name : node.getPropertyKeys()) {
                    n.put(name, node.get(name));
                }
                merge(node, (IConfig) n, level + 1);
            }
        }
    }

    public static String[] toStringArray(Collection<IConfig> nodes, String key) {
        LinkedList<String> out = new LinkedList<>();
        for (IConfig item : nodes) {
            String value = item.getString(key, null);
            if (value != null) out.add(value);
        }
        return out.toArray(new String[out.size()]);
    }
}
