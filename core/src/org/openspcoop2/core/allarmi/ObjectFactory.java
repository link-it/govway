/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.allarmi package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.allarmi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AllarmeParametro }
     */
    public AllarmeParametro createAllarmeParametro() {
        return new AllarmeParametro();
    }

    /**
     * Create an instance of {@link IdAllarme }
     */
    public IdAllarme createIdAllarme() {
        return new IdAllarme();
    }

    /**
     * Create an instance of {@link ElencoAllarmi }
     */
    public ElencoAllarmi createElencoAllarmi() {
        return new ElencoAllarmi();
    }

    /**
     * Create an instance of {@link AllarmeRaggruppamento }
     */
    public AllarmeRaggruppamento createAllarmeRaggruppamento() {
        return new AllarmeRaggruppamento();
    }

    /**
     * Create an instance of {@link AllarmeHistory }
     */
    public AllarmeHistory createAllarmeHistory() {
        return new AllarmeHistory();
    }

    /**
     * Create an instance of {@link AllarmeMail }
     */
    public AllarmeMail createAllarmeMail() {
        return new AllarmeMail();
    }

    /**
     * Create an instance of {@link ElencoIdAllarmi }
     */
    public ElencoIdAllarmi createElencoIdAllarmi() {
        return new ElencoIdAllarmi();
    }

    /**
     * Create an instance of {@link Allarme }
     */
    public Allarme createAllarme() {
        return new Allarme();
    }

    /**
     * Create an instance of {@link AllarmeFiltro }
     */
    public AllarmeFiltro createAllarmeFiltro() {
        return new AllarmeFiltro();
    }

    /**
     * Create an instance of {@link AllarmeScript }
     */
    public AllarmeScript createAllarmeScript() {
        return new AllarmeScript();
    }


 }
