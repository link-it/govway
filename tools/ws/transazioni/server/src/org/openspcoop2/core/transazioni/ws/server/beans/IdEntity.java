package org.openspcoop2.core.transazioni.ws.server.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**     
 * IdEntity
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "id-entity", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
	    "id"
	})
public class IdEntity extends org.openspcoop2.utils.beans.BaseBean {
	@XmlElements({
	@XmlElement(name="wrapperIdTransazione", namespace="http://www.openspcoop2.org/core/transazioni/management", type=WrapperIdTransazione.class),
	
	@XmlElement(name="wrapperIdDumpMessaggio", namespace="http://www.openspcoop2.org/core/transazioni/management", type=WrapperIdDumpMessaggio.class)
	})
	
	private Object id;
	
	public Object getId() {
		return this.id;
	}

	public void setId(WrapperIdTransazione id) {
		this.id = id;
	}
	public void setId(WrapperIdDumpMessaggio id) {	
		this.id = id;
	}
	
//	public void setId(Object id) {
//		this.id = id;
//	} 
	
}
