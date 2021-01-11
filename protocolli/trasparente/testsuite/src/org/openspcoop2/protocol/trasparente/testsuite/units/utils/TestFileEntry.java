/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * TestFileEntry
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestFileEntry {

	private String filenameRichiesta;
	private String extRichiesta;
	private String extRisposta;
	private byte[] bytesRichiesta;
	
	private String filenameRispostaKo;
	private String extRispostaKo;
	private byte[] bytesRispostaKo;
	
	public byte[] getBytesRichiesta() {
		return this.bytesRichiesta;
	}
	public byte[] getBytesRispostaKo() {
		return this.bytesRispostaKo;
	}
	public String getFilenameRichiesta() {
		return this.filenameRichiesta;
	}
	public void setFilenameRichiesta(String filenameRichiesta) throws Exception {
		this.filenameRichiesta = filenameRichiesta;
		this.bytesRichiesta = FileSystemUtilities.readBytesFromFile(filenameRichiesta);
	}
	
	public void setFilename(String filenameRichiesta, byte[] bytes) throws Exception {
		this.filenameRichiesta = filenameRichiesta;
		this.bytesRichiesta = bytes;
	}
	
	public String getExtRichiesta() {
		return this.extRichiesta;
	}
	public void setExtRichiesta(String extRichiesta) throws UtilsException {
		this.extRichiesta = MimeTypes.getInstance().getMimeType(extRichiesta);
		this.extRisposta = this.extRichiesta; 
	}
	public void setMimeTypeRichiesta(String extRichiesta) throws UtilsException {
		this.extRichiesta = extRichiesta;
		this.extRisposta = extRichiesta;
	}
	public String getFilenameRispostaKo() {
		return this.filenameRispostaKo;
	}
	public void setFilenameRispostaKo(String filenameRispostaKo) throws Exception {
		this.filenameRispostaKo = filenameRispostaKo;
		this.bytesRispostaKo = FileSystemUtilities.readBytesFromFile(filenameRispostaKo);
	}
	public String getExtRispostaKo() {
		return this.extRispostaKo;
	}
	public void setExtRispostaKo(String extRispostaKo) throws UtilsException {
		this.extRispostaKo = MimeTypes.getInstance().getMimeType(extRispostaKo);
	}
	public void setMimeTypeRispostaKo(String extRispostaKo) throws UtilsException {
		this.extRispostaKo = extRispostaKo;
	}
	public String getExtRisposta() {
		return this.extRisposta;
	}
	public void setExtRisposta(String extRisposta) throws UtilsException {
		this.extRisposta = MimeTypes.getInstance().getMimeType(extRisposta);
	}
	public void setMimeTypeRisposta(String extRisposta) throws UtilsException {
		this.extRisposta = extRisposta;
	}
}
