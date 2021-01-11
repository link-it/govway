/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtInformazioniApi", propOrder =
    { "profiloCollaborazione", "idAsincrono"
})


public class TransazioneExtInformazioniApi extends TransazioneExtInformazioniApiBase {
  @XmlElement(name="profilo_collaborazione")
  
  @Schema(description = "")
  private ProfiloCollaborazioneEnum profiloCollaborazione = null;
  @XmlElement(name="id_asincrono")
  
  @Schema(description = "")
  private String idAsincrono = null;
 /**
   * Get profiloCollaborazione
   * @return profiloCollaborazione
  **/
  @JsonProperty("profilo_collaborazione")
  @Valid
  public ProfiloCollaborazioneEnum getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(ProfiloCollaborazioneEnum profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public TransazioneExtInformazioniApi profiloCollaborazione(ProfiloCollaborazioneEnum profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
    return this;
  }

 /**
   * Get idAsincrono
   * @return idAsincrono
  **/
  @JsonProperty("id_asincrono")
  @Valid
  public String getIdAsincrono() {
    return this.idAsincrono;
  }

  public void setIdAsincrono(String idAsincrono) {
    this.idAsincrono = idAsincrono;
  }

  public TransazioneExtInformazioniApi idAsincrono(String idAsincrono) {
    this.idAsincrono = idAsincrono;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniApi {\n");
    sb.append("    ").append(TransazioneExtInformazioniApi.toIndentedString(super.toString())).append("\n");
    sb.append("    profiloCollaborazione: ").append(TransazioneExtInformazioniApi.toIndentedString(this.profiloCollaborazione)).append("\n");
    sb.append("    idAsincrono: ").append(TransazioneExtInformazioniApi.toIndentedString(this.idAsincrono)).append("\n");
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
