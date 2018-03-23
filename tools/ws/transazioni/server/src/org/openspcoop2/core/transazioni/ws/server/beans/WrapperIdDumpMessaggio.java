package org.openspcoop2.core.transazioni.ws.server.beans;

/**
 * <p>Java class for WrapperIdDumpMessaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wrapperIdDumpMessaggio">
 *     &lt;sequence>
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/transazioni}id-dump-messaggio" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;

/**     
 * WrapperIdDumpMessaggio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "wrapperIdDumpMessaggio", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "id"
})
@javax.xml.bind.annotation.XmlRootElement(name = "wrapperIdDumpMessaggio")
public class WrapperIdDumpMessaggio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="id",required=true,nillable=false)
	private IdDumpMessaggio id;
	
	public void setId(IdDumpMessaggio id){
		this.id = id;
	}
	
	public IdDumpMessaggio getId(){
		return this.id;
	}
	
	
	
	
}