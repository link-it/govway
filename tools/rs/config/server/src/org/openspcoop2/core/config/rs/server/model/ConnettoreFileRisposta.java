/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreFileRisposta  {
  
  @Schema(required = true, description = "")
  private String file = null;
  
  @Schema(description = "")
  private String fileHeaders = null;
  
  @Schema(description = "")
  private Boolean deleteAfterRead = false;
  
  @Schema(description = "")
  private Integer waitIfNotExistsMs = null;
 /**
   * Get file
   * @return file
  **/
  @JsonProperty("file")
  @NotNull
  @Valid
 @Size(max=4000)  public String getFile() {
    return this.file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public ConnettoreFileRisposta file(String file) {
    this.file = file;
    return this;
  }

 /**
   * Get fileHeaders
   * @return fileHeaders
  **/
  @JsonProperty("file_headers")
  @Valid
 @Size(max=4000)  public String getFileHeaders() {
    return this.fileHeaders;
  }

  public void setFileHeaders(String fileHeaders) {
    this.fileHeaders = fileHeaders;
  }

  public ConnettoreFileRisposta fileHeaders(String fileHeaders) {
    this.fileHeaders = fileHeaders;
    return this;
  }

 /**
   * Get deleteAfterRead
   * @return deleteAfterRead
  **/
  @JsonProperty("delete_after_read")
  @Valid
  public Boolean isDeleteAfterRead() {
    return this.deleteAfterRead;
  }

  public void setDeleteAfterRead(Boolean deleteAfterRead) {
    this.deleteAfterRead = deleteAfterRead;
  }

  public ConnettoreFileRisposta deleteAfterRead(Boolean deleteAfterRead) {
    this.deleteAfterRead = deleteAfterRead;
    return this;
  }

 /**
   * Get waitIfNotExistsMs
   * @return waitIfNotExistsMs
  **/
  @JsonProperty("wait_if_not_exists_ms")
  @Valid
  public Integer getWaitIfNotExistsMs() {
    return this.waitIfNotExistsMs;
  }

  public void setWaitIfNotExistsMs(Integer waitIfNotExistsMs) {
    this.waitIfNotExistsMs = waitIfNotExistsMs;
  }

  public ConnettoreFileRisposta waitIfNotExistsMs(Integer waitIfNotExistsMs) {
    this.waitIfNotExistsMs = waitIfNotExistsMs;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreFileRisposta {\n");
    
    sb.append("    file: ").append(ConnettoreFileRisposta.toIndentedString(this.file)).append("\n");
    sb.append("    fileHeaders: ").append(ConnettoreFileRisposta.toIndentedString(this.fileHeaders)).append("\n");
    sb.append("    deleteAfterRead: ").append(ConnettoreFileRisposta.toIndentedString(this.deleteAfterRead)).append("\n");
    sb.append("    waitIfNotExistsMs: ").append(ConnettoreFileRisposta.toIndentedString(this.waitIfNotExistsMs)).append("\n");
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
