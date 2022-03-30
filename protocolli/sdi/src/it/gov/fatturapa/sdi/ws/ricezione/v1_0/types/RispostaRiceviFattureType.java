/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.constants.EsitoRicezioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for rispostaRiceviFatture_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rispostaRiceviFatture_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Esito" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}esitoRicezione_Type" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "rispostaRiceviFatture_Type", 
  propOrder = {
  	"esito"
  }
)

@XmlRootElement(name = "rispostaRiceviFatture_Type")

public class RispostaRiceviFattureType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RispostaRiceviFattureType() {
  }

  public void set_value_esito(String value) {
    this.esito = (EsitoRicezioneType) EsitoRicezioneType.toEnumConstantFromString(value);
  }

  public String get_value_esito() {
    if(this.esito == null){
    	return null;
    }else{
    	return this.esito.toString();
    }
  }

  public it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.constants.EsitoRicezioneType getEsito() {
    return this.esito;
  }

  public void setEsito(it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.constants.EsitoRicezioneType esito) {
    this.esito = esito;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.RispostaRiceviFattureTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaRiceviFattureType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaRiceviFattureType.modelStaticInstance = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.RispostaRiceviFattureTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.RispostaRiceviFattureTypeModel model(){
	  if(it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaRiceviFattureType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaRiceviFattureType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_esito;

  @XmlElement(name="Esito",required=true,nillable=false)
  protected EsitoRicezioneType esito;

}
