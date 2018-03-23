package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindAllTransazioneResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllResponse">
 *     &lt;sequence>
 *         &lt;element name="findReturn" type="{http://www.openspcoop2.org/core/transazioni}transazione" maxOccurs="unbounded" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.Transazione;

/**     
 * FindAllTransazioneResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAllResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "findReturn"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAllResponse")
public class FindAllTransazioneResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="findReturn",required=true,nillable=false)
	private java.util.List<Transazione> findReturn;
	
	public void setFindReturn(java.util.List<Transazione> findReturn){
		this.findReturn = findReturn;
	}
	
	public java.util.List<Transazione> getFindReturn(){
		return this.findReturn;
	}
	
	
	
	
}