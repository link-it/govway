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



package org.openspcoop2.protocol.sdk.validator;


/**
 * Classe utilizzata per raccogliere le informazioni sul tipo di validazione desiderata.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ProprietaValidazione {
    
   
    /* ********  F I E L D S  P R I V A T I  ******** */

    /** Validazione con schema xsd. */
    private boolean validazioneConSchema;
    /** Validazione del profilo di collaborazione. */
    private boolean validazioneProfiloCollaborazione;
    /** Validazione manifestAttachments   . */
    private boolean validazioneManifestAttachments;
    /** Validazione messaggio ritornato in reply */
    private boolean validazioneMessaggioRispostaConnectionReply;
    /** versioneProtocollo */
    private String versioneProtocollo;
    /** validazioneID completa */
    private boolean validazioneIDCompleta;


  

    /* ********  C O S T R U T T O R E  ******** */

	/**
     * Costruttore. 
     *
     * 
     */
    public ProprietaValidazione(){
    		// Costruttore di default.
    }
    /**
     * Costruttore. 
     *
     * @param vSchema Indicazione se deve essere effettuata una validazione secondo schema XSD
     * @param vProfilo Indicazione se deve essere validato il profilo consultando il registro servizi
     * @param vManifest Indicazione se deve essere validato o meno il Manifesto degli attachments
     * 
     */
    public ProprietaValidazione(boolean vSchema,boolean vProfilo,boolean vManifest){
    	this.validazioneConSchema = vSchema;
    	this.validazioneProfiloCollaborazione = vProfilo;
    	this.validazioneManifestAttachments = vManifest;
    }
    


    /**
     * Indicazione se deve essere effettuata una validazione secondo schema XSD
     *
     * @param validazioneConSchema Indicazione se deve essere effettuata una validazione secondo schema XSD
     * 
     */
    public void setValidazioneConSchema(boolean validazioneConSchema) {
		this.validazioneConSchema = validazioneConSchema;
	}
    /**
     * Indicazione se deve essere validato il profilo consultando il registro servizi
     *
     * @param validazioneProfiloCollaborazione Indicazione se deve essere validato il profilo consultando il registro servizi
     * 
     */
    public void setValidazioneProfiloCollaborazione(
			boolean validazioneProfiloCollaborazione) {
		this.validazioneProfiloCollaborazione = validazioneProfiloCollaborazione;
	}
    /**
     * Indicazione se deve essere validato o meno il manifesto degli attachments
     *
     * @param validazioneManifestAttachments Indicazione se deve essere validato il manifesto degli attachments
     * 
     */
    public void setValidazioneManifestAttachments(
			boolean validazioneManifestAttachments) {
		this.validazioneManifestAttachments = validazioneManifestAttachments;
	}


    /**
     * Ritorna l'indicazione se deve essere effettuata una validazione secondo schema XSD
     *
     * @return l'indicazione se deve essere effettuata una validazione secondo schema XSD
     * 
     */ 
	public boolean isValidazioneConSchema() {
		return this.validazioneConSchema;
	}
	/**
     * Ritorna l'indicazione se deve essere validato il profilo consultando il registro servizi
     *
     * @return l'indicazione se deve essere validato il profilo consultando il registro servizi
     * 
     */ 
	public boolean isValidazioneProfiloCollaborazione() {
		return this.validazioneProfiloCollaborazione;
	}
	/**
     * Ritorna l'indicazione se deve essere validato o meno il manifesto degli attachments
     *
     * @return Indicazione se deve essere validato il manifesto degli attachments
     * 
     */
	public boolean isValidazioneManifestAttachments() {
		return this.validazioneManifestAttachments;
	}
	public boolean isValidazioneMessaggioRispostaConnectionReply() {
		return this.validazioneMessaggioRispostaConnectionReply;
	}
	public void setValidazioneMessaggioRispostaConnectionReply(
			boolean validazioneMessaggioRispostaConnectionReply) {
		this.validazioneMessaggioRispostaConnectionReply = validazioneMessaggioRispostaConnectionReply;
	}
	
	
	public String getVersioneProtocollo() {
		return this.versioneProtocollo;
	}

	public void setVersioneProtocollo(String versioneProtocollo) {
		this.versioneProtocollo = versioneProtocollo;
	}

    public boolean isValidazioneIDCompleta() {
		return this.validazioneIDCompleta;
	}
	public void setValidazioneIDCompleta(boolean validazioneIDCompleta) {
		this.validazioneIDCompleta = validazioneIDCompleta;
	}
}





