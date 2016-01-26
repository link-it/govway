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


/** <p>Java class OperationType.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class OperationType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String servizio;

  protected String operazione;

  protected String profiloDiCollaborazione;

  protected String servizioCorrelato;

  protected String operazioneCorrelata;


  public OperationType() {
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

  public String getServizio() {
    if(this.servizio!=null && ("".equals(this.servizio)==false)){
		return this.servizio.trim();
	}else{
		return null;
	}

  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public String getOperazione() {
    if(this.operazione!=null && ("".equals(this.operazione)==false)){
		return this.operazione.trim();
	}else{
		return null;
	}

  }

  public void setOperazione(String operazione) {
    this.operazione = operazione;
  }

  public String getProfiloDiCollaborazione() {
    if(this.profiloDiCollaborazione!=null && ("".equals(this.profiloDiCollaborazione)==false)){
		return this.profiloDiCollaborazione.trim();
	}else{
		return null;
	}

  }

  public void setProfiloDiCollaborazione(String profiloDiCollaborazione) {
    this.profiloDiCollaborazione = profiloDiCollaborazione;
  }

  public String getServizioCorrelato() {
    if(this.servizioCorrelato!=null && ("".equals(this.servizioCorrelato)==false)){
		return this.servizioCorrelato.trim();
	}else{
		return null;
	}

  }

  public void setServizioCorrelato(String servizioCorrelato) {
    this.servizioCorrelato = servizioCorrelato;
  }

  public String getOperazioneCorrelata() {
    if(this.operazioneCorrelata!=null && ("".equals(this.operazioneCorrelata)==false)){
		return this.operazioneCorrelata.trim();
	}else{
		return null;
	}

  }

  public void setOperazioneCorrelata(String operazioneCorrelata) {
    this.operazioneCorrelata = operazioneCorrelata;
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

  public static final String SERVIZIO = "servizio";

  public static final String OPERAZIONE = "operazione";

  public static final String PROFILO_DI_COLLABORAZIONE = "profiloDiCollaborazione";

  public static final String SERVIZIO_CORRELATO = "servizioCorrelato";

  public static final String OPERAZIONE_CORRELATA = "operazioneCorrelata";

}
