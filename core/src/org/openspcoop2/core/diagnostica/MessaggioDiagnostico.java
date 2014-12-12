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
package org.openspcoop2.core.diagnostica;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


/** <p>Java class MessaggioDiagnostico.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class MessaggioDiagnostico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Dominio dominio;

  protected String identificativoRichiesta;

  protected String identificativoRisposta;

  protected Date oraRegistrazione;

  protected String codice;

  protected String messaggio;

  protected BigInteger severita;

  protected Protocollo protocollo;


  public MessaggioDiagnostico() {
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

  public String getIdentificativoRichiesta() {
    if(this.identificativoRichiesta!=null && ("".equals(this.identificativoRichiesta)==false)){
		return this.identificativoRichiesta.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativoRichiesta(String identificativoRichiesta) {
    this.identificativoRichiesta = identificativoRichiesta;
  }

  public String getIdentificativoRisposta() {
    if(this.identificativoRisposta!=null && ("".equals(this.identificativoRisposta)==false)){
		return this.identificativoRisposta.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativoRisposta(String identificativoRisposta) {
    this.identificativoRisposta = identificativoRisposta;
  }

  public Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public String getCodice() {
    if(this.codice!=null && ("".equals(this.codice)==false)){
		return this.codice.trim();
	}else{
		return null;
	}

  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public String getMessaggio() {
    if(this.messaggio!=null && ("".equals(this.messaggio)==false)){
		return this.messaggio.trim();
	}else{
		return null;
	}

  }

  public void setMessaggio(String messaggio) {
    this.messaggio = messaggio;
  }

  public BigInteger getSeverita() {
    return this.severita;
  }

  public void setSeverita(BigInteger severita) {
    this.severita = severita;
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

  public static final String DOMINIO = "dominio";

  public static final String IDENTIFICATIVO_RICHIESTA = "identificativoRichiesta";

  public static final String IDENTIFICATIVO_RISPOSTA = "identificativoRisposta";

  public static final String ORA_REGISTRAZIONE = "oraRegistrazione";

  public static final String CODICE = "codice";

  public static final String MESSAGGIO = "messaggio";

  public static final String SEVERITA = "severita";

  public static final String PROTOCOLLO = "protocollo";

}
