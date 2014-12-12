/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.core.tracciamento;

import java.io.Serializable;
import java.util.Date;


/** <p>Java class Traccia.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Traccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Dominio dominio;

  protected Date oraRegistrazione;

  protected TracciaEsitoElaborazione esitoElaborazione;

  protected String identificativoCorrelazioneRichiesta;

  protected String identificativoCorrelazioneRisposta;

  protected String location;

  protected Busta busta;

  protected String bustaXml;

  protected Allegati allegati;

  protected String tipo;


  public Traccia() {
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

  public Dominio getDominio() {
    return this.dominio;
  }

  public void setDominio(Dominio dominio) {
    this.dominio = dominio;
  }

  public Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public TracciaEsitoElaborazione getEsitoElaborazione() {
    return this.esitoElaborazione;
  }

  public void setEsitoElaborazione(TracciaEsitoElaborazione esitoElaborazione) {
    this.esitoElaborazione = esitoElaborazione;
  }

  public String getIdentificativoCorrelazioneRichiesta() {
    if(this.identificativoCorrelazioneRichiesta!=null && ("".equals(this.identificativoCorrelazioneRichiesta)==false)){
		return this.identificativoCorrelazioneRichiesta.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativoCorrelazioneRichiesta(String identificativoCorrelazioneRichiesta) {
    this.identificativoCorrelazioneRichiesta = identificativoCorrelazioneRichiesta;
  }

  public String getIdentificativoCorrelazioneRisposta() {
    if(this.identificativoCorrelazioneRisposta!=null && ("".equals(this.identificativoCorrelazioneRisposta)==false)){
		return this.identificativoCorrelazioneRisposta.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativoCorrelazioneRisposta(String identificativoCorrelazioneRisposta) {
    this.identificativoCorrelazioneRisposta = identificativoCorrelazioneRisposta;
  }

  public String getLocation() {
    if(this.location!=null && ("".equals(this.location)==false)){
		return this.location.trim();
	}else{
		return null;
	}

  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Busta getBusta() {
    return this.busta;
  }

  public void setBusta(Busta busta) {
    this.busta = busta;
  }

  public String getBustaXml() {
    if(this.bustaXml!=null && ("".equals(this.bustaXml)==false)){
		return this.bustaXml.trim();
	}else{
		return null;
	}

  }

  public void setBustaXml(String bustaXml) {
    this.bustaXml = bustaXml;
  }

  public Allegati getAllegati() {
    return this.allegati;
  }

  public void setAllegati(Allegati allegati) {
    this.allegati = allegati;
  }

  public String getTipo() {
    if(this.tipo!=null && ("".equals(this.tipo)==false)){
		return this.tipo.trim();
	}else{
		return null;
	}

  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
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

  public static final String DOMINIO = "dominio";

  public static final String ORA_REGISTRAZIONE = "oraRegistrazione";

  public static final String ESITO_ELABORAZIONE = "esitoElaborazione";

  public static final String IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = "identificativoCorrelazioneRichiesta";

  public static final String IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = "identificativoCorrelazioneRisposta";

  public static final String LOCATION = "location";

  public static final String BUSTA = "busta";

  public static final String BUSTA_XML = "bustaXml";

  public static final String ALLEGATI = "allegati";

  public static final String TIPO = "tipo";

}
