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
package it.gov.spcoop.sica.manifest;

import java.io.Serializable;


/** <p>Java class AccordoServizioParteSpecifica.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class AccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String riferimentoParteComune;

  protected SpecificaPortiAccesso specificaPortiAccesso;

  protected SpecificaSicurezza specificaSicurezza;

  protected SpecificaLivelliServizio specificaLivelliServizio;

  protected String adesione;

  protected String erogatore;


  public AccordoServizioParteSpecifica() {
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

  public String getRiferimentoParteComune() {
    if(this.riferimentoParteComune!=null && ("".equals(this.riferimentoParteComune)==false)){
		return this.riferimentoParteComune.trim();
	}else{
		return null;
	}

  }

  public void setRiferimentoParteComune(String riferimentoParteComune) {
    this.riferimentoParteComune = riferimentoParteComune;
  }

  public SpecificaPortiAccesso getSpecificaPortiAccesso() {
    return this.specificaPortiAccesso;
  }

  public void setSpecificaPortiAccesso(SpecificaPortiAccesso specificaPortiAccesso) {
    this.specificaPortiAccesso = specificaPortiAccesso;
  }

  public SpecificaSicurezza getSpecificaSicurezza() {
    return this.specificaSicurezza;
  }

  public void setSpecificaSicurezza(SpecificaSicurezza specificaSicurezza) {
    this.specificaSicurezza = specificaSicurezza;
  }

  public SpecificaLivelliServizio getSpecificaLivelliServizio() {
    return this.specificaLivelliServizio;
  }

  public void setSpecificaLivelliServizio(SpecificaLivelliServizio specificaLivelliServizio) {
    this.specificaLivelliServizio = specificaLivelliServizio;
  }

  public String getAdesione() {
    if(this.adesione!=null && ("".equals(this.adesione)==false)){
		return this.adesione.trim();
	}else{
		return null;
	}

  }

  public void setAdesione(String adesione) {
    this.adesione = adesione;
  }

  public String getErogatore() {
    if(this.erogatore!=null && ("".equals(this.erogatore)==false)){
		return this.erogatore.trim();
	}else{
		return null;
	}

  }

  public void setErogatore(String erogatore) {
    this.erogatore = erogatore;
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

  public static final String RIFERIMENTO_PARTE_COMUNE = "riferimentoParteComune";

  public static final String SPECIFICA_PORTI_ACCESSO = "specificaPortiAccesso";

  public static final String SPECIFICA_SICUREZZA = "specificaSicurezza";

  public static final String SPECIFICA_LIVELLI_SERVIZIO = "specificaLivelliServizio";

  public static final String ADESIONE = "adesione";

  public static final String EROGATORE = "erogatore";

}
