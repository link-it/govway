package org.openspcoop2.core.transazioni.ws.server.wrapped;

/**
 * <p>Java class for FindDumpMessaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="find">
 *     &lt;sequence>
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/transazioni}search-filter-dump-messaggio" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterDumpMessaggio;

/**     
 * FindDumpMessaggio
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
public class FindDumpMessaggio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="filter",required=true,nillable=false)
	private SearchFilterDumpMessaggio filter;
	
	public void setFilter(SearchFilterDumpMessaggio filter){
		this.filter = filter;
	}
	
	public SearchFilterDumpMessaggio getFilter(){
		return this.filter;
	}
	
	
	
	
}