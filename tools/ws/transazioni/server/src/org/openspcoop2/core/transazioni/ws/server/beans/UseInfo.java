package org.openspcoop2.core.transazioni.ws.server.beans;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**     
 * UseInfo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "use-info", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "inUseCondition",
    "used"
})
@javax.xml.bind.annotation.XmlRootElement(name = "use-info")
public class UseInfo extends org.openspcoop2.utils.beans.BaseBean {

	@XmlElement(name="inUseCondition",required=false,nillable=false)
	private List<InUseCondition> inUseCondition = new ArrayList<InUseCondition>();
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
	@XmlElement(name="used",required=true,nillable=false)
	private boolean used;
	
	public void addInUseCondition(InUseCondition inUse){
		this.inUseCondition.add(inUse);
	}
	
	public InUseCondition getInUseCondition(int index){
		return this.inUseCondition.get(index);
	}
	
	public InUseCondition removeInUseCondition(int index){
		return this.inUseCondition.remove(index);
	}
	
	public int sizeInUseConditionList(){
		return this.inUseCondition.size();
	}

	public List<InUseCondition> getInUseCondition() {
		return this.inUseCondition;
	}

	public void setInUseCondition(List<InUseCondition> inUse) {
		this.inUseCondition = inUse;
	}

	public boolean isUsed() {
		return this.used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
}