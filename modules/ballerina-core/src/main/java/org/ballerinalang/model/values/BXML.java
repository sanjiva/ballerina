/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.model.values;

import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.util.XMLNodeType;
import org.ballerinalang.runtime.message.BallerinaMessageDataSource;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.OutputStream;

/**
 * {@code BXML} represents a XML in Ballerina. An XML could be one of:
 * <ul>
 * <li>element</li>
 * <li>text</li>
 * <li>comment</li>
 * <li>processing instruction</li>
 * <li>sequence of above</li>
 * </ul>
 * 
 * @param <T> Type of the BXML
 *
 * @since 0.8.0
 */
public abstract class BXML<T> extends BallerinaMessageDataSource implements BRefType<T> {

    protected OutputStream outputStream;
    
    /**
     * Start of a XML comment.
     */
    public static final String COMMENT_START = "<!--";
    
    /**
     * End of a XML Comment.
     */
    public static final String COMMENT_END = "-->";
    
    /**
     * Start of a XML processing instruction.
     */
    public static final String PI_START = "<?";
    
    /**
     * End of a XML processing instruction.
     */
    public static final String PI_END = "?>";
    
    /**
     * Check whether the XML sequence is empty.
     * 
     * @return Flag indicating whether the XML sequence is empty
     */
    public abstract BBoolean isEmpty();
    
    /**
     * Check whether the XML sequence contains only a single element.
     * 
     * @return Flag indicating whether the XML sequence contains only a single element
     */
    public abstract BBoolean isSingleton();
    
    /**
     * Get the type of the XML as a {@link BString}. Type can be one of "element", "text", "comment" or "pi".
     * 
     * @return Type of the XML as a {@link BString}
     */
    public abstract BString getItemType();
    
    /**
     * Get the fully qualified name of the element as a {@link BString}.
     * 
     * @return fully qualified name of the element as a {@link BString}.
     */
    public abstract BString getElementName();
    
    /**
     * Get the text values in this XML.
     * 
     * @return text values in this XML.
     */
    public abstract BString getTextValue();
    
    /**
     * Get the value of a single attribute as a string.
     * 
     * @param namespace Namespace of the attribute
     * @param localName Local name of the attribute
     * @return Value of the attribute
     */
    public abstract BString getAttribute(String namespace, String localName);
    
    /**
     * Get the value of a single attribute as a string.
     * 
     * @param namespace Namespace of the attribute
     * @param prefix    Prefix of the namespace
     * @param localName Local name of the attribute
     * @return Value of the attribute
     */
    public abstract BString getAttribute(String namespace, String prefix, String localName);
    
    /**
     * Set the value of a single attribute as a {@link BString}.
     * 
     * @param namespace Namespace of the attribute
     * @param prefix Namespace prefix of the attribute
     * @param localName Local name of the attribute
     * @param value Value of the attribute
     */
    public abstract void setAttribute(String namespace, String prefix, String localName, String value);

    
    /**
     * Get attributes as a {@link BMap}.
     * 
     * @return Attributes as a {@link BMap}
     */
    public abstract BMap<?, ?> getAttributes();

    /**
     * Get all the elements-type items, in the given sequence.
     * 
     * @return All the elements-type items, in the given sequence
     */
    public abstract BXML<?> elements();
    
    /**
     * Get all the elements-type items in the given sequence, that matches a given qualified name.
     * 
     * @param qname qualified name of the element
     * @return All the elements-type items, that matches a given qualified name, from the this sequence.
     */
    public abstract BXML<?> elements(BString qname);
    
    /**
     * Selects and concatenate all the children sequences of the elements in this sequence.
     * 
     * @return All the children sequences of the elements in this sequence
     */
    public abstract BXML<?> children();
    
    /**
     * Selects and concatenate all the children sequences that matches the given qualified name, 
     * in all the element-type items in this sequence. Only the children will be selected, but not
     * the nested children.
     * 
     * @param qname qualified name of the children to filter
     * @return All the children that matches the given qualified name, as a sequence
     */
    public abstract BXML<?> children(BString qname);
    
    /**
     * Set the children of this XML. Any existing children will be removed.
     * 
     * @param seq XML Sequence to be set as the children 
     */
    public abstract void setChildren(BXML<?> seq);
    
    /**
     * Set the attributes to the element.
     * 
     * @param attributes Attributes as a map
     */
    public abstract void setAttribute(BMap<BString, ?> attributes);
    
    /**
     * Strips any text items from the XML that are all whitespace.
     *
     * @return striped xml
     */
    public abstract BXML<?> strip();
    
    /**
     * Get the type of the XML.
     * 
     * @return Type of the XML
     */
    public abstract XMLNodeType getNodeType();
    
    /**
     * Returns a deep copy of the XML.
     */
    public abstract BXML<?> copy();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return stringValue();
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BType getType() {
        return BTypes.typeXML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageAsString() {
        return stringValue();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BallerinaMessageDataSource clone() {
        return copy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    // private methods
    
    protected static void handleXmlException(String message, Throwable t) {
        // Here local message of the cause is logged whenever possible, to avoid java class being logged
        // along with the error message.
        if (t.getCause() != null) {
            throw new BallerinaException(message + t.getCause().getMessage());
        } else {
            throw new BallerinaException(message + t.getMessage());
        }
    }

    /**
     * Slice and return a subsequence of the given XML sequence.
     * 
     * @param startIndex    To slice
     * @param endIndex      To slice
     * @return sliced sequence
     */
    public abstract BValue slice(long startIndex, long endIndex);
}
