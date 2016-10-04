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
package org.openspcoop2.core.registry.ws.server.filter.beans;

/**
 * <p>Java class for Servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio">
 *     &lt;sequence>
 *         &lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry/management}connettore" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipologia-servizio" type="{http://www.openspcoop2.org/core/registry}TipologiaServizio" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.ws.server.filter.beans.Connettore;

/**     
 * Servizio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "servizio", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "connettore",
    "tipoSoggettoErogatore",
    "nomeSoggettoErogatore",
    "tipo",
    "nome",
    "tipologiaServizio"
})
@javax.xml.bind.annotation.XmlRootElement(name = "servizio")
public class Servizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="connettore",required=false,nillable=false)
	private Connettore connettore;
	
	public void setConnettore(Connettore connettore){
		this.connettore = connettore;
	}
	
	public Connettore getConnettore(){
		return this.connettore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-erogatore",required=false,nillable=false)
	private String tipoSoggettoErogatore;
	
	public void setTipoSoggettoErogatore(String tipoSoggettoErogatore){
		this.tipoSoggettoErogatore = tipoSoggettoErogatore;
	}
	
	public String getTipoSoggettoErogatore(){
		return this.tipoSoggettoErogatore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-erogatore",required=false,nillable=false)
	private String nomeSoggettoErogatore;
	
	public void setNomeSoggettoErogatore(String nomeSoggettoErogatore){
		this.nomeSoggettoErogatore = nomeSoggettoErogatore;
	}
	
	public String getNomeSoggettoErogatore(){
		return this.nomeSoggettoErogatore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
	private String tipo;
	
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	
	public String getTipo(){
		return this.tipo;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
	private String nome;
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	
	@XmlElement(name="tipologia-servizio",required=false,nillable=false)
	private TipologiaServizio tipologiaServizio;
	
	public void setTipologiaServizio(TipologiaServizio tipologiaServizio){
		this.tipologiaServizio = tipologiaServizio;
	}
	
	public TipologiaServizio getTipologiaServizio(){
		return this.tipologiaServizio;
	}
	
	
	
	
}