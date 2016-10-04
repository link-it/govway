/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.message;

/**
 * SoapUtilsBuildParameter
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapUtilsBuildParameter {
	private byte[] byteMsg = null;
	private boolean isBodyStream;
	private boolean eraserXMLTag = false; 
	private boolean checkEmptyBody = true;
	private boolean fileCacheEnable = false;
	private String attachmentRepoDir = null; 
	private String fileThreshold = null;
	
	public SoapUtilsBuildParameter(byte[] byteMsg, boolean isBodyStream, boolean eraserXMLTag, boolean checkEmptyBody, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold){
		this.byteMsg = byteMsg;
		this.isBodyStream = isBodyStream;
		this.eraserXMLTag = eraserXMLTag; 
		this.checkEmptyBody = checkEmptyBody;
		this.fileCacheEnable = fileCacheEnable;
		this.attachmentRepoDir = attachmentRepoDir; 
		this.fileThreshold = fileThreshold;
	}
	
	public SoapUtilsBuildParameter(byte[] byteMsg, boolean isBodyStream, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold){
		this(byteMsg,isBodyStream,false,true,fileCacheEnable,attachmentRepoDir,fileThreshold);
	}
	
	public SoapUtilsBuildParameter(byte[] byteMsg, boolean isBodyStream, boolean eraserXMLTag, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold){
		this(byteMsg,isBodyStream,eraserXMLTag,true,fileCacheEnable,attachmentRepoDir,fileThreshold);
	}

	public byte[] getByteMsg() {
		return this.byteMsg;
	}

	public boolean isBodyStream() {
		return this.isBodyStream;
	}

	public boolean isEraserXMLTag() {
		return this.eraserXMLTag;
	}

	public boolean isCheckEmptyBody() {
		return this.checkEmptyBody;
	}

	public boolean isFileCacheEnable() {
		return this.fileCacheEnable;
	}

	public String getAttachmentRepoDir() {
		return this.attachmentRepoDir;
	}

	public String getFileThreshold() {
		return this.fileThreshold;
	}
	
	
}
