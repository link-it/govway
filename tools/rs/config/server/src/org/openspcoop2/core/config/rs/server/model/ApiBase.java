package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiBase  {
  
  @Schema(description = "")
  private String referente = null;
  
  @Schema(required = true, description = "")
  private TipoApiEnum tipo = null;
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(example = "descrizione API", description = "")
  private String descrizione = null;
  
  @Schema(required = true, description = "")
  private Integer versione = null;
  
  @Schema(example = "{\"formato\":\"OpenApi3.0\"}", required = true, description = "")
  private Object formato = null;
 /**
   * Get referente
   * @return referente
  **/
  @JsonProperty("referente")
  public String getReferente() {
    return this.referente;
  }

  public void setReferente(String referente) {
    this.referente = referente;
  }

  public ApiBase referente(String referente) {
    this.referente = referente;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  public TipoApiEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoApiEnum tipo) {
    this.tipo = tipo;
  }

  public ApiBase tipo(TipoApiEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ApiBase nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
 @Size(max=255)  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public ApiBase descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get versione
   * @return versione
  **/
  @JsonProperty("versione")
  @NotNull
  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public ApiBase versione(Integer versione) {
    this.versione = versione;
    return this;
  }

 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  @NotNull
  public Object getFormato() {
    return this.formato;
  }

  public void setFormato(Object formato) {
    this.formato = formato;
  }

  public ApiBase formato(Object formato) {
    this.formato = formato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiBase {\n");
    
    sb.append("    referente: ").append(ApiBase.toIndentedString(this.referente)).append("\n");
    sb.append("    tipo: ").append(ApiBase.toIndentedString(this.tipo)).append("\n");
    sb.append("    nome: ").append(ApiBase.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(ApiBase.toIndentedString(this.descrizione)).append("\n");
    sb.append("    versione: ").append(ApiBase.toIndentedString(this.versione)).append("\n");
    sb.append("    formato: ").append(ApiBase.toIndentedString(this.formato)).append("\n");
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
