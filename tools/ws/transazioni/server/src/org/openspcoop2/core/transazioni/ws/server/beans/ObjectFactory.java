package org.openspcoop2.core.transazioni.ws.server.beans;

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.transazioni
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WrapperIdDumpMessaggio }
     */
    public WrapperIdDumpMessaggio createWrapperIdDumpMessaggio() {
        return new WrapperIdDumpMessaggio();
    }
    
    /**
     * Create an instance of {@link WrapperIdTransazione }
     */
    public WrapperIdTransazione createWrapperIdTransazione() {
        return new WrapperIdTransazione();
    }
    
    /**
     * Create an instance of {@link IdEntity }
     */
    public IdEntity createIdEntity() {
        return new IdEntity();
    }
    
    /**
     * Create an instance of {@link InUseCondition }
     */
    public InUseCondition createInUseCondition() {
        return new InUseCondition();
    }
    
    /**
     * Create an instance of {@link UseInfo }
     */
    public UseInfo createUseInfo() {
        return new UseInfo();
    }
    

}