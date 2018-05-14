package org.openspcoop2.web.monitor.core.status;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

public abstract class BaseSondaPdd implements ISondaPdd{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient Logger log= null;
	protected String name= null;
	protected String identificativo = null;

	protected Properties propertiesSonda = null;
	protected List<IStatus> listaStatus = null;

	protected abstract void init() throws Exception;

	public BaseSondaPdd(String identificativo, Logger log, Properties prop) throws Exception{

		try{
			this.log = log;
			this.propertiesSonda = prop;
			this.identificativo = identificativo;

			this.name = this.identificativo;
			String name_tmp = this.propertiesSonda.getProperty("name");
			if(name_tmp != null)
				this.name = name_tmp;

			init();
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante l'Init Sonda "+identificativo+": " + e.getMessage(),e); 
			throw e;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getIdentificativo() {
		return this.getIdentificativo();
	}

	@Override
	public void reset() throws Exception {
		for (IStatus pddBean : this.listaStatus) {
			pddBean.setStato(SondaStatus.UNDEFINED);
			pddBean.setDescrizione("Verifica stato non effettuata");
		}
	}

	@Override
	public List<IStatus> getStato() throws Exception {
		return this.listaStatus;
	}

	@Override
	public String getLinkDettaglio() throws Exception {
		return null;
	}
}
