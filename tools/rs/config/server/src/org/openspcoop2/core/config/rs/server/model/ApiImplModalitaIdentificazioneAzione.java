package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ModalitaIdentificazioneAzioneEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiImplModalitaIdentificazioneAzione  {
  
  @Schema(required = true, description = "")
  private ModalitaIdentificazioneAzioneEnum modalita = null;
  
  @Schema(description = "XPath/JsonPath nel caso di modalità 'content-based' o espressione regolare nel caso 'url-based'")
 /**
   * XPath/JsonPath nel caso di modalità 'content-based' o espressione regolare nel caso 'url-based'  
  **/
  private String pattern = null;
  
  @Schema(description = "Nome dell'header http nel caso di modalità 'header-based'")
 /**
   * Nome dell'header http nel caso di modalità 'header-based'  
  **/
  private String nome = null;
  
  @Schema(description = "Indicazione se oltre alla modalità indicata per individuare l'azione viene usata comunque la modalità 'interface-based'")
 /**
   * Indicazione se oltre alla modalità indicata per individuare l'azione viene usata comunque la modalità 'interface-based'  
  **/
  private Boolean forceInterface = true;
  
  @Schema(example = "[\"az1\",\"az2\",\"az3\"]", description = "")
  private List<String> azioni = null;
 /**
   * Get modalita
   * @return modalita
  **/
  @JsonProperty("modalita")
  @NotNull
  public ModalitaIdentificazioneAzioneEnum getModalita() {
    return this.modalita;
  }

  public void setModalita(ModalitaIdentificazioneAzioneEnum modalita) {
    this.modalita = modalita;
  }

  public ApiImplModalitaIdentificazioneAzione modalita(ModalitaIdentificazioneAzioneEnum modalita) {
    this.modalita = modalita;
    return this;
  }

 /**
   * XPath/JsonPath nel caso di modalità &#x27;content-based&#x27; o espressione regolare nel caso &#x27;url-based&#x27;
   * @return pattern
  **/
  @JsonProperty("pattern")
 @Size(max=255)  public String getPattern() {
    return this.pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public ApiImplModalitaIdentificazioneAzione pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

 /**
   * Nome dell&#x27;header http nel caso di modalità &#x27;header-based&#x27;
   * @return nome
  **/
  @JsonProperty("nome")
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ApiImplModalitaIdentificazioneAzione nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Indicazione se oltre alla modalità indicata per individuare l&#x27;azione viene usata comunque la modalità &#x27;interface-based&#x27;
   * @return forceInterface
  **/
  @JsonProperty("force_interface")
  public Boolean isForceInterface() {
    return this.forceInterface;
  }

  public void setForceInterface(Boolean forceInterface) {
    this.forceInterface = forceInterface;
  }

  public ApiImplModalitaIdentificazioneAzione forceInterface(Boolean forceInterface) {
    this.forceInterface = forceInterface;
    return this;
  }

 /**
   * Get azioni
   * @return azioni
  **/
  @JsonProperty("azioni")
  public List<String> getAzioni() {
    return this.azioni;
  }

  public void setAzioni(List<String> azioni) {
    this.azioni = azioni;
  }

  public ApiImplModalitaIdentificazioneAzione azioni(List<String> azioni) {
    this.azioni = azioni;
    return this;
  }

  public ApiImplModalitaIdentificazioneAzione addAzioniItem(String azioniItem) {
    this.azioni.add(azioniItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplModalitaIdentificazioneAzione {\n");
    
    sb.append("    modalita: ").append(ApiImplModalitaIdentificazioneAzione.toIndentedString(this.modalita)).append("\n");
    sb.append("    pattern: ").append(ApiImplModalitaIdentificazioneAzione.toIndentedString(this.pattern)).append("\n");
    sb.append("    nome: ").append(ApiImplModalitaIdentificazioneAzione.toIndentedString(this.nome)).append("\n");
    sb.append("    forceInterface: ").append(ApiImplModalitaIdentificazioneAzione.toIndentedString(this.forceInterface)).append("\n");
    sb.append("    azioni: ").append(ApiImplModalitaIdentificazioneAzione.toIndentedString(this.azioni)).append("\n");
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
