package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindDumpMessaggioResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse">
 *     &lt;sequence>
 *         &lt;element name="dumpMessaggio" type="{http://www.openspcoop2.org/core/transazioni}dump-messaggio" maxOccurs="1" />
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
 * FindDumpMessaggioResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findResponse", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "dumpMessaggio"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findResponse")
public class FindDumpMessaggioResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="dumpMessaggio",required=true,nillable=false)
	private DumpMessaggio dumpMessaggio;
	
	public void setDumpMessaggio(DumpMessaggio dumpMessaggio){
		this.dumpMessaggio = dumpMessaggio;
	}
	
	public DumpMessaggio getDumpMessaggio(){
		return this.dumpMessaggio;
	}
	
	
	
	
}