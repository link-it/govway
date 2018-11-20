package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseSoggettoItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicativoItem extends BaseSoggettoItem {
  
  @Schema(example = "applicativo1", required = true, description = "")
  private String nome = null;
  
  @Schema(example = "3", description = "")
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

  public ApplicativoItem nome(String nome) {
    this.nome = nome;
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

  public ApplicativoItem countRuoli(Integer countRuoli) {
    this.countRuoli = countRuoli;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicativoItem {\n");
    sb.append("    ").append(ApplicativoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(ApplicativoItem.toIndentedString(this.nome)).append("\n");
    sb.append("    countRuoli: ").append(ApplicativoItem.toIndentedString(this.countRuoli)).append("\n");
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
