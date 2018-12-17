package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIImplAutenticazione  {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneEnum tipo = null;
  
  @Schema(description = "")
  private Boolean opzionale = false;
  
  @Schema(description = "nome autenticazione 'custom', set tipo='custom'")
 /**
   * nome autenticazione 'custom', set tipo='custom'  
  **/
  private String nome = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  public TipoAutenticazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutenticazione tipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get opzionale
   * @return opzionale
  **/
  @JsonProperty("opzionale")
  public Boolean isOpzionale() {
    return this.opzionale;
  }

  public void setOpzionale(Boolean opzionale) {
    this.opzionale = opzionale;
  }

  public APIImplAutenticazione opzionale(Boolean opzionale) {
    this.opzionale = opzionale;
    return this;
  }

 /**
   * nome autenticazione &#x27;custom&#x27;, set tipo&#x3D;&#x27;custom&#x27;
   * @return nome
  **/
  @JsonProperty("nome")
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public APIImplAutenticazione nome(String nome) {
    this.nome = nome;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazione {\n");
    
    sb.append("    tipo: ").append(APIImplAutenticazione.toIndentedString(this.tipo)).append("\n");
    sb.append("    opzionale: ").append(APIImplAutenticazione.toIndentedString(this.opzionale)).append("\n");
    sb.append("    nome: ").append(APIImplAutenticazione.toIndentedString(this.nome)).append("\n");
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
