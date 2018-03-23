package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindAllDumpMessaggioResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllResponse">
 *     &lt;sequence>
 *         &lt;element name="findReturn" type="{http://www.openspcoop2.org/core/transazioni}dump-messaggio" maxOccurs="unbounded" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.DumpMessaggio;

/**     
 * FindAllDumpMessaggioResponse
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
public class FindAllDumpMessaggioResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="findReturn",required=true,nillable=false)
	private java.util.List<DumpMessaggio> findReturn;
	
	public void setFindReturn(java.util.List<DumpMessaggio> findReturn){
		this.findReturn = findReturn;
	}
	
	public java.util.List<DumpMessaggio> getFindReturn(){
		return this.findReturn;
	}
	
	
	
	
}