package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for ExistsDumpMessaggioResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="existsResponse">
 *     &lt;sequence>
 *         &lt;element name="exists" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * ExistsDumpMessaggioResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "existsResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "exists"
})
@javax.xml.bind.annotation.XmlRootElement(name = "existsResponse")
public class ExistsDumpMessaggioResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="exists",required=true,nillable=false)
	private Boolean exists;
	
	public void setExists(Boolean exists){
		this.exists = exists;
	}
	
	public Boolean getExists(){
		return this.exists;
	}
	
	
	
	
}