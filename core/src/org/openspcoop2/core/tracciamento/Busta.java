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


/** <p>Java class Busta.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Busta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Soggetto mittente;

  protected Soggetto destinatario;

  protected ProfiloCollaborazione profiloCollaborazione;

  protected Servizio servizio;

  protected String azione;

  protected Servizio servizioCorrelato;

  protected String collaborazione;

  protected String identificativo;

  protected String riferimentoMessaggio;

  protected Data oraRegistrazione;

  protected Date scadenza;

  protected ProfiloTrasmissione profiloTrasmissione;

  protected String servizioApplicativoFruitore;

  protected String servizioApplicativoErogatore;

  protected String digest;

  protected Trasmissioni trasmissioni;

  protected Riscontri riscontri;

  protected Eccezioni eccezioni;

  protected Protocollo protocollo;


  public Busta() {
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

  public Soggetto getMittente() {
    return this.mittente;
  }

  public void setMittente(Soggetto mittente) {
    this.mittente = mittente;
  }

  public Soggetto getDestinatario() {
    return this.destinatario;
  }

  public void setDestinatario(Soggetto destinatario) {
    this.destinatario = destinatario;
  }

  public ProfiloCollaborazione getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(ProfiloCollaborazione profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public Servizio getServizio() {
    return this.servizio;
  }

  public void setServizio(Servizio servizio) {
    this.servizio = servizio;
  }

  public String getAzione() {
    if(this.azione!=null && ("".equals(this.azione)==false)){
		return this.azione.trim();
	}else{
		return null;
	}

  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public Servizio getServizioCorrelato() {
    return this.servizioCorrelato;
  }

  public void setServizioCorrelato(Servizio servizioCorrelato) {
    this.servizioCorrelato = servizioCorrelato;
  }

  public String getCollaborazione() {
    if(this.collaborazione!=null && ("".equals(this.collaborazione)==false)){
		return this.collaborazione.trim();
	}else{
		return null;
	}

  }

  public void setCollaborazione(String collaborazione) {
    this.collaborazione = collaborazione;
  }

  public String getIdentificativo() {
    if(this.identificativo!=null && ("".equals(this.identificativo)==false)){
		return this.identificativo.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativo(String identificativo) {
    this.identificativo = identificativo;
  }

  public String getRiferimentoMessaggio() {
    if(this.riferimentoMessaggio!=null && ("".equals(this.riferimentoMessaggio)==false)){
		return this.riferimentoMessaggio.trim();
	}else{
		return null;
	}

  }

  public void setRiferimentoMessaggio(String riferimentoMessaggio) {
    this.riferimentoMessaggio = riferimentoMessaggio;
  }

  public Data getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Data oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public Date getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(Date scadenza) {
    this.scadenza = scadenza;
  }

  public ProfiloTrasmissione getProfiloTrasmissione() {
    return this.profiloTrasmissione;
  }

  public void setProfiloTrasmissione(ProfiloTrasmissione profiloTrasmissione) {
    this.profiloTrasmissione = profiloTrasmissione;
  }

  public String getServizioApplicativoFruitore() {
    if(this.servizioApplicativoFruitore!=null && ("".equals(this.servizioApplicativoFruitore)==false)){
		return this.servizioApplicativoFruitore.trim();
	}else{
		return null;
	}

  }

  public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
    this.servizioApplicativoFruitore = servizioApplicativoFruitore;
  }

  public String getServizioApplicativoErogatore() {
    if(this.servizioApplicativoErogatore!=null && ("".equals(this.servizioApplicativoErogatore)==false)){
		return this.servizioApplicativoErogatore.trim();
	}else{
		return null;
	}

  }

  public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public String getDigest() {
    if(this.digest!=null && ("".equals(this.digest)==false)){
		return this.digest.trim();
	}else{
		return null;
	}

  }

  public void setDigest(String digest) {
    this.digest = digest;
  }

  public Trasmissioni getTrasmissioni() {
    return this.trasmissioni;
  }

  public void setTrasmissioni(Trasmissioni trasmissioni) {
    this.trasmissioni = trasmissioni;
  }

  public Riscontri getRiscontri() {
    return this.riscontri;
  }

  public void setRiscontri(Riscontri riscontri) {
    this.riscontri = riscontri;
  }

  public Eccezioni getEccezioni() {
    return this.eccezioni;
  }

  public void setEccezioni(Eccezioni eccezioni) {
    this.eccezioni = eccezioni;
  }

  public Protocollo getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(Protocollo protocollo) {
    this.protocollo = protocollo;
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

  public static final String MITTENTE = "mittente";

  public static final String DESTINATARIO = "destinatario";

  public static final String PROFILO_COLLABORAZIONE = "profiloCollaborazione";

  public static final String SERVIZIO = "servizio";

  public static final String AZIONE = "azione";

  public static final String SERVIZIO_CORRELATO = "servizioCorrelato";

  public static final String COLLABORAZIONE = "collaborazione";

  public static final String IDENTIFICATIVO = "identificativo";

  public static final String RIFERIMENTO_MESSAGGIO = "riferimentoMessaggio";

  public static final String ORA_REGISTRAZIONE = "oraRegistrazione";

  public static final String SCADENZA = "scadenza";

  public static final String PROFILO_TRASMISSIONE = "profiloTrasmissione";

  public static final String SERVIZIO_APPLICATIVO_FRUITORE = "servizioApplicativoFruitore";

  public static final String SERVIZIO_APPLICATIVO_EROGATORE = "servizioApplicativoErogatore";

  public static final String DIGEST = "digest";

  public static final String TRASMISSIONI = "trasmissioni";

  public static final String RISCONTRI = "riscontri";

  public static final String ECCEZIONI = "eccezioni";

  public static final String PROTOCOLLO = "protocollo";

}
