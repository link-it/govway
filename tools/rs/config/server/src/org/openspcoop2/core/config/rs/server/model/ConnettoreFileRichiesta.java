/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ConnettoreFileRichiesta  {
  
  @Schema(required = true, description = "")
  private String file = null;
  
  @Schema(description = "")
  private String filePermissions = null;
  
  @Schema(description = "")
  private String fileHeaders = null;
  
  @Schema(description = "")
  private String fileHeadersPermissions = null;
  
  @Schema(description = "")
  private Boolean createParentDir = false;
  
  @Schema(description = "")
  private Boolean overwriteIfExists = false;
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

  public ConnettoreFileRichiesta file(String file) {
    this.file = file;
    return this;
  }

 /**
   * Get filePermissions
   * @return filePermissions
  **/
  @JsonProperty("file_permissions")
  @Valid
 @Size(max=255)  public String getFilePermissions() {
    return this.filePermissions;
  }

  public void setFilePermissions(String filePermissions) {
    this.filePermissions = filePermissions;
  }

  public ConnettoreFileRichiesta filePermissions(String filePermissions) {
    this.filePermissions = filePermissions;
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

  public ConnettoreFileRichiesta fileHeaders(String fileHeaders) {
    this.fileHeaders = fileHeaders;
    return this;
  }

 /**
   * Get fileHeadersPermissions
   * @return fileHeadersPermissions
  **/
  @JsonProperty("file_headers_permissions")
  @Valid
 @Size(max=255)  public String getFileHeadersPermissions() {
    return this.fileHeadersPermissions;
  }

  public void setFileHeadersPermissions(String fileHeadersPermissions) {
    this.fileHeadersPermissions = fileHeadersPermissions;
  }

  public ConnettoreFileRichiesta fileHeadersPermissions(String fileHeadersPermissions) {
    this.fileHeadersPermissions = fileHeadersPermissions;
    return this;
  }

 /**
   * Get createParentDir
   * @return createParentDir
  **/
  @JsonProperty("create_parent_dir")
  @Valid
  public Boolean isCreateParentDir() {
    return this.createParentDir;
  }

  public void setCreateParentDir(Boolean createParentDir) {
    this.createParentDir = createParentDir;
  }

  public ConnettoreFileRichiesta createParentDir(Boolean createParentDir) {
    this.createParentDir = createParentDir;
    return this;
  }

 /**
   * Get overwriteIfExists
   * @return overwriteIfExists
  **/
  @JsonProperty("overwrite_if_exists")
  @Valid
  public Boolean isOverwriteIfExists() {
    return this.overwriteIfExists;
  }

  public void setOverwriteIfExists(Boolean overwriteIfExists) {
    this.overwriteIfExists = overwriteIfExists;
  }

  public ConnettoreFileRichiesta overwriteIfExists(Boolean overwriteIfExists) {
    this.overwriteIfExists = overwriteIfExists;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreFileRichiesta {\n");
    
    sb.append("    file: ").append(ConnettoreFileRichiesta.toIndentedString(this.file)).append("\n");
    sb.append("    filePermissions: ").append(ConnettoreFileRichiesta.toIndentedString(this.filePermissions)).append("\n");
    sb.append("    fileHeaders: ").append(ConnettoreFileRichiesta.toIndentedString(this.fileHeaders)).append("\n");
    sb.append("    fileHeadersPermissions: ").append(ConnettoreFileRichiesta.toIndentedString(this.fileHeadersPermissions)).append("\n");
    sb.append("    createParentDir: ").append(ConnettoreFileRichiesta.toIndentedString(this.createParentDir)).append("\n");
    sb.append("    overwriteIfExists: ").append(ConnettoreFileRichiesta.toIndentedString(this.overwriteIfExists)).append("\n");
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
