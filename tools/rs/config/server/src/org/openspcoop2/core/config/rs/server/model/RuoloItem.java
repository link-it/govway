package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RuoloItem  {
  
  @Schema(example = "AccessoSolaLettura", required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private FonteEnum fonte = null;
  
  @Schema(required = true, description = "")
  private ContestoEnum contesto = null;
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

  public RuoloItem nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get fonte
   * @return fonte
  **/
  @JsonProperty("fonte")
  @NotNull
  public FonteEnum getFonte() {
    return this.fonte;
  }

  public void setFonte(FonteEnum fonte) {
    this.fonte = fonte;
  }

  public RuoloItem fonte(FonteEnum fonte) {
    this.fonte = fonte;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  @NotNull
  public ContestoEnum getContesto() {
    return this.contesto;
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public RuoloItem contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuoloItem {\n");
    
    sb.append("    nome: ").append(RuoloItem.toIndentedString(this.nome)).append("\n");
    sb.append("    fonte: ").append(RuoloItem.toIndentedString(this.fonte)).append("\n");
    sb.append("    contesto: ").append(RuoloItem.toIndentedString(this.contesto)).append("\n");
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
