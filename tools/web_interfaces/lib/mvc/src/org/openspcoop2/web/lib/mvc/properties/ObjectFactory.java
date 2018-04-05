/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.lib.mvc.properties;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.web.lib.mvc.properties package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.web.lib.mvc.properties
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Property }
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Conditions }
     */
    public Conditions createConditions() {
        return new Conditions();
    }

    /**
     * Create an instance of {@link ItemValue }
     */
    public ItemValue createItemValue() {
        return new ItemValue();
    }

    /**
     * Create an instance of {@link Condition }
     */
    public Condition createCondition() {
        return new Condition();
    }

    /**
     * Create an instance of {@link Config }
     */
    public Config createConfig() {
        return new Config();
    }

    /**
     * Create an instance of {@link Subsection }
     */
    public Subsection createSubsection() {
        return new Subsection();
    }

    /**
     * Create an instance of {@link Selected }
     */
    public Selected createSelected() {
        return new Selected();
    }

    /**
     * Create an instance of {@link Collection }
     */
    public Collection createCollection() {
        return new Collection();
    }

    /**
     * Create an instance of {@link Item }
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Section }
     */
    public Section createSection() {
        return new Section();
    }

    /**
     * Create an instance of {@link Equals }
     */
    public Equals createEquals() {
        return new Equals();
    }

    /**
     * Create an instance of {@link ItemValues }
     */
    public ItemValues createItemValues() {
        return new ItemValues();
    }

    /**
     * Create an instance of {@link Undefined }
     */
    public Undefined createUndefined() {
        return new Undefined();
    }

    /**
     * Create an instance of {@link Properties }
     */
    public Properties createProperties() {
        return new Properties();
    }


 }
