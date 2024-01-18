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
package org.openspcoop2.protocol.sdk.properties;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

/**
 * BinaryConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BinaryConsoleItem extends AbstractConsoleItem<byte[]> {
	
	private String fileName = null;
	private String fileId = null;
	private boolean readOnly = false; // vale solo in change
	private boolean showContent = true; // vale solo in change
	private String noteUpdate = null;

	protected BinaryConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException {
		this(id, label, type, null, null);
	}
	
	protected BinaryConsoleItem(String id, String label, ConsoleItemType type, String fileName, String fileId) throws ProtocolException {
		super(id, label, type);
		this.fileName = fileName;
		this.fileId = fileId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileId() {
		return this.fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isShowContent() {
		return this.showContent;
	}

	public void setShowContent(boolean showContent) {
		this.showContent = showContent;
	}

	public String getNoteUpdate() {
		return this.noteUpdate;
	}

	public void setNoteUpdate(String noteUpdate) {
		this.noteUpdate = noteUpdate;
	}
	
	@Override
	protected byte[] cloneValue(byte[] value) throws ProtocolException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(value);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
