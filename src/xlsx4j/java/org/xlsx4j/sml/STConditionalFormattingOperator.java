/*
 *  Copyright 2010, Plutext Pty Ltd.
 *   
 *  This file is part of docx4j.

    docx4j is licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 

    You may obtain a copy of the License at 

        http://www.apache.org/licenses/LICENSE-2.0 

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.

 */


package org.xlsx4j.sml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ST_ConditionalFormattingOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ST_ConditionalFormattingOperator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="lessThan"/>
 *     &lt;enumeration value="lessThanOrEqual"/>
 *     &lt;enumeration value="equal"/>
 *     &lt;enumeration value="notEqual"/>
 *     &lt;enumeration value="greaterThanOrEqual"/>
 *     &lt;enumeration value="greaterThan"/>
 *     &lt;enumeration value="between"/>
 *     &lt;enumeration value="notBetween"/>
 *     &lt;enumeration value="containsText"/>
 *     &lt;enumeration value="notContains"/>
 *     &lt;enumeration value="beginsWith"/>
 *     &lt;enumeration value="endsWith"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ST_ConditionalFormattingOperator")
@XmlEnum
public enum STConditionalFormattingOperator {


    /**
     * Less Than
     * 
     */
    @XmlEnumValue("lessThan")
    LESS_THAN("lessThan"),

    /**
     * Less Than Or Equal
     * 
     */
    @XmlEnumValue("lessThanOrEqual")
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),

    /**
     * Equal
     * 
     */
    @XmlEnumValue("equal")
    EQUAL("equal"),

    /**
     * Not Equal
     * 
     */
    @XmlEnumValue("notEqual")
    NOT_EQUAL("notEqual"),

    /**
     * Greater Than Or Equal
     * 
     */
    @XmlEnumValue("greaterThanOrEqual")
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual"),

    /**
     * Greater Than
     * 
     */
    @XmlEnumValue("greaterThan")
    GREATER_THAN("greaterThan"),

    /**
     * Between
     * 
     */
    @XmlEnumValue("between")
    BETWEEN("between"),

    /**
     * Not Between
     * 
     */
    @XmlEnumValue("notBetween")
    NOT_BETWEEN("notBetween"),

    /**
     * Contains
     * 
     */
    @XmlEnumValue("containsText")
    CONTAINS_TEXT("containsText"),

    /**
     * Does Not Contain
     * 
     */
    @XmlEnumValue("notContains")
    NOT_CONTAINS("notContains"),

    /**
     * Begins With
     * 
     */
    @XmlEnumValue("beginsWith")
    BEGINS_WITH("beginsWith"),

    /**
     * Ends With
     * 
     */
    @XmlEnumValue("endsWith")
    ENDS_WITH("endsWith");
    private final String value;

    STConditionalFormattingOperator(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static STConditionalFormattingOperator fromValue(String v) {
        for (STConditionalFormattingOperator c: STConditionalFormattingOperator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
