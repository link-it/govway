package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindTransazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="find">
 *     &lt;sequence>
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/transazioni}search-filter-transazione" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterTransazione;

/**     
 * FindTransazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "find", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "filter"
})
@javax.xml.bind.annotation.XmlRootElement(name = "find")
public class FindTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="filter",required=true,nillable=false)
	private SearchFilterTransazione filter;
	
	public void setFilter(SearchFilterTransazione filter){
		this.filter = filter;
	}
	
	public SearchFilterTransazione getFilter(){
		return this.filter;
	}
	
	
	
	
}