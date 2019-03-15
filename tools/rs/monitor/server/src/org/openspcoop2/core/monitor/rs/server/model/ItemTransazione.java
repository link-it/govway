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
package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneBase;
import org.openspcoop2.utils.service.beans.TransazioneContestoEnum;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioRichiestaBase;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioRispostaBase;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniApiBase;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniMittenteBase;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniSoggetto;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ItemTransazione extends TransazioneBase {
  
  @Schema(required = true, description = "")
  private ProfiloEnum profilo = null;
  
  @Schema(required = true, description = "")
  private TransazioneContestoEnum contesto = null;
  
  @Schema(description = "")
  private String idCluster = null;
  
  @Schema(description = "")
  private TransazioneExtInformazioniSoggetto informazioniEmittente = null;
  
  @Schema(description = "")
  private String stato = null;
  
  @Schema(required = true, description = "")
  private DateTime data = null;
  
  @Schema(example = "8", description = "")
  private Long latenzaServizio = null;
  
  @Schema(example = "20", description = "")
  private Long latenzaTotale = null;
  
  @Schema(description = "")
  private TransazioneExtDettaglioRichiestaBase richiesta = null;
  
  @Schema(description = "")
  private TransazioneExtDettaglioRispostaBase risposta = null;
  
  @Schema(description = "")
  private TransazioneExtInformazioniApiBase api = null;
  
  @Schema(description = "")
  private TransazioneExtInformazioniMittenteBase mittente = null;
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

  public ItemTransazione profilo(ProfiloEnum profilo) {
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

  public ItemTransazione contesto(TransazioneContestoEnum contesto) {
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

  public ItemTransazione idCluster(String idCluster) {
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

  public ItemTransazione informazioniEmittente(TransazioneExtInformazioniSoggetto informazioniEmittente) {
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

  public ItemTransazione stato(String stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get data
   * @return data
  **/
  @JsonProperty("data")
  @NotNull
  @Valid
  public DateTime getData() {
    return this.data;
  }

  public void setData(DateTime data) {
    this.data = data;
  }

  public ItemTransazione data(DateTime data) {
    this.data = data;
    return this;
  }

 /**
   * Get latenzaServizio
   * @return latenzaServizio
  **/
  @JsonProperty("latenza_servizio")
  @Valid
  public Long getLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  public ItemTransazione latenzaServizio(Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
    return this;
  }

 /**
   * Get latenzaTotale
   * @return latenzaTotale
  **/
  @JsonProperty("latenza_totale")
  @Valid
  public Long getLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public ItemTransazione latenzaTotale(Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
    return this;
  }

 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @Valid
  public TransazioneExtDettaglioRichiestaBase getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(TransazioneExtDettaglioRichiestaBase richiesta) {
    this.richiesta = richiesta;
  }

  public ItemTransazione richiesta(TransazioneExtDettaglioRichiestaBase richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @Valid
  public TransazioneExtDettaglioRispostaBase getRisposta() {
    return this.risposta;
  }

  public void setRisposta(TransazioneExtDettaglioRispostaBase risposta) {
    this.risposta = risposta;
  }

  public ItemTransazione risposta(TransazioneExtDettaglioRispostaBase risposta) {
    this.risposta = risposta;
    return this;
  }

 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @Valid
  public TransazioneExtInformazioniApiBase getApi() {
    return this.api;
  }

  public void setApi(TransazioneExtInformazioniApiBase api) {
    this.api = api;
  }

  public ItemTransazione api(TransazioneExtInformazioniApiBase api) {
    this.api = api;
    return this;
  }

 /**
   * Get mittente
   * @return mittente
  **/
  @JsonProperty("mittente")
  @Valid
  public TransazioneExtInformazioniMittenteBase getMittente() {
    return this.mittente;
  }

  public void setMittente(TransazioneExtInformazioniMittenteBase mittente) {
    this.mittente = mittente;
  }

  public ItemTransazione mittente(TransazioneExtInformazioniMittenteBase mittente) {
    this.mittente = mittente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ItemTransazione {\n");
    sb.append("    ").append(ItemTransazione.toIndentedString(super.toString())).append("\n");
    sb.append("    profilo: ").append(ItemTransazione.toIndentedString(this.profilo)).append("\n");
    sb.append("    contesto: ").append(ItemTransazione.toIndentedString(this.contesto)).append("\n");
    sb.append("    idCluster: ").append(ItemTransazione.toIndentedString(this.idCluster)).append("\n");
    sb.append("    informazioniEmittente: ").append(ItemTransazione.toIndentedString(this.informazioniEmittente)).append("\n");
    sb.append("    stato: ").append(ItemTransazione.toIndentedString(this.stato)).append("\n");
    sb.append("    data: ").append(ItemTransazione.toIndentedString(this.data)).append("\n");
    sb.append("    latenzaServizio: ").append(ItemTransazione.toIndentedString(this.latenzaServizio)).append("\n");
    sb.append("    latenzaTotale: ").append(ItemTransazione.toIndentedString(this.latenzaTotale)).append("\n");
    sb.append("    richiesta: ").append(ItemTransazione.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(ItemTransazione.toIndentedString(this.risposta)).append("\n");
    sb.append("    api: ").append(ItemTransazione.toIndentedString(this.api)).append("\n");
    sb.append("    mittente: ").append(ItemTransazione.toIndentedString(this.mittente)).append("\n");
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
