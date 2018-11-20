package org.openspcoop2.core.monitor.rs.server.model;

import java.util.UUID;
import org.joda.time.DateTime;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Traccia  {
  
  @Schema(example = "PROXY", description = "")
  private String tipoMittente = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String nomeMittente = null;
  
  @Schema(example = "EnteInternoPdD", description = "")
  private String codicePortaMittente = null;
  
  @Schema(example = "PROXY", description = "")
  private String tipoDestinatario = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String nomeDestinatario = null;
  
  @Schema(example = "EnteEsternoPdD", description = "")
  private String codicePortaDestinatario = null;
  
  @Schema(example = "oneway", description = "")
  private String profilo = null;
  
  @Schema(example = "idCollab", description = "")
  private String collaborazione = null;
  
  @Schema(example = "PROXY", description = "")
  private String tipoServizio = null;
  
  @Schema(example = "servizio", description = "")
  private String nomeServizio = null;
  
  @Schema(example = "1", description = "")
  private Integer versioneServizio = null;
  
  @Schema(example = "azione", description = "")
  private String azione = null;
  
  @Schema(example = "0998f497-e05d-420a-a6b0-ff3bb718d2c4", description = "")
  private UUID idMessaggio = null;
  
  @Schema(example = "df1d0806-80f3-4c53-80b8-330999989798", description = "")
  private UUID riferimentoMessaggio = null;
  
  @Schema(example = "sincronizzato", description = "")
  private String sorgenteTemporale = null;
  
  @Schema(description = "")
  private DateTime oraRegistrazione = null;
  
  @Schema(example = "30", description = "")
  private String scadenza = null;
 /**
   * Get tipoMittente
   * @return tipoMittente
  **/
  @JsonProperty("tipoMittente")
  public String getTipoMittente() {
    return this.tipoMittente;
  }

  public void setTipoMittente(String tipoMittente) {
    this.tipoMittente = tipoMittente;
  }

  public Traccia tipoMittente(String tipoMittente) {
    this.tipoMittente = tipoMittente;
    return this;
  }

 /**
   * Get nomeMittente
   * @return nomeMittente
  **/
  @JsonProperty("nomeMittente")
  public String getNomeMittente() {
    return this.nomeMittente;
  }

  public void setNomeMittente(String nomeMittente) {
    this.nomeMittente = nomeMittente;
  }

  public Traccia nomeMittente(String nomeMittente) {
    this.nomeMittente = nomeMittente;
    return this;
  }

 /**
   * Get codicePortaMittente
   * @return codicePortaMittente
  **/
  @JsonProperty("codicePortaMittente")
  public String getCodicePortaMittente() {
    return this.codicePortaMittente;
  }

  public void setCodicePortaMittente(String codicePortaMittente) {
    this.codicePortaMittente = codicePortaMittente;
  }

  public Traccia codicePortaMittente(String codicePortaMittente) {
    this.codicePortaMittente = codicePortaMittente;
    return this;
  }

 /**
   * Get tipoDestinatario
   * @return tipoDestinatario
  **/
  @JsonProperty("tipoDestinatario")
  public String getTipoDestinatario() {
    return this.tipoDestinatario;
  }

  public void setTipoDestinatario(String tipoDestinatario) {
    this.tipoDestinatario = tipoDestinatario;
  }

  public Traccia tipoDestinatario(String tipoDestinatario) {
    this.tipoDestinatario = tipoDestinatario;
    return this;
  }

 /**
   * Get nomeDestinatario
   * @return nomeDestinatario
  **/
  @JsonProperty("nomeDestinatario")
  public String getNomeDestinatario() {
    return this.nomeDestinatario;
  }

  public void setNomeDestinatario(String nomeDestinatario) {
    this.nomeDestinatario = nomeDestinatario;
  }

  public Traccia nomeDestinatario(String nomeDestinatario) {
    this.nomeDestinatario = nomeDestinatario;
    return this;
  }

 /**
   * Get codicePortaDestinatario
   * @return codicePortaDestinatario
  **/
  @JsonProperty("codicePortaDestinatario")
  public String getCodicePortaDestinatario() {
    return this.codicePortaDestinatario;
  }

  public void setCodicePortaDestinatario(String codicePortaDestinatario) {
    this.codicePortaDestinatario = codicePortaDestinatario;
  }

  public Traccia codicePortaDestinatario(String codicePortaDestinatario) {
    this.codicePortaDestinatario = codicePortaDestinatario;
    return this;
  }

 /**
   * Get profilo
   * @return profilo
  **/
  @JsonProperty("profilo")
  public String getProfilo() {
    return this.profilo;
  }

  public void setProfilo(String profilo) {
    this.profilo = profilo;
  }

  public Traccia profilo(String profilo) {
    this.profilo = profilo;
    return this;
  }

 /**
   * Get collaborazione
   * @return collaborazione
  **/
  @JsonProperty("collaborazione")
  public String getCollaborazione() {
    return this.collaborazione;
  }

  public void setCollaborazione(String collaborazione) {
    this.collaborazione = collaborazione;
  }

  public Traccia collaborazione(String collaborazione) {
    this.collaborazione = collaborazione;
    return this;
  }

 /**
   * Get tipoServizio
   * @return tipoServizio
  **/
  @JsonProperty("tipoServizio")
  public String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public Traccia tipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
    return this;
  }

 /**
   * Get nomeServizio
   * @return nomeServizio
  **/
  @JsonProperty("nomeServizio")
  public String getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(String nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public Traccia nomeServizio(String nomeServizio) {
    this.nomeServizio = nomeServizio;
    return this;
  }

 /**
   * Get versioneServizio
   * minimum: 1
   * @return versioneServizio
  **/
  @JsonProperty("versioneServizio")
 @Min(1)  public Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public Traccia versioneServizio(Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public Traccia azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get idMessaggio
   * @return idMessaggio
  **/
  @JsonProperty("idMessaggio")
  public UUID getIdMessaggio() {
    return this.idMessaggio;
  }

  public void setIdMessaggio(UUID idMessaggio) {
    this.idMessaggio = idMessaggio;
  }

  public Traccia idMessaggio(UUID idMessaggio) {
    this.idMessaggio = idMessaggio;
    return this;
  }

 /**
   * Get riferimentoMessaggio
   * @return riferimentoMessaggio
  **/
  @JsonProperty("riferimentoMessaggio")
  public UUID getRiferimentoMessaggio() {
    return this.riferimentoMessaggio;
  }

  public void setRiferimentoMessaggio(UUID riferimentoMessaggio) {
    this.riferimentoMessaggio = riferimentoMessaggio;
  }

  public Traccia riferimentoMessaggio(UUID riferimentoMessaggio) {
    this.riferimentoMessaggio = riferimentoMessaggio;
    return this;
  }

 /**
   * Get sorgenteTemporale
   * @return sorgenteTemporale
  **/
  @JsonProperty("sorgenteTemporale")
  public String getSorgenteTemporale() {
    return this.sorgenteTemporale;
  }

  public void setSorgenteTemporale(String sorgenteTemporale) {
    this.sorgenteTemporale = sorgenteTemporale;
  }

  public Traccia sorgenteTemporale(String sorgenteTemporale) {
    this.sorgenteTemporale = sorgenteTemporale;
    return this;
  }

 /**
   * Get oraRegistrazione
   * @return oraRegistrazione
  **/
  @JsonProperty("oraRegistrazione")
  public DateTime getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(DateTime oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public Traccia oraRegistrazione(DateTime oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
    return this;
  }

 /**
   * Get scadenza
   * @return scadenza
  **/
  @JsonProperty("scadenza")
  public String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(String scadenza) {
    this.scadenza = scadenza;
  }

  public Traccia scadenza(String scadenza) {
    this.scadenza = scadenza;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Traccia {\n");
    
    sb.append("    tipoMittente: ").append(Traccia.toIndentedString(this.tipoMittente)).append("\n");
    sb.append("    nomeMittente: ").append(Traccia.toIndentedString(this.nomeMittente)).append("\n");
    sb.append("    codicePortaMittente: ").append(Traccia.toIndentedString(this.codicePortaMittente)).append("\n");
    sb.append("    tipoDestinatario: ").append(Traccia.toIndentedString(this.tipoDestinatario)).append("\n");
    sb.append("    nomeDestinatario: ").append(Traccia.toIndentedString(this.nomeDestinatario)).append("\n");
    sb.append("    codicePortaDestinatario: ").append(Traccia.toIndentedString(this.codicePortaDestinatario)).append("\n");
    sb.append("    profilo: ").append(Traccia.toIndentedString(this.profilo)).append("\n");
    sb.append("    collaborazione: ").append(Traccia.toIndentedString(this.collaborazione)).append("\n");
    sb.append("    tipoServizio: ").append(Traccia.toIndentedString(this.tipoServizio)).append("\n");
    sb.append("    nomeServizio: ").append(Traccia.toIndentedString(this.nomeServizio)).append("\n");
    sb.append("    versioneServizio: ").append(Traccia.toIndentedString(this.versioneServizio)).append("\n");
    sb.append("    azione: ").append(Traccia.toIndentedString(this.azione)).append("\n");
    sb.append("    idMessaggio: ").append(Traccia.toIndentedString(this.idMessaggio)).append("\n");
    sb.append("    riferimentoMessaggio: ").append(Traccia.toIndentedString(this.riferimentoMessaggio)).append("\n");
    sb.append("    sorgenteTemporale: ").append(Traccia.toIndentedString(this.sorgenteTemporale)).append("\n");
    sb.append("    oraRegistrazione: ").append(Traccia.toIndentedString(this.oraRegistrazione)).append("\n");
    sb.append("    scadenza: ").append(Traccia.toIndentedString(this.scadenza)).append("\n");
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
