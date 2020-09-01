/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExt", propOrder =
    { "profilo", "contesto", "idCluster", "informazioniEmittente", "stato", "richiesta", "risposta", "api", "mittente", "diagnostici"
})


public class TransazioneExt extends TransazioneBase {
  @XmlElement(name="profilo", required = true)
  
  @Schema(required = true, description = "")
  private ProfiloEnum profilo = null;
  @XmlElement(name="contesto", required = true)
  
  @Schema(required = true, description = "")
  private TransazioneContestoEnum contesto = null;
  @XmlElement(name="id_cluster")
  
  @Schema(description = "")
  private String idCluster = null;
  @XmlElement(name="informazioni_emittente")
  
  @Schema(description = "")
  private TransazioneExtInformazioniSoggetto informazioniEmittente = null;
  @XmlElement(name="stato")
  
  @Schema(description = "")
  private String stato = null;
  @XmlElement(name="richiesta")
  
  @Schema(description = "")
  private TransazioneExtDettaglioRichiesta richiesta = null;
  @XmlElement(name="risposta")
  
  @Schema(description = "")
  private TransazioneExtDettaglioRisposta risposta = null;
  @XmlElement(name="api")
  
  @Schema(description = "")
  private TransazioneExtInformazioniApi api = null;
  @XmlElement(name="mittente")
  
  @Schema(description = "")
  private TransazioneExtInformazioniMittente mittente = null;
  @XmlElement(name="diagnostici")
  
  @Schema(description = "")
  private List<Diagnostico> diagnostici = null;
 /**
   * Get profilo
   * @return profilo
  **/
  @JsonProperty("profilo")
  @NotNull
  @Valid
  public ProfiloEnum getProfilo() {
    return this.profilo;
  }

  public void setProfilo(ProfiloEnum profilo) {
    this.profilo = profilo;
  }

  public TransazioneExt profilo(ProfiloEnum profilo) {
    this.profilo = profilo;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  @NotNull
  @Valid
  public TransazioneContestoEnum getContesto() {
    return this.contesto;
  }

  public void setContesto(TransazioneContestoEnum contesto) {
    this.contesto = contesto;
  }

  public TransazioneExt contesto(TransazioneContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get idCluster
   * @return idCluster
  **/
  @JsonProperty("id_cluster")
  @Valid
  public String getIdCluster() {
    return this.idCluster;
  }

  public void setIdCluster(String idCluster) {
    this.idCluster = idCluster;
  }

  public TransazioneExt idCluster(String idCluster) {
    this.idCluster = idCluster;
    return this;
  }

 /**
   * Get informazioniEmittente
   * @return informazioniEmittente
  **/
  @JsonProperty("informazioni_emittente")
  @Valid
  public TransazioneExtInformazioniSoggetto getInformazioniEmittente() {
    return this.informazioniEmittente;
  }

  public void setInformazioniEmittente(TransazioneExtInformazioniSoggetto informazioniEmittente) {
    this.informazioniEmittente = informazioniEmittente;
  }

  public TransazioneExt informazioniEmittente(TransazioneExtInformazioniSoggetto informazioniEmittente) {
    this.informazioniEmittente = informazioniEmittente;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @Valid
  public String getStato() {
    return this.stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public TransazioneExt stato(String stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @Valid
  public TransazioneExtDettaglioRichiesta getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(TransazioneExtDettaglioRichiesta richiesta) {
    this.richiesta = richiesta;
  }

  public TransazioneExt richiesta(TransazioneExtDettaglioRichiesta richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @Valid
  public TransazioneExtDettaglioRisposta getRisposta() {
    return this.risposta;
  }

  public void setRisposta(TransazioneExtDettaglioRisposta risposta) {
    this.risposta = risposta;
  }

  public TransazioneExt risposta(TransazioneExtDettaglioRisposta risposta) {
    this.risposta = risposta;
    return this;
  }

 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @Valid
  public TransazioneExtInformazioniApi getApi() {
    return this.api;
  }

  public void setApi(TransazioneExtInformazioniApi api) {
    this.api = api;
  }

  public TransazioneExt api(TransazioneExtInformazioniApi api) {
    this.api = api;
    return this;
  }

 /**
   * Get mittente
   * @return mittente
  **/
  @JsonProperty("mittente")
  @Valid
  public TransazioneExtInformazioniMittente getMittente() {
    return this.mittente;
  }

  public void setMittente(TransazioneExtInformazioniMittente mittente) {
    this.mittente = mittente;
  }

  public TransazioneExt mittente(TransazioneExtInformazioniMittente mittente) {
    this.mittente = mittente;
    return this;
  }

 /**
   * Get diagnostici
   * @return diagnostici
  **/
  @JsonProperty("diagnostici")
  @Valid
  public List<Diagnostico> getDiagnostici() {
    return this.diagnostici;
  }

  public void setDiagnostici(List<Diagnostico> diagnostici) {
    this.diagnostici = diagnostici;
  }

  public TransazioneExt diagnostici(List<Diagnostico> diagnostici) {
    this.diagnostici = diagnostici;
    return this;
  }

  public TransazioneExt addDiagnosticiItem(Diagnostico diagnosticiItem) {
    this.diagnostici.add(diagnosticiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExt {\n");
    sb.append("    ").append(TransazioneExt.toIndentedString(super.toString())).append("\n");
    sb.append("    profilo: ").append(TransazioneExt.toIndentedString(this.profilo)).append("\n");
    sb.append("    contesto: ").append(TransazioneExt.toIndentedString(this.contesto)).append("\n");
    sb.append("    idCluster: ").append(TransazioneExt.toIndentedString(this.idCluster)).append("\n");
    sb.append("    informazioniEmittente: ").append(TransazioneExt.toIndentedString(this.informazioniEmittente)).append("\n");
    sb.append("    stato: ").append(TransazioneExt.toIndentedString(this.stato)).append("\n");
    sb.append("    richiesta: ").append(TransazioneExt.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(TransazioneExt.toIndentedString(this.risposta)).append("\n");
    sb.append("    api: ").append(TransazioneExt.toIndentedString(this.api)).append("\n");
    sb.append("    mittente: ").append(TransazioneExt.toIndentedString(this.mittente)).append("\n");
    sb.append("    diagnostici: ").append(TransazioneExt.toIndentedString(this.diagnostici)).append("\n");
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
