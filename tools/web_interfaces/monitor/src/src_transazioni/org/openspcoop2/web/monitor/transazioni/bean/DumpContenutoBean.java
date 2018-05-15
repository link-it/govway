package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.utils.TransactionContentUtils;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

public class DumpContenutoBean extends DumpContenuto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DumpContenutoBean() {
		super();
	}
	
	public DumpContenutoBean(DumpContenuto dumpContenuto){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		
		BeanUtils.copy(this, dumpContenuto, metodiEsclusi);
	}

	public java.lang.String getValoreAsString() {
		return TransactionContentUtils.getDumpContenutoValue(this);
	}
}
