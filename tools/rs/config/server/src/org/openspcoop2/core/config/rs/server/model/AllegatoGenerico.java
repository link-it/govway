package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AllegatoGenerico  {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private byte[] documento = null;
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

  public AllegatoGenerico nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get documento
   * @return documento
  **/
  @JsonProperty("documento")
  @NotNull
  public byte[] getDocumento() {
    return this.documento;
  }

  public void setDocumento(byte[] documento) {
    this.documento = documento;
  }

  public AllegatoGenerico documento(byte[] documento) {
    this.documento = documento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllegatoGenerico {\n");
    
    sb.append("    nome: ").append(AllegatoGenerico.toIndentedString(this.nome)).append("\n");
    sb.append("    documento: ").append(AllegatoGenerico.toIndentedString(this.documento)).append("\n");
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
