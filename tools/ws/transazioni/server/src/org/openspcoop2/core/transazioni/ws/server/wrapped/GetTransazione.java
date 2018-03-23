package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for GetTransazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get">
 *     &lt;sequence>
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/transazioni}string" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * GetTransazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "get", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "id"
})
@javax.xml.bind.annotation.XmlRootElement(name = "get")
public class GetTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id",required=true,nillable=false)
	private String id;
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	
	
	
}