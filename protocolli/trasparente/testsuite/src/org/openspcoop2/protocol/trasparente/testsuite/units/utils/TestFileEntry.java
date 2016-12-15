package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.utils.resources.FileSystemUtilities;


public class TestFileEntry {

	private String filenameRichiesta;
	private String extRichiesta;
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
	
	public String getExtRichiesta() {
		return this.extRichiesta;
	}
	public void setExtRichiesta(String extRichiesta) {
		this.extRichiesta = extRichiesta;
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
	public void setExtRispostaKo(String extRispostaKo) {
		this.extRispostaKo = extRispostaKo;
	}
}
