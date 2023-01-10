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
package org.openspcoop2.message;

import java.io.File;

import org.openspcoop2.message.exception.MessageException;

/**
 * AttachmentsProcessingMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttachmentsProcessingMode {

	public static AttachmentsProcessingMode getFileCacheProcessingMode(File fileRepository,String fileThreshold) throws MessageException{
		return new AttachmentsProcessingMode(true,fileRepository,fileThreshold);
	}
	public static AttachmentsProcessingMode getFileCacheProcessingMode(String fileRepository,String fileThreshold) throws MessageException{
		return new AttachmentsProcessingMode(true,new File(fileRepository),fileThreshold);
	}
	public static AttachmentsProcessingMode getMemoryCacheProcessingMode() {
		return new AttachmentsProcessingMode(false);
	}
	
	private boolean fileCacheEnable;
	private File fileRepository;
	private String fileThreshold;
	
	private AttachmentsProcessingMode(boolean fileCacheEnable) {
		this.fileCacheEnable = fileCacheEnable;
	}
	private AttachmentsProcessingMode(boolean fileCacheEnable,File fileRepository,String fileThreshold) throws MessageException{
		if(fileCacheEnable){
			if(fileRepository==null){
				throw new MessageException("Repository directory for attachments undefined (required with fileCache enabled)");
			}
			if(fileRepository.exists()==false){
				throw new MessageException("Repository directory for attachments ["+fileRepository.getAbsolutePath()+"] not exists (required with fileCache enabled)");
			}
			if(fileRepository.canRead()==false){
				throw new MessageException("Repository directory for attachments ["+fileRepository.getAbsolutePath()+"] cannot read (required with fileCache enabled)");
			}
			if(fileRepository.canWrite()==false){
				throw new MessageException("Repository directory for attachments ["+fileRepository.getAbsolutePath()+"] cannot write (required with fileCache enabled)");
			}
			if(fileThreshold==null){
				throw new MessageException("Threshold for attachments undefined (required with fileCache enabled)");
			}
		}
		this.fileCacheEnable = fileCacheEnable;
		this.fileRepository = fileRepository;
		this.fileThreshold = fileThreshold;
	}
	
	public boolean isFileCacheEnable() {
		return this.fileCacheEnable;
	}
	public void setFileCacheEnable(boolean fileCacheEnable) {
		this.fileCacheEnable = fileCacheEnable;
	}
	public File getFileRepository() {
		return this.fileRepository;
	}
	public void setFileRepository(File fileRepository) {
		this.fileRepository = fileRepository;
	}
	public String getFileThreshold() {
		return this.fileThreshold;
	}
	public void setFileThreshold(String fileThreshold) {
		this.fileThreshold = fileThreshold;
	}
	
}
