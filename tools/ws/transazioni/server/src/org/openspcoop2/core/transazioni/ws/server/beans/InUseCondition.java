package org.openspcoop2.core.transazioni.ws.server.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**     
 * InUseCondition
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "in-use-condition", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
	"name",
    "type",
    "id",
    "cause"
})

public class InUseCondition extends org.openspcoop2.utils.beans.BaseBean {

	@javax.xml.bind.annotation.XmlSchemaType(name="string")
	@XmlElement(name="name",required=true,nillable=false)
	private String name;

	@XmlElement(name="type",required=false,nillable=false)
	private Identified type;
	
	@XmlElement(name="id",required=false,nillable=false)
	private List<IdEntity> id;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
	@XmlElement(name="cause",required=true,nillable=false)
	private String cause;

	public Identified getType() {
		return this.type;
	}

	public void setType(Identified type) {
		this.type = type;
	}

	public String getCause() {
		return this.cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IdEntity> getId() {
		return this.id;
	}

	public void setId(List<IdEntity> idList) {
		this.id = idList;
	}
	
	public void addId(IdEntity idEntity){
		if(this.id==null)
			this.id = new ArrayList<IdEntity>();
		
		this.id.add(idEntity);
		
	}
}