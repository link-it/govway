package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseSoggettoItem;
import org.openspcoop2.core.config.rs.server.model.StatoApi;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiItem extends BaseSoggettoItem {
  
  @Schema(required = true, description = "")
  private TipoApiEnum tipo = null;
  
  @Schema(example = "PetStore", required = true, description = "")
  private String nome = null;
  
  @Schema(example = "descrizione API", description = "")
  private String descrizione = null;
  
  @Schema(example = "1", required = true, description = "")
  private Integer versione = null;
  
  @Schema(example = "{\"formato\":\"OpenApi3.0\"}", required = true, description = "")
  private Object formato = null;
  
  @Schema(description = "")
  private byte[] interfaccia = null;
  
  @Schema(required = true, description = "")
  private StatoApi stato = null;
  
  @Schema(required = true, description = "")
  private String statoDescrizione = null;
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

  public ApiItem tipo(TipoApiEnum tipo) {
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

  public ApiItem nome(String nome) {
    this.nome = nome;
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

  public ApiItem descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get versione
   * minimum: 1
   * @return versione
  **/
  @JsonProperty("versione")
  @NotNull
 @Min(1)  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public ApiItem versione(Integer versione) {
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

  public ApiItem formato(Object formato) {
    this.formato = formato;
    return this;
  }

 /**
   * Get interfaccia
   * @return interfaccia
  **/
  @JsonProperty("interfaccia")
  public byte[] getInterfaccia() {
    return this.interfaccia;
  }

  public void setInterfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
  }

  public ApiItem interfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  public StatoApi getStato() {
    return this.stato;
  }

  public void setStato(StatoApi stato) {
    this.stato = stato;
  }

  public ApiItem stato(StatoApi stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get statoDescrizione
   * @return statoDescrizione
  **/
  @JsonProperty("stato_descrizione")
  @NotNull
  public String getStatoDescrizione() {
    return this.statoDescrizione;
  }

  public void setStatoDescrizione(String statoDescrizione) {
    this.statoDescrizione = statoDescrizione;
  }

  public ApiItem statoDescrizione(String statoDescrizione) {
    this.statoDescrizione = statoDescrizione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiItem {\n");
    sb.append("    ").append(ApiItem.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(ApiItem.toIndentedString(this.tipo)).append("\n");
    sb.append("    nome: ").append(ApiItem.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(ApiItem.toIndentedString(this.descrizione)).append("\n");
    sb.append("    versione: ").append(ApiItem.toIndentedString(this.versione)).append("\n");
    sb.append("    formato: ").append(ApiItem.toIndentedString(this.formato)).append("\n");
    sb.append("    interfaccia: ").append(ApiItem.toIndentedString(this.interfaccia)).append("\n");
    sb.append("    stato: ").append(ApiItem.toIndentedString(this.stato)).append("\n");
    sb.append("    statoDescrizione: ").append(ApiItem.toIndentedString(this.statoDescrizione)).append("\n");
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
