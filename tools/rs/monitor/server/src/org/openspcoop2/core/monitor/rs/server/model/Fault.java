package org.openspcoop2.core.monitor.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Fault  {
  
  @Schema(required = true, description = "Codice dell'errore riscontrato")
 /**
   * Codice dell'errore riscontrato  
  **/
  private String codice = null;
  
  @Schema(required = true, description = "Descrizione dell'errore")
 /**
   * Descrizione dell'errore  
  **/
  private String descrizione = null;
  
  @Schema(description = "Dettagli aggiuntivi sull'errore")
 /**
   * Dettagli aggiuntivi sull'errore  
  **/
  private String dettaglio = null;
 /**
   * Codice dell&#x27;errore riscontrato
   * @return codice
  **/
  @JsonProperty("codice")
  @NotNull
  @Valid
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public Fault codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Descrizione dell&#x27;errore
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @NotNull
  @Valid
  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public Fault descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Dettagli aggiuntivi sull&#x27;errore
   * @return dettaglio
  **/
  @JsonProperty("dettaglio")
  @Valid
  public String getDettaglio() {
    return this.dettaglio;
  }

  public void setDettaglio(String dettaglio) {
    this.dettaglio = dettaglio;
  }

  public Fault dettaglio(String dettaglio) {
    this.dettaglio = dettaglio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Fault {\n");
    
    sb.append("    codice: ").append(Fault.toIndentedString(this.codice)).append("\n");
    sb.append("    descrizione: ").append(Fault.toIndentedString(this.descrizione)).append("\n");
    sb.append("    dettaglio: ").append(Fault.toIndentedString(this.dettaglio)).append("\n");
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
