/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.services.connector;

import java.io.Serializable;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * CheckStatoPdDHealthCheckStats
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CheckStatoPdDHealthCheckStats implements Serializable {

	private static final long serialVersionUID = 1L;

	public static CheckStatoPdDHealthCheckStats readProprietaVerificaInformazioniStatistiche(OpenSPCoop2Properties properties) {
		
		boolean verificaStatisticaOraria = properties.isCheckHealthCheckStatsHourlyEnabled();
		boolean verificaStatisticaGiornaliera = properties.isCheckHealthCheckStatsDailyEnabled();
		boolean verificaStatisticaSettimanale = properties.isCheckHealthCheckStatsWeeklyEnabled();
		boolean verificaStatisticaMensile = properties.isCheckHealthCheckStatsMonthlyEnabled();
		int verificaStatisticaOrariaSoglia = properties.getCheckHealthCheckStatsHourlyThreshold();
		int verificaStatisticaGiornalieraSoglia = properties.getCheckHealthCheckStatsDailyThreshold();
		int verificaStatisticaSettimanaleSoglia = properties.getCheckHealthCheckStatsWeeklyThreshold();
		int verificaStatisticaMensileSoglia =properties.getCheckHealthCheckStatsMonthlyThreshold();
		
		CheckStatoPdDHealthCheckStats check = new CheckStatoPdDHealthCheckStats();
		check.setVerificaStatisticaOraria(verificaStatisticaOraria);
		check.setVerificaStatisticaGiornaliera(verificaStatisticaGiornaliera);
		check.setVerificaStatisticaSettimanale(verificaStatisticaSettimanale);
		check.setVerificaStatisticaMensile(verificaStatisticaMensile);
		check.setVerificaStatisticaOrariaSoglia(verificaStatisticaOrariaSoglia);
		check.setVerificaStatisticaGiornalieraSoglia(verificaStatisticaGiornalieraSoglia);
		check.setVerificaStatisticaSettimanaleSoglia(verificaStatisticaSettimanaleSoglia);
		check.setVerificaStatisticaMensileSoglia(verificaStatisticaMensileSoglia);
		
		return check;
	}
	
	private boolean verificaStatisticaOraria = false;
	private boolean verificaStatisticaGiornaliera = false;
	private boolean verificaStatisticaSettimanale = false;
	private boolean verificaStatisticaMensile = false;
	private int verificaStatisticaOrariaSoglia = 1;
	private int verificaStatisticaGiornalieraSoglia = 1;
	private int verificaStatisticaSettimanaleSoglia = 1;
	private int verificaStatisticaMensileSoglia = 1;
	
	public boolean isVerificaStatisticaOraria() {
		return this.verificaStatisticaOraria;
	}
	public void setVerificaStatisticaOraria(boolean verificaStatisticaOraria) {
		this.verificaStatisticaOraria = verificaStatisticaOraria;
	}
	public boolean isVerificaStatisticaGiornaliera() {
		return this.verificaStatisticaGiornaliera;
	}
	public void setVerificaStatisticaGiornaliera(boolean verificaStatisticaGiornaliera) {
		this.verificaStatisticaGiornaliera = verificaStatisticaGiornaliera;
	}
	public boolean isVerificaStatisticaSettimanale() {
		return this.verificaStatisticaSettimanale;
	}
	public void setVerificaStatisticaSettimanale(boolean verificaStatisticaSettimanale) {
		this.verificaStatisticaSettimanale = verificaStatisticaSettimanale;
	}
	public boolean isVerificaStatisticaMensile() {
		return this.verificaStatisticaMensile;
	}
	public void setVerificaStatisticaMensile(boolean verificaStatisticaMensile) {
		this.verificaStatisticaMensile = verificaStatisticaMensile;
	}
	public int getVerificaStatisticaOrariaSoglia() {
		return this.verificaStatisticaOrariaSoglia;
	}
	public void setVerificaStatisticaOrariaSoglia(int verificaStatisticaOrariaSoglia) {
		this.verificaStatisticaOrariaSoglia = verificaStatisticaOrariaSoglia;
	}
	public int getVerificaStatisticaGiornalieraSoglia() {
		return this.verificaStatisticaGiornalieraSoglia;
	}
	public void setVerificaStatisticaGiornalieraSoglia(int verificaStatisticaGiornalieraSoglia) {
		this.verificaStatisticaGiornalieraSoglia = verificaStatisticaGiornalieraSoglia;
	}
	public int getVerificaStatisticaSettimanaleSoglia() {
		return this.verificaStatisticaSettimanaleSoglia;
	}
	public void setVerificaStatisticaSettimanaleSoglia(int verificaStatisticaSettimanaleSoglia) {
		this.verificaStatisticaSettimanaleSoglia = verificaStatisticaSettimanaleSoglia;
	}
	public int getVerificaStatisticaMensileSoglia() {
		return this.verificaStatisticaMensileSoglia;
	}
	public void setVerificaStatisticaMensileSoglia(int verificaStatisticaMensileSoglia) {
		this.verificaStatisticaMensileSoglia = verificaStatisticaMensileSoglia;
	}
	
}
