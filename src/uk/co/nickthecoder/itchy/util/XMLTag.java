/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLTag
{
    private final Node _node;

    public static XMLTag openDocument( Reader reader ) throws XMLException, IOException
    {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document document = builder.parse(new InputSource(reader));
            return new XMLTag(document);

        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new XMLException(e);
        } catch (org.xml.sax.SAXException e) {
            throw new XMLException(e);
        }

    }

    public XMLTag( Node node )
    {
        this._node = node;
    }

    public String getName()
    {
        return this._node.getNodeName();
    }

    /**
     * Returns the string value of a content-only tag, ie a tag which has a text body, no attributes
     * and no sub-tags. The html title tag is an example of a content-only tag.
     */
    public String getOptionalContent( String tagName, String defaultValue ) throws XMLException
    {
        if (!this.hasTag(tagName)) {
            return defaultValue;
        } else {
            return this.getContent(tagName);
        }
    }

    public String getContent( String tagName ) throws XMLException
    {
        XMLTag childTag = this.getSingleSubTag(tagName);

        return childTag.getBody();
    }

    public String getBody() throws XMLException
    {
        NodeList nodes = this._node.getChildNodes();

        if (nodes.getLength() > 1) {
            throw new XMLException("Expected just a body");
        }

        if (nodes.getLength() == 0) {
            return "";
        }

        Node child = nodes.item(0);
        if (child.getNodeType() != Node.TEXT_NODE) {
            throw new XMLException("Expected a text node");
        }

        return child.getNodeValue();
    }

    public XMLTag getTag( String tagName ) throws XMLException
    {
        return this.getSingleSubTag(tagName);
    }

    public Iterator<XMLTag> getTags()
    {
        List<XMLTag> tagList = this.getChildren();

        return tagList.iterator();
    }

    public Iterator<XMLTag> getTags( String tagName ) throws XMLException
    {
        List<XMLTag> tagList = this.getChildrenByTagName(tagName);

        return tagList.iterator();
    }

    public Iterator<XMLTag> getTags( String tagName, int minimum ) throws XMLException
    {
        List<XMLTag> tagList = this.getChildrenByTagName(tagName);

        if (tagList.size() < minimum) {
            throw new XMLException("Expeceted at least " + minimum + " tags, but found only " +
                    tagList.size());
        }

        return tagList.iterator();
    }

    public Iterator<XMLTag> getTags( String tagName, int minimum, int maximum ) throws XMLException
    {
        List<XMLTag> tagList = this.getChildrenByTagName(tagName);

        if (tagList.size() < minimum) {
            throw new XMLException("Expeceted at least " + minimum + " tags, but found only " +
                    tagList.size());
        }

        if (tagList.size() > maximum) {
            throw new XMLException("Expeceted at most " + maximum + " tags, but found " +
                    tagList.size());
        }

        return tagList.iterator();
    }

    private List<XMLTag> getChildren()
    {
        ArrayList<XMLTag> result = new ArrayList<XMLTag>();

        NodeList nodeList = this._node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);

            if (!"#text".equals(child.getNodeName())) {
                result.add(new XMLTag(child));
            }
        }

        return result;
    }

    /**
     * Returns a List of XMLTags which are child tags of this tag, with the given name.
     */
    private List<XMLTag> getChildrenByTagName( String tagName )
    {
        ArrayList<XMLTag> result = new ArrayList<XMLTag>();

        NodeList nodeList = this._node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);

            if (tagName.equals(child.getNodeName())) {
                result.add(new XMLTag(child));
            }
        }

        return result;
    }

    public boolean hasTag( String tagName )
    {
        List<XMLTag> tagList = this.getChildrenByTagName(tagName);
        return (tagList.size() > 0);
    }

    public boolean hasAttribute( String attributeName )
    {
        try {
            return (this._node.getAttributes().getNamedItem(attributeName) != null);
        } catch (Exception e) {
            return false;
        }
    }

    public String getAttribute( String attributeName ) throws XMLException
    {
        try {
            Node attNode = this._node.getAttributes().getNamedItem(attributeName);
            return attNode.getNodeValue();
        } catch (Exception e) {
            throw new XMLException("Expected attribute '" + attributeName + "'");
        }
    }

    public String getOptionalAttribute( String attributeName, String defaultValue )
        throws XMLException
    {
        if (this.hasAttribute(attributeName)) {
            return this.getAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    public boolean getBooleanAttribute( String attributeName ) throws XMLException
    {
        String value = this.getAttribute(attributeName);

        if ("true".equals(value)) {
            return true;

        } else if ("false".equals(value)) {
            return false;

        } else {
            throw new XMLException("Expected a boolean attribute named '" + attributeName +
                    "', but found '" + value + "'");
        }

    }

    public boolean getOptionalBooleanAttribute( String attributeName, boolean defaultValue )
        throws XMLException
    {
        if (this.hasAttribute(attributeName)) {
            return this.getBooleanAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    public int getIntAttribute( String attributeName ) throws XMLException
    {
        String value = this.getAttribute(attributeName);

        try {
            return Integer.parseInt(value);

        } catch (Exception e) {
            throw new XMLException("Expected an integer attribute named '" + attributeName +
                    "', but found '" + value + "'");
        }
    }

    public int getOptionalIntAttribute( String attributeName, int defaultValue )
        throws XMLException
    {
        if (this.hasAttribute(attributeName)) {
            return this.getIntAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    public float getFloatAttribute( String attributeName ) throws XMLException
    {
        String value = this.getAttribute(attributeName);

        try {
            return Float.parseFloat(value);

        } catch (Exception e) {
            throw new XMLException("Expected a float attribute named '" + attributeName +
                    "', but found '" + value + "'");
        }
    }

    public float getOptionalFloatAttribute( String attributeName, float defaultValue )
        throws XMLException
    {
        if (this.hasAttribute(attributeName)) {
            return this.getFloatAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    public double getDoubleAttribute( String attributeName ) throws XMLException
    {
        String value = this.getAttribute(attributeName);

        try {
            return Double.parseDouble(value);

        } catch (Exception e) {
            throw new XMLException("Expected a double attribute named '" + attributeName +
                    "', but found '" + value + "'");
        }
    }

    public double getOptionalDoubleAttribute( String attributeName, double defaultValue )
        throws XMLException
    {
        if (this.hasAttribute(attributeName)) {
            return this.getDoubleAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    /**
     * Throws an exception if any attribute have not been read, or if any sub-tags have not been
     * read. Currently not implemented, so does nothing.
     */
    public void assertAllUsed()
    {
    }

    private XMLTag getSingleSubTag( String tagName ) throws XMLException
    {
        List<XMLTag> tagList = this.getChildrenByTagName(tagName);

        if (tagList.size() > 1) {
            throw new XMLException("Expected only one '" + tagName + "' tags, but found " +
                    tagList.size());

        } else if (tagList.size() < 1) {
            throw new XMLException("Expected a '" + tagName + "' tag.");

        } else {
            return tagList.get(0);
        }

    }

}
