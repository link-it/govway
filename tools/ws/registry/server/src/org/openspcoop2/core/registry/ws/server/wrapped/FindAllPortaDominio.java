/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.registry.ws.server.wrapped;

/**
 * <p>Java class for FindAllPortaDominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAll">
 *     &lt;sequence>
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/registry}search-filter-porta-dominio" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.ws.server.filter.SearchFilterPortaDominio;

/**     
 * FindAllPortaDominio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAll", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "filter"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAll")
public class FindAllPortaDominio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="filter",required=true,nillable=false)
	private SearchFilterPortaDominio filter;
	
	public void setFilter(SearchFilterPortaDominio filter){
		this.filter = filter;
	}
	
	public SearchFilterPortaDominio getFilter(){
		return this.filter;
	}
	
	
	
	
}