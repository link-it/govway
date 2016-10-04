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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for ValidazioneContenutiApplicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi">
 *     &lt;sequence>
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" minOccurs="0" maxOccurs="1" default="(ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString("xsd")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;

/**     
 * ValidazioneContenutiApplicativi
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "validazione-contenuti-applicativi", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "stato",
    "tipo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "validazione-contenuti-applicativi")
public class ValidazioneContenutiApplicativi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="stato",required=false,nillable=false)
	private StatoFunzionalitaConWarning stato;
	
	public void setStato(StatoFunzionalitaConWarning stato){
		this.stato = stato;
	}
	
	public StatoFunzionalitaConWarning getStato(){
		return this.stato;
	}
	
	
	@XmlElement(name="tipo",required=false,nillable=false,defaultValue="xsd")
	private ValidazioneContenutiApplicativiTipo tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString("xsd");
	
	public void setTipo(ValidazioneContenutiApplicativiTipo tipo){
		this.tipo = tipo;
	}
	
	public ValidazioneContenutiApplicativiTipo getTipo(){
		return this.tipo;
	}
	
	
	
	
}