/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.UUID;
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
 @XmlType(name = "TransazioneBase", propOrder =
    { "emittente", "dataEmissione", "idTraccia", "idConversazione", "ruolo", "esito"
})

@XmlRootElement(name="TransazioneBase")
public class TransazioneBase  {
  @XmlElement(name="emittente", required = true)
  
  @Schema(required = true, description = "")
  private String emittente = null;
  @XmlElement(name="data_emissione", required = true)
  
  @Schema(required = true, description = "")
  private DateTime dataEmissione = null;
  @XmlElement(name="id_traccia", required = true)
  
  @Schema(required = true, description = "")
  private UUID idTraccia = null;
  @XmlElement(name="id_conversazione")
  
  @Schema(example = "eba4355e-403f-4e75-8d56-0751710409c2", description = "")
  private String idConversazione = null;
  @XmlElement(name="ruolo", required = true)
  
  @Schema(required = true, description = "")
  private TransazioneRuoloEnum ruolo = null;
  @XmlElement(name="esito", required = true)
  
  @Schema(required = true, description = "")
  private TransazioneEsito esito = null;
 /**
   * Get emittente
   * @return emittente
  **/
  @JsonProperty("emittente")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255)  public String getEmittente() {
    return this.emittente;
  }

  public void setEmittente(String emittente) {
    this.emittente = emittente;
  }

  public TransazioneBase emittente(String emittente) {
    this.emittente = emittente;
    return this;
  }

 /**
   * Get dataEmissione
   * @return dataEmissione
  **/
  @JsonProperty("data_emissione")
  @NotNull
  @Valid
  public DateTime getDataEmissione() {
    return this.dataEmissione;
  }

  public void setDataEmissione(DateTime dataEmissione) {
    this.dataEmissione = dataEmissione;
  }

  public TransazioneBase dataEmissione(DateTime dataEmissione) {
    this.dataEmissione = dataEmissione;
    return this;
  }

 /**
   * Get idTraccia
   * @return idTraccia
  **/
  @JsonProperty("id_traccia")
  @NotNull
  @Valid
  public UUID getIdTraccia() {
    return this.idTraccia;
  }

  public void setIdTraccia(UUID idTraccia) {
    this.idTraccia = idTraccia;
  }

  public TransazioneBase idTraccia(UUID idTraccia) {
    this.idTraccia = idTraccia;
    return this;
  }

 /**
   * Get idConversazione
   * @return idConversazione
  **/
  @JsonProperty("id_conversazione")
  @Valid
  public String getIdConversazione() {
    return this.idConversazione;
  }

  public void setIdConversazione(String idConversazione) {
    this.idConversazione = idConversazione;
  }

  public TransazioneBase idConversazione(String idConversazione) {
    this.idConversazione = idConversazione;
    return this;
  }

 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @NotNull
  @Valid
  public TransazioneRuoloEnum getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(TransazioneRuoloEnum ruolo) {
    this.ruolo = ruolo;
  }

  public TransazioneBase ruolo(TransazioneRuoloEnum ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @NotNull
  @Valid
  public TransazioneEsito getEsito() {
    return this.esito;
  }

  public void setEsito(TransazioneEsito esito) {
    this.esito = esito;
  }

  public TransazioneBase esito(TransazioneEsito esito) {
    this.esito = esito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneBase {\n");
    
    sb.append("    emittente: ").append(TransazioneBase.toIndentedString(this.emittente)).append("\n");
    sb.append("    dataEmissione: ").append(TransazioneBase.toIndentedString(this.dataEmissione)).append("\n");
    sb.append("    idTraccia: ").append(TransazioneBase.toIndentedString(this.idTraccia)).append("\n");
    sb.append("    idConversazione: ").append(TransazioneBase.toIndentedString(this.idConversazione)).append("\n");
    sb.append("    ruolo: ").append(TransazioneBase.toIndentedString(this.ruolo)).append("\n");
    sb.append("    esito: ").append(TransazioneBase.toIndentedString(this.esito)).append("\n");
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
