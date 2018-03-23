package org.openspcoop2.core.transazioni.ws.server.filter;

import javax.xml.bind.annotation.XmlRegistry;




/**     
 * ObjectFactory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
 @XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.tracciamento
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterTransazione }
     */
    public SearchFilterTransazione createSearchFilterTransazione() {
        return new SearchFilterTransazione();
    }
    
    /**
     * Create an instance of {@link SearchFilterDumpMessaggio }
     */
    public SearchFilterDumpMessaggio createSearchFilterDumpMessaggio() {
        return new SearchFilterDumpMessaggio();
    }
    

}