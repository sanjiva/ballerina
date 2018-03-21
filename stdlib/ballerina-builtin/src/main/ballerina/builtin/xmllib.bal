// Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package ballerina.builtin;

@Description { value:"Check whether the XML sequence contains only a single element."}
@Param { value:"x: An XML object" }
@Return { value:"Boolean flag indicating whether the XML sequence contains only a single element" }
public native function <xml x> isSingleton() returns (boolean);

@Description { value:"Check whether the XML sequence is empty."}
@Param { value:"x: An XML object" }
@Return { value:"Boolean flag indicating whether the XML sequence is empty" }
public native function <xml x> isEmpty() returns (boolean);

@Description { value:"Get all the items that are of element type in an XML sequence."}
@Param { value:"x: An XML object" }
@Return { value:"All the elements-type items in the given XML sequence" }
public native function <xml x> elements() returns (xml);

@Description { value:"Get all the items that are of element type, and matches the given qualified name, in an XML sequence."}
@Param { value:"x: An XML object" }
@Param { value:"qname: Qualified name of the element" }
@Return { value:"All the elements-type items in the given XML sequence, that matches the qualified name" }
public native function <xml x> select(string qname) returns (xml);

@Description { value:"Get the type of a XML as a string. If the XML is singleton, type can be one of 'element', 'text', 'comment' or 'pi'. Returns an empty string if the XML is not a singleton."}
@Param { value:"x: An XML object" }
@Return { value:"Type of the XML as a string" }
public native function <xml x> getItemType() returns (string);

@Description { value:"Get the fully qualified name of the element as a string. Returns an empty string if the XML is not a singleton."}
@Param { value:"x: An XML object" }
@Return { value:"Qualified name of the XML as a string" }
public native function <xml x> getElementName() returns (string);

@Description { value:"Get the text value of a XML. If the XML is a sequence, concatenation of the text values of the members of the sequence is returned. If the XML is an element, then the text value of the sequence of children is returned.  If the XML is a text item, then the text is returned. Otherwise, an empty string is returned."}
@Param { value:"x: An XML object" }
@Return { value:"Text value of the xml" }
public native function <xml x> getTextValue() returns (string);

@Description { value:"Selects all the children of the elements in an XML, and return as a sequence."}
@Param { value:"x: An XML object" }
@Return { value:"All the children of the elements in the xml" }
public native function <xml x> children() returns (xml);

@Description { value:"Selects all the children of the elements in this sequence that matches the given qualified name."}
@Param { value:"x: An XML object" }
@Param { value:"qname: Qualified name of the element" }
@Return { value:"All the children of the elements in this sequence that matches the given qualified name" }
public native function <xml x> selectChildren(string qname) returns (xml);

@Description { value:"Set the children of an XML if its a singleton. An Error otherwise. Any existing children will be removed."}
@Param { value:"x: An XML object" }
public native function <xml x> setChildren(xml children);

@Description { value:"Make a deep copy of an XML."}
@Param { value:"x: An XML object" }
@Return { value:"A Copy of the XML" }
public native function <xml x> copy() returns (xml);

@Description { value:"Strips any text items from an XML sequence that are all whitespace."}
@Param { value:"x: An XML object" }
@Return { value:"Striped sequence" }
public native function <xml x> strip() returns (xml);

@Description { value:"Slice and return a subsequence of the an XML sequence."}
@Param { value:"x: An XML object" }
@Param { value:"startIndex: Start index, inclusive" }
@Param { value:"endIndex: End index, exclusive" }
@Return { value:"Sliced sequence" }
public native function <xml x> slice(int startIndex, int endIndex) returns (xml);

@Description { value:"Sets the attributes to the provided attributes map."}
@Param { value:"x: An XML object" }
@Param { value:"attributes: Attributes map" }
public native function <xml x> setAttributes(map attributes);

@Description { value:"Converts a XML object to a JSON representation"}
@Param { value:"x: A XML object" }
@Param { value:"options: xmlOptions struct for XML to JSON conversion properties" }
@Return { value:"JSON representation of the given XML" }
public native function <xml x> toJSON (struct {
                                           string attributePrefix = "@";
                                           boolean preserveNamespaces = true;
                                       } options) returns (json);

@Description { value:"Searches in children recursively for elements matching the qualified name and returns a sequence containing them all. Does not search within a matched result." }
@Param { value:"x: An XML object" }
@Param { value:"qname: Qualified name of the element" }
@Return { value:"All the descendants that matches the given qualified name, as a sequence" }
public native function <xml x> selectDescendants(string qname) returns (xml);

@Description { value:"Remove an attribute from an XML." }
@Param { value:"x: An XML object" }
@Param { value:"qname: Qualified name of the attribute" }
public native function <xml x> removeAttribute(string qname);
