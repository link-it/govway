/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.dao;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.utils.beans.BaseBean;

/**
 * Bean ruolo
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Ruolo extends BaseBean {

	private Long idAccordo;
	private Long idServizioApplicativo;
	private String nome;
	private String nomeServizioApplicativo;
	private String tipoProprietarioSA;
	private String nomeProprietarioSA;
	private boolean correlato;
	private String oldNomeForUpdate;
	private String descrizione;
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Long getIdAccordo() {
		return this.idAccordo;
	}

	public String getNome() {
		return this.nome;
	}

	public void setIdAccordo(Long id) {
		this.idAccordo = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getOldNomeForUpdate() {
		return this.oldNomeForUpdate;
	}

	public void setOldNomeForUpdate(String oldNomeForUpdate) {
		this.oldNomeForUpdate = oldNomeForUpdate;
	}

	public boolean isCorrelato() {
		return this.correlato;
	}

	public void setCorrelato(boolean correlato) {
		this.correlato = correlato;
	}

	public Long getIdServizioApplicativo() {
		return this.idServizioApplicativo;
	}

	public void setIdServizioApplicativo(Long idServizioApplicativo) {
		this.idServizioApplicativo = idServizioApplicativo;
	}

	public String getNomeServizioApplicativo() {
		return this.nomeServizioApplicativo;
	}

	public void setNomeServizioApplicativo(String nomeServizioApplicativo) {
		this.nomeServizioApplicativo = nomeServizioApplicativo;
	}

	public String getTipoProprietarioSA() {
		return this.tipoProprietarioSA;
	}

	public void setTipoProprietarioSA(String tipoProprietarioSA) {
		this.tipoProprietarioSA = tipoProprietarioSA;
	}

	public String getNomeProprietarioSA() {
		return this.nomeProprietarioSA;
	}

	public void setNomeProprietarioSA(String nomeProprietarioSA) {
		this.nomeProprietarioSA = nomeProprietarioSA;
	}
	
	
	public static String getNomeRuoloByUriAccordo(String uriAccordo) throws DriverRegistroServiziException{
		String tmp = uriAccordo.replaceAll("/", "");
		tmp = tmp.replaceAll("\\.", "_");
		tmp = tmp.replaceAll(":", "_");
		return tmp;
	}
	public static String getNomeRuoloByIDAccordo(IDAccordo idAccordo) throws DriverRegistroServiziException{
		return Ruolo.getNomeRuoloByUriAccordo(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
	}
	public static String getNomeRuoloByAccordo(AccordoServizioParteComune accordo) throws DriverRegistroServiziException{
		return Ruolo.getNomeRuoloByUriAccordo(IDAccordoFactory.getInstance().getUriFromAccordo(accordo));
	}
}
