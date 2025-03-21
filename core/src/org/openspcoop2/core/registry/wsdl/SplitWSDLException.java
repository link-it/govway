/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.wsdl;

/**
 * Eccezione che si puo' verificare durante la creazione dei WSDL suddivisi in schemi, parte comune e parte specifica a partire da WSDL standard. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */

public class SplitWSDLException extends Exception {

	private static final long serialVersionUID = 1L;

	public SplitWSDLException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public SplitWSDLException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SplitWSDLException() {
		super();
	}
	public SplitWSDLException(String msg) {
		super(msg);
	}
}

