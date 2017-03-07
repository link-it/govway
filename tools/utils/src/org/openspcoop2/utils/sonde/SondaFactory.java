/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.sonde;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Classe di implementazione della Factory per le Sonde
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author: gbussu $
 * @version $Rev: 12563 $, $Date: 2017-01-11 13:25:31 +0100(mer, 11 gen 2017) $
 */

public class SondaFactory {

	/**
	 * Aggiorna la configurazione della sonda identificata dal parametro nome
	 * @param nome nome della sonda da aggiornare
	 * @param warn soglia di warning da impostare
	 * @param err soglia di error da impostare
	 * @param connection connessione al database
	 * @param tipoDatabase tipo di database
	 * @throws Exception
	 */
	public static void updateConfSonda(String nome, long warn, long err, Connection connection, TipiDatabase tipoDatabase) throws Exception {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);

		sqlQueryObject.addUpdateTable("sonde");
		sqlQueryObject.addUpdateField("soglia_warn", "?");
		sqlQueryObject.addUpdateField("soglia_error", "?");
		sqlQueryObject.addWhereCondition("nome = ?");
		String sql = sqlQueryObject.createSQLUpdate();

		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setLong(1, warn);
		ps.setLong(2, err);
		ps.setString(3, nome);
		ps.executeUpdate();
	}

	/**
	 * Aggiorna lo stato della sonda identificata dal parametro nome
	 * @param nome nome della sonda da aggiornare
	 * @param sonda parametri con cui aggiornare la sonda
	 * @param connection connessione al database
	 * @param tipoDatabase tipo di database
	 * @throws SondaException
	 */
	public static void updateStatoSonda(String nome, Sonda sonda, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);

			sqlQueryObject.addUpdateTable("sonde");
			sqlQueryObject.addUpdateField("data_warn", "?");
			sqlQueryObject.addUpdateField("data_error", "?");
			sqlQueryObject.addUpdateField("data_ultimo_check", "?");
			sqlQueryObject.addUpdateField("dati_check", "?");
			sqlQueryObject.addUpdateField("stato_ultimo_check", "?");
			sqlQueryObject.addWhereCondition("nome = ?");
			String sql = sqlQueryObject.createSQLUpdate();

			PreparedStatement ps = connection.prepareStatement(sql);
			int i = 1;
			if(sonda.getParam().getDataWarn()!= null) {
				ps.setTimestamp(i++, new java.sql.Timestamp(sonda.getParam().getDataWarn().getTime()));
			} else {
				ps.setNull(i++, Types.TIMESTAMP);
			}
			if(sonda.getParam().getDataError()!= null) {
				ps.setTimestamp(i++, new java.sql.Timestamp(sonda.getParam().getDataError().getTime()));
			} else {
				ps.setNull(i++, Types.TIMESTAMP);
			}
			ps.setTimestamp(i++, new java.sql.Timestamp(sonda.getParam().getDataUltimoCheck().getTime()));
			ps.setString(i++, sonda.getParam().marshallDatiCheck());
			ps.setInt(i++, sonda.getParam().getStatoUltimoCheck());
			ps.setString(i++, nome);
			ps.executeUpdate();
		} catch(Exception e) {
			throw new SondaException(e);
		}
	}

	/**
	 * @param nome nome della sonda da recuperare
	 * @param connection connessione al database
	 * @param tipoDatabase tipo di database
	 * @return la sonda identificata dal parametro nome
	 * @throws SondaException
	 */
	public static Sonda get(String nome, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		try {
			ISQLQueryObject sqlQueryObject = getSqlQueryObjectForGetSonda(tipoDatabase);

			sqlQueryObject.addWhereCondition("nome = ?");
			String sql = sqlQueryObject.createSQLQuery();

			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, nome);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return getSonda(rs);
			}

			return null;
		} catch(Exception e) {
			throw new SondaException(e);
		}
	}

	/**
	 * @param connection connessione al database
	 * @param tipoDatabase tipo di database
	 * @return la lista di tutte le sonde presenti nel db
	 * @throws SondaException
	 */
	public static List<Sonda> findAll(Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		try {
			List<Sonda> sondaLst = new ArrayList<Sonda>();

			ISQLQueryObject sqlQueryObject = getSqlQueryObjectForGetSonda(tipoDatabase);

			String sql = sqlQueryObject.createSQLQuery();

			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Sonda sonda = getSonda(rs);
				sondaLst.add(sonda);
			}

			return sondaLst;
		} catch(Exception e) {
			throw new SondaException(e);
		}
	}

	private static Sonda getSonda(ResultSet rs) throws SQLException,
	ClassNotFoundException, NoSuchMethodException, Exception,
	InstantiationException, IllegalAccessException,
	InvocationTargetException {
		String nome = rs.getString("nome");
		String classe = rs.getString("classe");
		Long soglia_warn = rs.getLong("soglia_warn");
		Long soglia_error = rs.getLong("soglia_error");
		Date data_warn = rs.getTimestamp("data_warn");
		Date data_error = rs.getTimestamp("data_error");
		Date data_ultimo_check = rs.getDate("data_ultimo_check");
		String dati_check = rs.getString("dati_check");
		Integer stato_ultimo_check = rs.getInt("stato_ultimo_check");
		Class<?> className = null;
		try {
			className = (Class<?>) Class.forName(classe);
		} catch(ClassNotFoundException e) {
			throw new SondaException("Classe di definizione della sonda ["+classe+"] non trovata");
		}
		if(!Sonda.class.isAssignableFrom(className)) {
			throw new SondaException("Classe di definizione della sonda ["+classe+"] deve essere un'estensione della classe ["+Sonda.class.getName()+"]");
		}
		Constructor<?> ctor = className.getDeclaredConstructor(ParametriSonda.class);
		ctor.setAccessible(true);
		ParametriSonda param = new ParametriSonda();

		param.setNome(nome);

		param.setDataUltimoCheck(data_ultimo_check);

		param.setDataError(data_error);
		param.setDataWarn(data_warn);

		param.unmarshallDatiCheck(dati_check);

		param.setSogliaWarn(soglia_warn);
		param.setSogliaError(soglia_error);

		param.setStatoUltimoCheck(stato_ultimo_check);

		Sonda sonda = (Sonda)ctor.newInstance(param);
		return sonda;
	}

	private static ISQLQueryObject getSqlQueryObjectForGetSonda(TipiDatabase tipoDatabase) throws SQLQueryObjectException {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);

		sqlQueryObject.addFromTable("sonde");
		sqlQueryObject.addSelectField("nome");
		sqlQueryObject.addSelectField("classe");
		sqlQueryObject.addSelectField("soglia_warn");
		sqlQueryObject.addSelectField("soglia_error");
		sqlQueryObject.addSelectField("data_warn");
		sqlQueryObject.addSelectField("data_error");
		sqlQueryObject.addSelectField("data_ultimo_check");
		sqlQueryObject.addSelectField("dati_check");
		sqlQueryObject.addSelectField("stato_ultimo_check");
		return sqlQueryObject;
	}

}
