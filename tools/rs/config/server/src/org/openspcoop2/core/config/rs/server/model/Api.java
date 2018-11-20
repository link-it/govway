package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Api  {
  
  @Schema(description = "")
  private TipoApiEnum tipo = null;
  
  @Schema(example = "PetStore", required = true, description = "")
  private String nome = null;
  
  @Schema(example = "descrizione API", description = "")
  private String descrizione = null;
  
  @Schema(example = "1", description = "")
  private Integer versione = 1;
  
  @Schema(example = "{\"formato\":\"OpenApi3.0\"}", description = "")
  private Object formato = null;
  
  @Schema(description = "")
  private byte[] interfaccia = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  public TipoApiEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoApiEnum tipo) {
    this.tipo = tipo;
  }

  public Api tipo(TipoApiEnum tipo) {
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

  public Api nome(String nome) {
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

  public Api descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get versione
   * minimum: 1
   * @return versione
  **/
  @JsonProperty("versione")
 @Min(1)  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public Api versione(Integer versione) {
    this.versione = versione;
    return this;
  }

 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  public Object getFormato() {
    return this.formato;
  }

  public void setFormato(Object formato) {
    this.formato = formato;
  }

  public Api formato(Object formato) {
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

  public Api interfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Api {\n");
    
    sb.append("    tipo: ").append(Api.toIndentedString(this.tipo)).append("\n");
    sb.append("    nome: ").append(Api.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(Api.toIndentedString(this.descrizione)).append("\n");
    sb.append("    versione: ").append(Api.toIndentedString(this.versione)).append("\n");
    sb.append("    formato: ").append(Api.toIndentedString(this.formato)).append("\n");
    sb.append("    interfaccia: ").append(Api.toIndentedString(this.interfaccia)).append("\n");
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
