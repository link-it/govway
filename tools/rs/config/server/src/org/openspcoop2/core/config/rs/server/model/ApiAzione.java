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
package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.utils.service.beans.ProfiloCollaborazioneEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiAzione  {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private Boolean profiloRidefinito = false;
  
  @Schema(description = "")
  private ProfiloCollaborazioneEnum profiloCollaborazione = null;
  
  @Schema(example = "false", description = "")
  private Boolean idCollaborazione = false;
  
  @Schema(example = "false", description = "")
  private Boolean riferimentoIdRichiesta = null;
  
  @Schema(description = "")
  private ApiModIAzioneSoap modi = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ApiAzione nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get profiloRidefinito
   * @return profiloRidefinito
  **/
  @JsonProperty("profilo_ridefinito")
  @NotNull
  @Valid
  public Boolean isProfiloRidefinito() {
    return this.profiloRidefinito;
  }

  public void setProfiloRidefinito(Boolean profiloRidefinito) {
    this.profiloRidefinito = profiloRidefinito;
  }

  public ApiAzione profiloRidefinito(Boolean profiloRidefinito) {
    this.profiloRidefinito = profiloRidefinito;
    return this;
  }

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

  public ApiAzione profiloCollaborazione(ProfiloCollaborazioneEnum profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
    return this;
  }

 /**
   * Get idCollaborazione
   * @return idCollaborazione
  **/
  @JsonProperty("id_collaborazione")
  @Valid
  public Boolean isIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(Boolean idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public ApiAzione idCollaborazione(Boolean idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
    return this;
  }

 /**
   * Get riferimentoIdRichiesta
   * @return riferimentoIdRichiesta
  **/
  @JsonProperty("riferimento_id_richiesta")
  @Valid
  public Boolean isRiferimentoIdRichiesta() {
    return this.riferimentoIdRichiesta;
  }

  public void setRiferimentoIdRichiesta(Boolean riferimentoIdRichiesta) {
    this.riferimentoIdRichiesta = riferimentoIdRichiesta;
  }

  public ApiAzione riferimentoIdRichiesta(Boolean riferimentoIdRichiesta) {
    this.riferimentoIdRichiesta = riferimentoIdRichiesta;
    return this;
  }

 /**
   * Get modi
   * @return modi
  **/
  @JsonProperty("modi")
  @Valid
  public ApiModIAzioneSoap getModi() {
    return this.modi;
  }

  public void setModi(ApiModIAzioneSoap modi) {
    this.modi = modi;
  }

  public ApiAzione modi(ApiModIAzioneSoap modi) {
    this.modi = modi;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiAzione {\n");
    
    sb.append("    nome: ").append(ApiAzione.toIndentedString(this.nome)).append("\n");
    sb.append("    profiloRidefinito: ").append(ApiAzione.toIndentedString(this.profiloRidefinito)).append("\n");
    sb.append("    profiloCollaborazione: ").append(ApiAzione.toIndentedString(this.profiloCollaborazione)).append("\n");
    sb.append("    idCollaborazione: ").append(ApiAzione.toIndentedString(this.idCollaborazione)).append("\n");
    sb.append("    riferimentoIdRichiesta: ").append(ApiAzione.toIndentedString(this.riferimentoIdRichiesta)).append("\n");
    sb.append("    modi: ").append(ApiAzione.toIndentedString(this.modi)).append("\n");
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
