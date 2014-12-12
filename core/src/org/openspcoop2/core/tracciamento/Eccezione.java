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


/** <p>Java class Eccezione.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Eccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected CodiceEccezione codice;

  protected ContestoCodificaEccezione contestoCodifica;

  protected String posizione;

  protected RilevanzaEccezione rilevanza;


  public Eccezione() {
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

  public CodiceEccezione getCodice() {
    return this.codice;
  }

  public void setCodice(CodiceEccezione codice) {
    this.codice = codice;
  }

  public ContestoCodificaEccezione getContestoCodifica() {
    return this.contestoCodifica;
  }

  public void setContestoCodifica(ContestoCodificaEccezione contestoCodifica) {
    this.contestoCodifica = contestoCodifica;
  }

  public String getPosizione() {
    if(this.posizione!=null && ("".equals(this.posizione)==false)){
		return this.posizione.trim();
	}else{
		return null;
	}

  }

  public void setPosizione(String posizione) {
    this.posizione = posizione;
  }

  public RilevanzaEccezione getRilevanza() {
    return this.rilevanza;
  }

  public void setRilevanza(RilevanzaEccezione rilevanza) {
    this.rilevanza = rilevanza;
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

  public static final String CODICE = "codice";

  public static final String CONTESTO_CODIFICA = "contestoCodifica";

  public static final String POSIZIONE = "posizione";

  public static final String RILEVANZA = "rilevanza";

}
