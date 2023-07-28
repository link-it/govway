/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wscp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for profiloCollaborazioneEGOV complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profiloCollaborazioneEGOV"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="versioneEGOV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="riferimentoDefinizioneInterfaccia" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="listaCollaborazioni" type="{http://spcoop.gov.it/sica/wscp}operationListType" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profiloCollaborazioneEGOV", 
  propOrder = {
  	"versioneEGOV",
  	"riferimentoDefinizioneInterfaccia",
  	"listaCollaborazioni"
  }
)

@XmlRootElement(name = "profiloCollaborazioneEGOV")

public class ProfiloCollaborazioneEGOV extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ProfiloCollaborazioneEGOV() {
    super();
  }

  public java.lang.String getVersioneEGOV() {
    return this.versioneEGOV;
  }

  public void setVersioneEGOV(java.lang.String versioneEGOV) {
    this.versioneEGOV = versioneEGOV;
  }

  public java.net.URI getRiferimentoDefinizioneInterfaccia() {
    return this.riferimentoDefinizioneInterfaccia;
  }

  public void setRiferimentoDefinizioneInterfaccia(java.net.URI riferimentoDefinizioneInterfaccia) {
    this.riferimentoDefinizioneInterfaccia = riferimentoDefinizioneInterfaccia;
  }

  public OperationListType getListaCollaborazioni() {
    return this.listaCollaborazioni;
  }

  public void setListaCollaborazioni(OperationListType listaCollaborazioni) {
    this.listaCollaborazioni = listaCollaborazioni;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.spcoop.sica.wscp.model.ProfiloCollaborazioneEGOVModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV.modelStaticInstance==null){
  			it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV.modelStaticInstance = new it.gov.spcoop.sica.wscp.model.ProfiloCollaborazioneEGOVModel();
	  }
  }
  public static it.gov.spcoop.sica.wscp.model.ProfiloCollaborazioneEGOVModel model(){
	  if(it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="versioneEGOV",required=true,nillable=false)
  protected java.lang.String versioneEGOV;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="riferimentoDefinizioneInterfaccia",required=true,nillable=false)
  protected java.net.URI riferimentoDefinizioneInterfaccia;

  @XmlElement(name="listaCollaborazioni",required=true,nillable=false)
  protected OperationListType listaCollaborazioni;

}
