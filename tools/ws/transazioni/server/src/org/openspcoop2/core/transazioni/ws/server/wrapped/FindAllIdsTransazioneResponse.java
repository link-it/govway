package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindAllIdsTransazioneResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllIdsResponse">
 *     &lt;sequence>
 *         &lt;element name="itemsFound" type="{http://www.openspcoop2.org/core/transazioni}transazione" maxOccurs="unbounded" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * FindAllIdsTransazioneResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAllIdsResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "itemsFound"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAllIdsResponse")
public class FindAllIdsTransazioneResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="itemsFound",required=true,nillable=false)
	private java.util.List<String> itemsFound;
	
	public void setItemsFound(java.util.List<String> itemsFound){
		this.itemsFound = itemsFound;
	}
	
	public java.util.List<String> getItemsFound(){
		return this.itemsFound;
	}
	
	
	
	
}