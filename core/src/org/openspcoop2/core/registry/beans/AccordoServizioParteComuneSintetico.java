/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ServiceBinding;


/** 
 * AccordoServizioParteComuneSintetico
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */
public class AccordoServizioParteComuneSintetico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccordoServizioParteComuneSintetico(AccordoServizioParteComune apc) {
		this.id = apc.getId();
		this.soggettoReferente = apc.getSoggettoReferente();
		if(apc.getServizioComposto()!=null) {
			this.servizioComposto = new AccordoServizioParteComuneServizioCompostoSintetico(apc.getServizioComposto());
		}
		if(apc.sizeAzioneList()>0) {
			for (Azione azione : apc.getAzioneList()) {
				this.azione.add(new AzioneSintetica(azione));
			}
		}
		if(apc.sizePortTypeList()>0) {
			for (PortType pt : apc.getPortTypeList()) {
				this.portType.add(new PortTypeSintetico(pt));
			}
		}
		if(apc.sizeResourceList()>0) {
			for (Resource pt : apc.getResourceList()) {
				this.resource.add(new ResourceSintetica(pt));
			}
		}
		if(apc.getGruppi()!=null && apc.getGruppi().sizeGruppoList()>0) {
			for (GruppoAccordo gruppoAccordo : apc.getGruppi().getGruppoList()) {
				this.gruppo.add(new GruppoSintetico(gruppoAccordo));
			}
		}
		this.superUser = apc.getSuperUser();
		this.statoPackage = apc.getStatoPackage();
		this.canale = apc.getCanale();
		this.privato = apc.getPrivato();
		this.byteWsdlDefinitorio = apc.getByteWsdlDefinitorio();
		this.byteWsdlConcettuale = apc.getByteWsdlConcettuale();
		this.byteWsdlLogicoErogatore = apc.getByteWsdlLogicoErogatore();
		this.byteWsdlLogicoFruitore = apc.getByteWsdlLogicoFruitore();
		this.byteSpecificaConversazioneConcettuale = apc.getByteSpecificaConversazioneConcettuale();
		this.byteSpecificaConversazioneErogatore = apc.getByteSpecificaConversazioneErogatore();
		this.byteSpecificaConversazioneFruitore = apc.getByteSpecificaConversazioneFruitore();
		this.serviceBinding = apc.getServiceBinding();
		this.nome = apc.getNome();
		this.descrizione = apc.getDescrizione();
		this.profiloCollaborazione = apc.getProfiloCollaborazione();
		this.formatoSpecifica = apc.getFormatoSpecifica();
		this.utilizzoSenzaAzione = apc.getUtilizzoSenzaAzione();
		this.oraRegistrazione = apc.getOraRegistrazione();
		this.versione = apc.getVersione();
	}
	public AccordoServizioParteComuneSintetico() {
	}

	private Long id;

	private IdSoggetto soggettoReferente;

	private AccordoServizioParteComuneServizioCompostoSintetico servizioComposto;

	private List<AzioneSintetica> azione = new ArrayList<AzioneSintetica>();

	private List<PortTypeSintetico> portType = new ArrayList<PortTypeSintetico>();

	private List<ResourceSintetica> resource = new ArrayList<ResourceSintetica>();
	
	private List<GruppoSintetico> gruppo = new ArrayList<GruppoSintetico>();

	private java.lang.String superUser;

	private java.lang.String statoPackage;

	private java.lang.String canale;
	
	private Boolean privato = Boolean.valueOf("false");

	private byte[] byteWsdlDefinitorio;

	private byte[] byteWsdlConcettuale;

	private byte[] byteWsdlLogicoErogatore;

	private byte[] byteWsdlLogicoFruitore;

	private byte[] byteSpecificaConversazioneConcettuale;

	private byte[] byteSpecificaConversazioneErogatore;

	private byte[] byteSpecificaConversazioneFruitore;

	private ServiceBinding serviceBinding;

	private java.lang.String nome;

	private java.lang.String descrizione;

	private ProfiloCollaborazione profiloCollaborazione;

	private FormatoSpecifica formatoSpecifica;

	private boolean utilizzoSenzaAzione = false;

	private java.util.Date oraRegistrazione;
	
	private java.lang.Integer versione = java.lang.Integer.valueOf("1");

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public IdSoggetto getSoggettoReferente() {
		return this.soggettoReferente;
	}

	public void setSoggettoReferente(IdSoggetto soggettoReferente) {
		this.soggettoReferente = soggettoReferente;
	}

	public AccordoServizioParteComuneServizioCompostoSintetico getServizioComposto() {
		return this.servizioComposto;
	}

	public void setServizioComposto(AccordoServizioParteComuneServizioCompostoSintetico servizioComposto) {
		this.servizioComposto = servizioComposto;
	}

	public List<AzioneSintetica> getAzione() {
		return this.azione;
	}

	public void setAzione(List<AzioneSintetica> azione) {
		this.azione = azione;
	}

	public List<PortTypeSintetico> getPortType() {
		return this.portType;
	}

	public void setPortType(List<PortTypeSintetico> portType) {
		this.portType = portType;
	}

	public List<ResourceSintetica> getResource() {
		return this.resource;
	}

	public void setResource(List<ResourceSintetica> resource) {
		this.resource = resource;
	}

	public List<GruppoSintetico> getGruppo() {
		return this.gruppo;
	}
	public void setGruppo(List<GruppoSintetico> gruppo) {
		this.gruppo = gruppo;
	}
	
	public java.lang.String getSuperUser() {
		return this.superUser;
	}

	public void setSuperUser(java.lang.String superUser) {
		this.superUser = superUser;
	}

	public java.lang.String getStatoPackage() {
		return this.statoPackage;
	}

	public void setStatoPackage(java.lang.String statoPackage) {
		this.statoPackage = statoPackage;
	}

	public java.lang.String getCanale() {
		return this.canale;
	}
	public void setCanale(java.lang.String canale) {
		this.canale = canale;
	}
	
	public Boolean getPrivato() {
		return this.privato;
	}

	public void setPrivato(Boolean privato) {
		this.privato = privato;
	}

	public byte[] getByteWsdlDefinitorio() {
		return this.byteWsdlDefinitorio;
	}

	public void setByteWsdlDefinitorio(byte[] byteWsdlDefinitorio) {
		this.byteWsdlDefinitorio = byteWsdlDefinitorio;
	}

	public byte[] getByteWsdlConcettuale() {
		return this.byteWsdlConcettuale;
	}

	public void setByteWsdlConcettuale(byte[] byteWsdlConcettuale) {
		this.byteWsdlConcettuale = byteWsdlConcettuale;
	}

	public byte[] getByteWsdlLogicoErogatore() {
		return this.byteWsdlLogicoErogatore;
	}

	public void setByteWsdlLogicoErogatore(byte[] byteWsdlLogicoErogatore) {
		this.byteWsdlLogicoErogatore = byteWsdlLogicoErogatore;
	}

	public byte[] getByteWsdlLogicoFruitore() {
		return this.byteWsdlLogicoFruitore;
	}

	public void setByteWsdlLogicoFruitore(byte[] byteWsdlLogicoFruitore) {
		this.byteWsdlLogicoFruitore = byteWsdlLogicoFruitore;
	}

	public byte[] getByteSpecificaConversazioneConcettuale() {
		return this.byteSpecificaConversazioneConcettuale;
	}

	public void setByteSpecificaConversazioneConcettuale(byte[] byteSpecificaConversazioneConcettuale) {
		this.byteSpecificaConversazioneConcettuale = byteSpecificaConversazioneConcettuale;
	}

	public byte[] getByteSpecificaConversazioneErogatore() {
		return this.byteSpecificaConversazioneErogatore;
	}

	public void setByteSpecificaConversazioneErogatore(byte[] byteSpecificaConversazioneErogatore) {
		this.byteSpecificaConversazioneErogatore = byteSpecificaConversazioneErogatore;
	}

	public byte[] getByteSpecificaConversazioneFruitore() {
		return this.byteSpecificaConversazioneFruitore;
	}

	public void setByteSpecificaConversazioneFruitore(byte[] byteSpecificaConversazioneFruitore) {
		this.byteSpecificaConversazioneFruitore = byteSpecificaConversazioneFruitore;
	}

	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}

	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}

	public java.lang.String getNome() {
		return this.nome;
	}

	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	public java.lang.String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	public ProfiloCollaborazione getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}

	public void setProfiloCollaborazione(ProfiloCollaborazione profiloCollaborazione) {
		this.profiloCollaborazione = profiloCollaborazione;
	}

	public FormatoSpecifica getFormatoSpecifica() {
		return this.formatoSpecifica;
	}

	public void setFormatoSpecifica(FormatoSpecifica formatoSpecifica) {
		this.formatoSpecifica = formatoSpecifica;
	}

	public boolean isUtilizzoSenzaAzione() {
		return this.utilizzoSenzaAzione;
	}

	public void setUtilizzoSenzaAzione(boolean utilizzoSenzaAzione) {
		this.utilizzoSenzaAzione = utilizzoSenzaAzione;
	}

	public java.util.Date getOraRegistrazione() {
		return this.oraRegistrazione;
	}

	public void setOraRegistrazione(java.util.Date oraRegistrazione) {
		this.oraRegistrazione = oraRegistrazione;
	}
	
	public java.lang.Integer getVersione() {
		return this.versione;
	}

	public void setVersione(java.lang.Integer versione) {
		this.versione = versione;
	}

}
