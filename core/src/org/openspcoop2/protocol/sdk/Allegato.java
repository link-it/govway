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



package org.openspcoop2.protocol.sdk;


/**
 * Classe utilizzata per rappresentare un Allegato.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Allegato implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private org.openspcoop2.core.tracciamento.Allegato allegato;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public Allegato(){
		this.allegato = new org.openspcoop2.core.tracciamento.Allegato();
	}
	public Allegato(org.openspcoop2.core.tracciamento.Allegato allegato){
		this.allegato = allegato;
	}


	
	// base
	
	public org.openspcoop2.core.tracciamento.Allegato getAllegato() {
		return this.allegato;
	}
	public void setAllegato(org.openspcoop2.core.tracciamento.Allegato allegato) {
		this.allegato = allegato;
	}
	
	

	// id  [Wrapper]
	
	public Long getId() {
		return this.allegato.getId();
	}
	public void setId(Long id) {
		this.allegato.setId(id);
	}

	
	// content-id  [Wrapper]
	
	public java.lang.String getContentId() {
		return this.allegato.getContentId();
	}
	public void setContentId(java.lang.String contentId) {
		this.allegato.setContentId(contentId);
	}

	
	// content-location  [Wrapper]
	
	public java.lang.String getContentLocation() {
		return this.allegato.getContentLocation();
	}
	public void setContentLocation(java.lang.String contentLocation) {
		this.allegato.setContentLocation(contentLocation);
	}

	
	// content-type  [Wrapper]
	
	public java.lang.String getContentType() {
		return this.allegato.getContentType();
	}
	public void setContentType(java.lang.String contentType) {
		this.allegato.setContentType(contentType);
	}

	
	// digest  [Wrapper]
	
	public java.lang.String getDigest() {
		return this.allegato.getDigest();
	}
	public void setDigest(java.lang.String digest) {
		this.allegato.setDigest(digest);
	}






	@Override
	public Allegato clone(){

		// Non uso il base clone per far si che venga usato il costruttore new String()

		Allegato clone = new Allegato();

		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		clone.setContentId(this.getContentId()!=null ? new String(this.getContentId()) : null);
		clone.setContentLocation(this.getContentLocation()!=null ? new String(this.getContentLocation()) : null);
		clone.setContentType(this.getContentType()!=null ? new String(this.getContentType()) : null);
		clone.setDigest(this.getDigest()!=null ? new String(this.getDigest()) : null);

		return clone;
	}
}





