/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.diagnostica.ws.server.wrapped;

/**
 * <p>Java class for FindAllMessaggioDiagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAll"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/diagnostica}search-filter-messaggio-diagnostico" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.diagnostica.ws.server.filter.SearchFilterMessaggioDiagnostico;

/**     
 * FindAllMessaggioDiagnostico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAll", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "filter"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAll")
public class FindAllMessaggioDiagnostico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="filter",required=true,nillable=false)
	private SearchFilterMessaggioDiagnostico filter;
	
	public void setFilter(SearchFilterMessaggioDiagnostico filter){
		this.filter = filter;
	}
	
	public SearchFilterMessaggioDiagnostico getFilter(){
		return this.filter;
	}
	
	
	
	
}