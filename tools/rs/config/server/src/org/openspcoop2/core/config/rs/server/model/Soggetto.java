package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import org.openspcoop2.core.config.rs.server.model.BaseCredenziali;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Soggetto extends BaseCredenziali {
  
  @Schema(example = "EnteEsterno", required = true, description = "")
  private String nome = null;
  
  @Schema(description = "")
  private DominioEnum dominio = null;
  
  @Schema(example = "descrizione del soggetto EnteEsterno", description = "")
  private String descrizione = null;
  
  @Schema(example = "[\"ruolo1\",\"ruolo2\"]", description = "")
  private List<String> ruoli = null;
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

  public Soggetto nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get dominio
   * @return dominio
  **/
  @JsonProperty("dominio")
  public DominioEnum getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioEnum dominio) {
    this.dominio = dominio;
  }

  public Soggetto dominio(DominioEnum dominio) {
    this.dominio = dominio;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public Soggetto descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get ruoli
   * @return ruoli
  **/
  @JsonProperty("ruoli")
  public List<String> getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(List<String> ruoli) {
    this.ruoli = ruoli;
  }

  public Soggetto ruoli(List<String> ruoli) {
    this.ruoli = ruoli;
    return this;
  }

  public Soggetto addRuoliItem(String ruoliItem) {
    this.ruoli.add(ruoliItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Soggetto {\n");
    sb.append("    ").append(Soggetto.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(Soggetto.toIndentedString(this.nome)).append("\n");
    sb.append("    dominio: ").append(Soggetto.toIndentedString(this.dominio)).append("\n");
    sb.append("    descrizione: ").append(Soggetto.toIndentedString(this.descrizione)).append("\n");
    sb.append("    ruoli: ").append(Soggetto.toIndentedString(this.ruoli)).append("\n");
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
