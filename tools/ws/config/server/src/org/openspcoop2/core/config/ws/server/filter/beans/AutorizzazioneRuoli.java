/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for AutorizzazioneRuoli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autorizzazione-ruoli"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="match" type="{http://www.openspcoop2.org/core/config}RuoloTipoMatch" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;

/**     
 * AutorizzazioneRuoli
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "autorizzazione-ruoli", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "match"
})
@javax.xml.bind.annotation.XmlRootElement(name = "autorizzazione-ruoli")
public class AutorizzazioneRuoli extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="match",required=false,nillable=false)
	private RuoloTipoMatch match;
	
	public void setMatch(RuoloTipoMatch match){
		this.match = match;
	}
	
	public RuoloTipoMatch getMatch(){
		return this.match;
	}
	
	
	
	
}