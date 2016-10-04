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
package org.openspcoop2.core.eccezione.errore_applicativo;

import java.io.Serializable;
import java.util.Date;


/** <p>Java class ErroreApplicativo.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ErroreApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Dominio dominio;

  protected Date oraRegistrazione;

  protected DatiCooperazione datiCooperazione;

  protected Eccezione eccezione;


  public ErroreApplicativo() {
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

  public DatiCooperazione getDatiCooperazione() {
    return this.datiCooperazione;
  }

  public void setDatiCooperazione(DatiCooperazione datiCooperazione) {
    this.datiCooperazione = datiCooperazione;
  }

  public Eccezione getEccezione() {
    return this.eccezione;
  }

  public void setEccezione(Eccezione eccezione) {
    this.eccezione = eccezione;
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

  public static final String DATI_COOPERAZIONE = "datiCooperazione";

  public static final String ECCEZIONE = "eccezione";

}
