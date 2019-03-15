/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.service.beans;

import org.joda.time.DateTime;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioRispostaDatiTrasporto", propOrder =
    { "dataUscita", "codiceRisposta"
})

@XmlRootElement(name="TransazioneDettaglioRispostaDatiTrasporto")
public class TransazioneDettaglioRispostaDatiTrasporto  {
  @XmlElement(name="data_uscita", required = true)
  
  @Schema(required = true, description = "")
  private DateTime dataUscita = null;
  @XmlElement(name="codice_risposta", required = true)
  
  @Schema(example = "200", required = true, description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String codiceRisposta = null;
 /**
   * Get dataUscita
   * @return dataUscita
  **/
  @JsonProperty("data_uscita")
  @NotNull
  @Valid
  public DateTime getDataUscita() {
    return this.dataUscita;
  }

  public void setDataUscita(DateTime dataUscita) {
    this.dataUscita = dataUscita;
  }

  public TransazioneDettaglioRispostaDatiTrasporto dataUscita(DateTime dataUscita) {
    this.dataUscita = dataUscita;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return codiceRisposta
  **/
  @JsonProperty("codice_risposta")
  @NotNull
  @Valid
  public String getCodiceRisposta() {
    return this.codiceRisposta;
  }

  public void setCodiceRisposta(String codiceRisposta) {
    this.codiceRisposta = codiceRisposta;
  }

  public TransazioneDettaglioRispostaDatiTrasporto codiceRisposta(String codiceRisposta) {
    this.codiceRisposta = codiceRisposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioRispostaDatiTrasporto {\n");
    
    sb.append("    dataUscita: ").append(TransazioneDettaglioRispostaDatiTrasporto.toIndentedString(this.dataUscita)).append("\n");
    sb.append("    codiceRisposta: ").append(TransazioneDettaglioRispostaDatiTrasporto.toIndentedString(this.codiceRisposta)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
