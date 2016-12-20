package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;


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
