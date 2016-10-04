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

package org.openspcoop2.protocol.sdk.archive;

/**
 *  ArchiveCascadeConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveCascadeConfiguration {
	
	public ArchiveCascadeConfiguration(){	
	}
	public ArchiveCascadeConfiguration(boolean cascadeAllEnabled){
		
		this.cascadePdd = cascadeAllEnabled;
		
		this.cascadeSoggetti = cascadeAllEnabled;
		
		this.cascadeServiziApplicativi = cascadeAllEnabled;
		this.cascadePorteDelegate = cascadeAllEnabled;
		this.cascadePorteApplicative = cascadeAllEnabled;
		
		this.cascadeAccordoServizioParteComune = cascadeAllEnabled;
		this.cascadeAccordoServizioComposto = cascadeAllEnabled;
		this.cascadeAccordoServizioParteSpecifica = cascadeAllEnabled;
		this.cascadeAccordoCooperazione = cascadeAllEnabled;
		this.cascadeFruizioni = cascadeAllEnabled;
	}
	
	@Override
	public Object clone(){
		ArchiveCascadeConfiguration config = new ArchiveCascadeConfiguration();
		
		config.cascadePdd = this.cascadePdd;
		
		config.cascadeSoggetti = this.cascadeSoggetti;
		
		config.cascadeServiziApplicativi = this.cascadeServiziApplicativi;
		config.cascadePorteDelegate = this.cascadePorteDelegate;
		config.cascadePorteApplicative = this.cascadePorteApplicative;
		
		config.cascadeAccordoServizioParteComune = this.cascadeAccordoServizioParteComune;
		config.cascadeAccordoServizioComposto = this.cascadeAccordoServizioComposto;
		config.cascadeAccordoServizioParteSpecifica = this.cascadeAccordoServizioParteSpecifica;
		config.cascadeAccordoCooperazione = this.cascadeAccordoCooperazione;
		config.cascadeFruizioni = this.cascadeFruizioni;
	
		return config;
	}
	
	
	private boolean cascadePdd = false;
	
	private boolean cascadeSoggetti = false;
	
	private boolean cascadeServiziApplicativi = false;
	private boolean cascadePorteDelegate = false;
	private boolean cascadePorteApplicative = false;
	
	private boolean cascadeAccordoServizioParteComune = false;
	private boolean cascadeAccordoServizioComposto = false;
	private boolean cascadeAccordoServizioParteSpecifica = false;
	private boolean cascadeAccordoCooperazione = false;
	private boolean cascadeFruizioni = false;
	
	
	public boolean isCascadePdd() {
		return this.cascadePdd;
	}
	public void setCascadePdd(boolean cascadePdd) {
		this.cascadePdd = cascadePdd;
	}
	
	public boolean isCascadeFruizioni() {
		return this.cascadeFruizioni;
	}
	public void setCascadeFruizioni(boolean cascadeFruizioni) {
		this.cascadeFruizioni = cascadeFruizioni;
	}
	public boolean isCascadeSoggetti() {
		return this.cascadeSoggetti;
	}
	public void setCascadeSoggetti(boolean cascadeSoggetti) {
		this.cascadeSoggetti = cascadeSoggetti;
	}
	public boolean isCascadeServiziApplicativi() {
		return this.cascadeServiziApplicativi;
	}
	public void setCascadeServiziApplicativi(boolean cascadeServiziApplicativi) {
		this.cascadeServiziApplicativi = cascadeServiziApplicativi;
	}
	public boolean isCascadePorteDelegate() {
		return this.cascadePorteDelegate;
	}
	public void setCascadePorteDelegate(boolean cascadePorteDelegate) {
		this.cascadePorteDelegate = cascadePorteDelegate;
	}
	public boolean isCascadePorteApplicative() {
		return this.cascadePorteApplicative;
	}
	public void setCascadePorteApplicative(boolean cascadePorteApplicative) {
		this.cascadePorteApplicative = cascadePorteApplicative;
	}
	public boolean isCascadeAccordoServizioParteComune() {
		return this.cascadeAccordoServizioParteComune;
	}
	public void setCascadeAccordoServizioParteComune(
			boolean cascadeAccordoServizioParteComune) {
		this.cascadeAccordoServizioParteComune = cascadeAccordoServizioParteComune;
	}
	public boolean isCascadeAccordoServizioComposto() {
		return this.cascadeAccordoServizioComposto;
	}
	public void setCascadeAccordoServizioComposto(
			boolean cascadeAccordoServizioComposto) {
		this.cascadeAccordoServizioComposto = cascadeAccordoServizioComposto;
	}
	public boolean isCascadeAccordoServizioParteSpecifica() {
		return this.cascadeAccordoServizioParteSpecifica;
	}
	public void setCascadeAccordoServizioParteSpecifica(
			boolean cascadeAccordoServizioParteSpecifica) {
		this.cascadeAccordoServizioParteSpecifica = cascadeAccordoServizioParteSpecifica;
	}
	public boolean isCascadeAccordoCooperazione() {
		return this.cascadeAccordoCooperazione;
	}
	public void setCascadeAccordoCooperazione(boolean cascadeAccordoCooperazione) {
		this.cascadeAccordoCooperazione = cascadeAccordoCooperazione;
	}
		
}
