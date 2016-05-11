/*
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MXml {

	//private static Log log = Log.getLog(MXml.class);
	
	/**
	 * Returns the text value of a node.
	 * 
	 * @param root The element where you need the text value from
	 * @param inner If true all inner elements are parsed and the text appended
	 * @return null if root is null or the text of the element
	 */
	public static String getValue(Element root, boolean inner) {

		if (root == null) return null;
		
		NodeList raw = root.getChildNodes();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < raw.getLength(); i++) {
			if (raw.item(i).getNodeType() == Node.ELEMENT_NODE && inner)
				sb.append(getValue((Element) raw.item(i), inner));
			else if (raw.item(i).getNodeType() == Node.TEXT_NODE)
				sb.append(raw.item(i).getNodeValue());
			else if (raw.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
				sb.append(raw.item(i).getNodeValue());
		}

		return sb.toString();

	}

	/**
	 * Returns the text value of a node. The listener manipulates the output for different inner notes.
	 * 
	 * @param root
	 * @param listener
	 * @return null if the root is null or the text
	 */
	public static String getValue(Element root, ValueListener listener) {

		if (root == null) return null;

		NodeList raw = root.getChildNodes();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < raw.getLength(); i++) {
			if (raw.item(i).getNodeType() == Node.ELEMENT_NODE) {
				sb.append(listener.valueOf( (Element)raw.item(i), getValue((Element) raw.item(i), listener ) ) );
			} else if (raw.item(i).getNodeType() == Node.TEXT_NODE)
				sb.append(raw.item(i).getNodeValue());
		}

		return sb.toString();

	}
	
	public static interface ValueListener {

		public String valueOf(Element item, String value);
		
	}
	
	/**
	 * Returns a list of elements with this name in the given root element on the first level only.
	 * 
	 * @param root
	 * @param name
	 * @return null if the root or name is null else the list of nodes
	 */
	public static NodeList getLocalElements(Element root, String name) {
		
		if (root == null || name == null) return null;
		
		NodeList raw = root.getChildNodes();

		MyNodeList out = new MyNodeList();
		for (int i = 0; i < raw.getLength(); i++)
			if (raw.item(i).getNodeType() == Node.ELEMENT_NODE
					&& (name.equals("*") || name.equals(raw.item(i)
							.getNodeName())))
				out.add(raw.item(i));

		return out;
	}

	/**
	 * Returns a list of all elements in the given root element on the first level only.
	 * 
	 * @param root
	 * @return null if the root is null otherwise the list of nodes
	 */
	public static NodeList getLocalElements(Element root) {
		
		if (root == null) return null;
		
		NodeList raw = root.getChildNodes();

		MyNodeList out = new MyNodeList();
		for (int i = 0; i < raw.getLength(); i++)
			if (raw.item(i).getNodeType() == Node.ELEMENT_NODE)
				out.add(raw.item(i));

		return out;
	}

	/**
	 * Returns an iterator of elements with this name in the given root element on the first level only.
	 * 
	 * @param root
	 * @param name
	 * @return every time an iterator - never null
	 */
	public static ElementIterator getLocalElementIterator(Element root, String name) {
		return new ElementIterator(getLocalElements(root,name));
	}
	
	/**
	 * Returns an iterator of all elements in the given root element on the first level only.
	 * 
	 * @param root
	 * @return
	 */
	public static ElementIterator getLocalElementIterator(Element root) {
		return new ElementIterator(getLocalElements(root));
	}

	/**
	 * Returns the first found element by path. Available search definitions are slash and 
	 * bracked, e.g. "/root/somenode/number[3]/name"
  	 *
	 * @param root
	 * @param path
	 * @return null if root or path is null or the element is not found
	 */
	public static Element getElementByPath(Element root, String path) {

		if (root == null || path == null) return null;
		
		if (path.startsWith("/")) {
			while (root.getParentNode() != null && root.getParentNode() instanceof Element)
				root = (Element) root.getParentNode();
			path = path.substring(1);
		}

		String[] parts = path.split("\\/");

		for (int i = 0; i < parts.length; i++) {

			String part = parts[i].trim();
			int index = 0;
			String key = null;
			String value = null;

			int pos = part.indexOf('@');
			if (pos >= 0) {
				String kv = part.substring(pos+1);
				key = MString.beforeIndex(kv, '=');
				value = MString.afterIndex(kv, '=');
				part = part.substring(0, pos);
			}
			
			pos = part.indexOf('[');
			if (pos >= 0) {

				int pos2 = part.indexOf(']', pos);

				try {
					index = Integer.parseInt(part.substring(pos + 1, pos2));
				} catch (NumberFormatException nfe) {
					return null;
				}
				part = part.substring(0, pos);

			}

			NodeList list = getLocalElements(root, part);
			
			if (key != null) {
				root = null;
				for (int j = 0; j < list.getLength(); j++) {
					Node e = list.item(j);
					if (e instanceof Element) {
						if ( value.equals( ((Element)e).getAttribute(key) ) ) {
							root = (Element) e;
							break;
						}
					}
				}
				if (root == null) return null;
			} else {
				if (list.getLength() <= index)
					return null;
				root = (Element) list.item(index);
			}
		}

		return root;
	}

	@SuppressWarnings("serial")
	private static class MyNodeList extends Vector<Node> implements NodeList {

		@Override
		public int getLength() {
			return size();
		}

		@Override
		public Node item(int index) {
			return (Node) elementAt(index);
		}

	}

	/**
	 * Returns the path to the node as string representation, separated with slashes.
	 * 
	 * @param in
	 * @return an empty string if the node is null
	 */
	public static String getPathAsString(Node in) {
		if (in == null)
			return "";
		StringBuffer out = new StringBuffer();
		do {
			if (in instanceof Document) {
				// out.insert( 0, in.getBaseURI() );
			} else {
				out.insert(0, in.getNodeName());
				out.insert(0, '/');
			}
			in = in.getParentNode();
		} while (in != null);

		return out.toString();
	}

	/**
	 * Create a XML Document from a string. Using the defined charset.
	 * 
	 * @param xml
	 * @param charset
	 * @return
	 * @throws ParserConfigurationException
	 * @throws UnsupportedEncodingException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadXml(String xml, String charset) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
		if (xml == null) return null;
		if (!xml.startsWith("<?xml")) xml = "<?xml version=\"1.0\" encoding=\"" + charset + "\"?>" + xml;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
	}
	
	/**
	 * Create a XML Document from a string.
	 * 
	 * @param xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadXml(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		return loadXml(xml, MString.CHARSET_UTF_8);
	}

	/**
	 * Create a XML Document from a stream resource.
	 * 
	 * @param is
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadXml(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		return builder.parse(is);
	}

	public static Document loadXml(Reader file)
		throws ParserConfigurationException, SAXException, IOException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			return builder.parse(new InputSource(file));
	}

	/**
	 * Write the element into the stream.
	 * 
	 * @param e
	 * @param out
	 * @throws Exception
	 */
	public static void saveXml(Node e, OutputStream out) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(out);
		DOMSource source = new DOMSource(e);
		transformer.transform(source, result);
		
	}
	
	public static void saveXml(Node e, File out) throws Exception {
		
		FileOutputStream fo = new FileOutputStream(out);
		saveXml(e, fo);
		fo.close();
		
	}
	
	public static void saveXml(Node e, Writer out,boolean intend) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, intend ? "yes" : "no");

		StreamResult result = new StreamResult(out);
		DOMSource source = new DOMSource(e);
		transformer.transform(source, result);
		
	}
	
	public static String toString(Node e, boolean intend) throws Exception {
		StringWriter sw = new StringWriter();
		saveXml(e,sw,intend);
		return sw.toString();
	}
	
	/**
	 * Create and return a empty xml document.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Document createDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		return builder.newDocument();
	}

	/**
	 * Encode the default problematic characters in a string to store it in a xml value.
	 * 
	 * @param _in
	 * @return
	 */
	public static String encode(String _in) {

		if (_in == null) return null;

		StringBuffer out = new StringBuffer();
		for (int i = 0; i < _in.length(); i++) {
			char c = _in.charAt(i);
			if (c > 126 || c < 32) {
				out.append("&#" + (int) c + ";");
			} else if (c == '&') {
				out.append("&amp;");
			} else if (c == '"') {
				out.append("&quot;");
			} else if (c == '<') {
				out.append("&lt;");
			} else if (c == '>') {
				out.append("&gt;");
			} else
				out.append(c);

		}
		return out.toString();
	}

	/**
	 * Encodes the amp and all characters greater then 255 to unicode representation with amp and hash signs.
	 * 
	 * @param _in
	 * @return
	 */
	public static String unicodeEncode(String _in) {

		StringBuffer out = new StringBuffer();
		for (int i = 0; i < _in.length(); i++) {
			char c = _in.charAt(i);
			if (c > 255) {
				out.append("&#" + (int) c + ";");
			} else if (c == '&') {
				out.append("&amp;");
			} else
				out.append(c);

		}
		return out.toString();
	}

	/**
	 * Decodes a string with encoded characters to a java string.
	 * @param _in 
	 * @return 
	 * 
	 */
	public static String decode(String _in) {

		if (_in == null) return null;
		
		StringBuffer out = new StringBuffer();
		int mode = 0;
		StringBuffer joker = null;
		int jLen = 0;
		for (int i = 0; i < _in.length(); i++) {
			char c = _in.charAt(i);
			if (mode == 0) {
				if (c == '&') {
					mode = 1;
					jLen = 0;
					joker = new StringBuffer();
				} else {
					out.append(c);
				}
			} else {
				jLen++;
				if (c == ';') {
					String j = joker.toString();
					if (j.length() > 0) {
						if (j.startsWith("#")) {
							out.append((char) Integer.parseInt(j.substring(1)));
						} else if (j.equals("quot")) {
							out.append('"');
						} else if (j.equals("lt")) {
							out.append('<');
						} else if (j.equals("gt")) {
							out.append('>');
						} else if (j.equals("amp")) {
							out.append('&');
						} else if (j.equals("auml")) {
							out.append('\u00e4');
						} else if (j.equals("Auml")) {
							out.append('\u00c4');
						} else if (j.equals("ouml")) {
							out.append('\u00f6');
						} else if (j.equals("Ouml")) {
							out.append('\u00d6');
						} else if (j.equals("uuml")) {
							out.append('\u00fc');
						} else if (j.equals("Uuml")) {
							out.append('\u00dc');
						} else if (j.equals("szlig")) {
							out.append('\u00df');
						} else if (j.equals("nbsp")) {
							out.append(' ');
						} else {
							out.append("&" + j + ";");
						}

					} else {
						out.append("&;");
					}
					mode = 0;
				} else {
					joker.append(c);
					if (jLen > 8) { // not ok
						out.append("&").append(joker);
						mode = 0;
					}
				}
			}
		}
		return out.toString();
	}
	
	/**
	 *  Prints information - most technical - of the xml element and its childs and returns it as string.
	 *  Use this function for debugging (debugger).
	 *  
	 * @param element
	 * @return
	 */
	public static String dump(Node element) {
		if (element == null) return "null";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(out);
		dump(o, element, "");
		return out.toString();
	}

	/**
	 * Prints information - most technical - of the xml element and its childs to the stream.
	 * 
	 * @param o
	 * @param element
	 */
	public static void dump(PrintStream o, Node element) {
		dump(o, element, "");
	}

	public static void dump(PrintStream o, Node node, String level) {
		if (node instanceof Document) {
			dump(o, ((Document)node).getDocumentElement(), level);
		} else
		if (node instanceof Element) {
			Element element = (Element) node;
			o.print(level + "<" + element.getNodeName());
			NamedNodeMap attrs = element.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++)
				o.print(" " + attrs.item(i).getNodeName() + "='"
						+ attrs.item(i).getNodeValue() + "'");
			o.println("> {" + node.getClass().getCanonicalName() + "}");

			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++)
				dump(o, nodes.item(i), level + "  ");

			o.println(level + "</" + element.getNodeName() + ">");
		} else if (node instanceof ProcessingInstruction) {
			o.println(level + "<?" + node.getNodeName() + " "
					+ ((ProcessingInstruction) node).getData() + "?> {"
					+ node.getClass().getCanonicalName() + "}");
		} else if (node instanceof Comment) {
			o.println(level + "<!--" + ((Comment) node).getData() + "--> {"
					+ node.getClass().getCanonicalName() + "}");
		} else if (node instanceof Text) {
			o.println(level + "'" + ((Text) node).getData() + "' {"
					+ node.getClass().getCanonicalName() + "}");
		} else if (node instanceof DocumentType) {
			o.println(level + "<!DOCTYPE " + ((DocumentType) node).getName()
					+ " " + ((DocumentType) node).getInternalSubset() + " \""
					+ ((DocumentType) node).getPublicId() + "\"> {"
					+ node.getClass().getCanonicalName() + "}");
		} else {
			o.println(level + "{" + node.getClass().getCanonicalName() + "}");
		}

	}

	public static void dump(PrintStream out, NodeList nodes) {

		for (int i = 0; i < nodes.getLength(); i++)
			dump(out, nodes.item(i), "");
	}

	
	public static class ElementIterator implements Iterator<Element>,Iterable<Element>{
		private NodeList list;
		private int next;

		public ElementIterator(NodeList list) {
			this.list = list;
			next = 0;
		}

		@Override
		public boolean hasNext() {
			return list != null && list.getLength() > next;
		}

		@Override
		public Element next() {
			Element ret = (Element)list.item(next);
			next++;
			return ret;
		}

		@Override
		public void remove() {
			// not supported
		}

		@Override
		public Iterator<Element> iterator() {
			return this;
		}
	}

	public static class NodeIterator implements Iterator<Node>,Iterable<Node>{
		private NodeList list;
		private int next;

		public NodeIterator(NodeList list) {
			this.list = list;
			next = 0;
		}

		@Override
		public boolean hasNext() {
			return list != null && list.getLength() > next;
		}

		@Override
		public Node next() {
			Node ret = (Node)list.item(next);
			next++;
			return ret;
		}

		@Override
		public void remove() {
			// not supported
		}

		@Override
		public Iterator<Node> iterator() {
			return this;
		}
	}
	
	/**
	 * Returns the inner XML Structure as string with all tag definitions.
	 * 
	 * @param node
	 * @return
	 */
	public static String innerXml(org.w3c.dom.Node node) {
		return innerXml(node, true);
	}
	
	/**
	 * Returns the inner XML Structure as string with all tag definitions.
	 * 
	 * @param node
	 * @param instructions set to false to ignore processing instructions (on the first level)
	 * @return null if the node is null otherwise the innerXml as text
	 */
	public static String innerXml(org.w3c.dom.Node node, boolean instructions) {
		if (node == null) return null;
		org.w3c.dom.Node childChild = null;
		try {
		    DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
		    LSSerializer lsSerializer = lsImpl.createLSSerializer();
		    NodeList childNodes = node.getChildNodes();
		    StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < childNodes.getLength(); i++) {
		    	childChild = childNodes.item(i);
		    	if (childChild instanceof Text) {
		    		sb.append(childChild.getNodeValue());
		    	} else
		    	if (childChild instanceof ProcessingInstruction) {
		    		if (instructions) sb.append("<?").append(childChild.getNodeName()).append(" ").append( childChild.getNodeValue()).append("?>");
		    	} else {
		    		String ser = lsSerializer.writeToString(childChild);
				    if (ser.substring(0, 2).equals("<?")) {
				    	// remove <?xml ... ?> header
				    	int pos = ser.indexOf("?>\n");
				    	if (pos > 0) ser = ser.substring(pos+3);
				    }
		    		sb.append(ser);
		    	}
		    }
		    return sb.toString();
		} catch (Throwable t) {
		   //log.t(dump(childChild),t);
		}
		return "";
	}
	
	/**
	 * Execute an XPATH query for a list of nodes.
	 * 
	 * @param root
	 * @param query
	 * @return never null
	 * @throws XPathExpressionException
	 */
	public static NodeIterator queryXPath(Node root, String query) throws XPathExpressionException {
		if (root == null || query == null) return new NodeIterator(null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(query);
		NodeList result = (NodeList)expr.evaluate(root, XPathConstants.NODESET);
		return new NodeIterator(result);
	}

	/**
	 * Execute an XPATH query for a list of elements.
	 * 
	 * @param root
	 * @param query
	 * @return never null
	 * @throws XPathExpressionException 
	 */
	public static ElementIterator queryXPathElements(Node root, String query) throws XPathExpressionException {
		if (root == null || query == null) return new ElementIterator(null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(query);
		NodeList result = (NodeList)expr.evaluate(root, XPathConstants.NODESET);
		return new ElementIterator(result);
	}
	
	/**
	 * Remove white spaces in the text nodes.
	 * 
	 * @param element
	 */
	public static void trim(Element element) {
		if (element == null) return;
		
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element)
				trim((Element)node);
			else
			if (node instanceof Text) {
				String value = ((Text)node).getNodeValue();
				if (value != null) {
//					boolean hasEnter = value.indexOf('\n') > -1;
					value = value.trim();
//					if (hasEnter && value.indexOf('\n') < 0)
//						value = value + "\n";
					node.setNodeValue(value);
				}
			}
		}
	}

	/**
	 * Remove the element from his parent but append the children
	 * instead if it into the parent node - on the same position.
	 * @param element
	 */
	public static void carveOut(Element element) {
		
		if (element == null) return;
		
		Element parent = (Element) element.getParentNode();
		
		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			element.removeChild(child);
			parent.insertBefore(child, element);
		}
		
		parent.removeChild(element);
		
	}

	/**
	 * Normalize the name of attributes or node names. Allowed characters are (a-z, A-Z, 0-9, - _ .) all other
	 * characters are replaced with _
	 * 
	 * @param key
	 * @return normalized key or null if the key is null.
	 */
	public static String normalizeName(String key) {
		boolean doIt = false;
		if (key == null) return null;
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '.' || c == '-')) {
				doIt = true;
				break;
			}
		}

		if (doIt) {
			StringBuffer out = new StringBuffer();
			for (int i = 0; i < key.length(); i++) {
				char c = key.charAt(i);
				if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '.' || c == '-')) {
					c = '_';
				}
				out.append(c);
			}
			key = out.toString();
		}
		return encode(key);
	}
	
	public static String removeTags(String txt) {
		txt = txt.replaceAll("<([\\s\\S]*?)>", "");
		return txt;
	}
	
	public static String removeHtmlTags(String txt) {
		txt = txt.replaceAll("<script([\\s\\S]*?)</script>", "");
		txt = txt.replaceAll("<SCRIPT([\\s\\S]*?)</SCRIPT>", "");
		txt = txt.replaceAll("<!--([\\s\\S]*?)-->", "");
		txt = removeTags(txt);
		
		while (txt.indexOf("\n\n\n") >= 0)
			txt = txt.replaceAll("\n\n\n", "\n\n");

		txt = txt.trim();
		
		return txt;
		
	}
	
	public static String getValue(Element root, String path, String def) {
		Element ele = getElementByPath(root, path);
		if (ele == null) return def;
		return getValue(ele, false);
	}

	public static CDATASection findCDataSection(Element a) {
		NodeList list = a.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child instanceof CDATASection) return (CDATASection)child;
		}
		return null;
	}
	
}
