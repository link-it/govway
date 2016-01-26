/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.spcoop.sica.wscp;

import java.io.Serializable;


/** <p>Java class ProfiloCollaborazioneEGOV.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ProfiloCollaborazioneEGOV extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String versioneEGOV;

  protected String riferimentoDefinizioneInterfaccia;

  protected OperationListType listaCollaborazioni;


  public ProfiloCollaborazioneEGOV() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public String getVersioneEGOV() {
    if(this.versioneEGOV!=null && ("".equals(this.versioneEGOV)==false)){
		return this.versioneEGOV.trim();
	}else{
		return null;
	}

  }

  public void setVersioneEGOV(String versioneEGOV) {
    this.versioneEGOV = versioneEGOV;
  }

  public String getRiferimentoDefinizioneInterfaccia() {
    if(this.riferimentoDefinizioneInterfaccia!=null && ("".equals(this.riferimentoDefinizioneInterfaccia)==false)){
		return this.riferimentoDefinizioneInterfaccia.trim();
	}else{
		return null;
	}

  }

  public void setRiferimentoDefinizioneInterfaccia(String riferimentoDefinizioneInterfaccia) {
    this.riferimentoDefinizioneInterfaccia = riferimentoDefinizioneInterfaccia;
  }

  public OperationListType getListaCollaborazioni() {
    return this.listaCollaborazioni;
  }

  public void setListaCollaborazioni(OperationListType listaCollaborazioni) {
    this.listaCollaborazioni = listaCollaborazioni;
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  public static final String VERSIONE_EGOV = "versioneEGOV";

  public static final String RIFERIMENTO_DEFINIZIONE_INTERFACCIA = "riferimentoDefinizioneInterfaccia";

  public static final String LISTA_COLLABORAZIONI = "listaCollaborazioni";

}
