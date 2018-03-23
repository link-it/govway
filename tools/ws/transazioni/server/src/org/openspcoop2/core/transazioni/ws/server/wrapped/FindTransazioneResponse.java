package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindTransazioneResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse">
 *     &lt;sequence>
 *         &lt;element name="transazione" type="{http://www.openspcoop2.org/core/transazioni}transazione" maxOccurs="1" />
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
 * FindTransazioneResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "transazione"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findResponse")
public class FindTransazioneResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="transazione",required=true,nillable=false)
	private Transazione transazione;
	
	public void setTransazione(Transazione transazione){
		this.transazione = transazione;
	}
	
	public Transazione getTransazione(){
		return this.transazione;
	}
	
	
	
	
}