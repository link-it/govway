package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseItem;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SoggettoItem extends BaseItem {
  
  @Schema(example = "EnteEsterno", required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private DominioEnum dominio = null;
  
  @Schema(example = "0", description = "")
  private Integer countRuoli = null;
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

  public SoggettoItem nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get dominio
   * @return dominio
  **/
  @JsonProperty("dominio")
  @NotNull
  public DominioEnum getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioEnum dominio) {
    this.dominio = dominio;
  }

  public SoggettoItem dominio(DominioEnum dominio) {
    this.dominio = dominio;
    return this;
  }

 /**
   * Get countRuoli
   * minimum: 0
   * @return countRuoli
  **/
  @JsonProperty("count_ruoli")
 @Min(0)  public Integer getCountRuoli() {
    return this.countRuoli;
  }

  public void setCountRuoli(Integer countRuoli) {
    this.countRuoli = countRuoli;
  }

  public SoggettoItem countRuoli(Integer countRuoli) {
    this.countRuoli = countRuoli;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SoggettoItem {\n");
    sb.append("    ").append(SoggettoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(SoggettoItem.toIndentedString(this.nome)).append("\n");
    sb.append("    dominio: ").append(SoggettoItem.toIndentedString(this.dominio)).append("\n");
    sb.append("    countRuoli: ").append(SoggettoItem.toIndentedString(this.countRuoli)).append("\n");
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
