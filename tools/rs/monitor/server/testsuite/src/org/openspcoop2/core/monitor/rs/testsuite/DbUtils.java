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

package org.openspcoop2.core.monitor.rs.testsuite;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * DbUtils
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private final JdbcTemplate jdbc;

    public DbUtils(Map<String, Object> config) {
        String url = (String) config.get("url");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        String driver = (String) config.get("driverClassName");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbc = new JdbcTemplate(dataSource);
        logger.info("init jdbc template: {}", url);
    }

    public Object readValue(String query) {
        return this.jdbc.queryForObject(query, Object.class);
    }

    public Map<String, Object> readRow(String query) {
        return this.jdbc.queryForMap(query);
    }

    public List<Map<String, Object>> readRows(String query) {
        return this.jdbc.queryForList(query);
    }

    public int update(String query) {
        return this.jdbc.update(query);
    }
    
    public int update(String query, Object ...params) {
        return this.jdbc.update(query, params);
    }
    
	public Timestamp now() {
		return Timestamp.from(Instant.now());
	}
	
	public Timestamp addTimestamp(Timestamp timestamp, int days) {
    	return Timestamp.from(timestamp.toInstant().plus(Duration.ofDays(days)));
    }
	
	public String formatTimestamp(Timestamp timestamp, String format) {
		return new SimpleDateFormat(format).format(timestamp);
	}
	
	public Timestamp parseTimestamp(String timestamp, String format) {
		try {
			return Timestamp.from(new SimpleDateFormat(format).parse(timestamp).toInstant());
		} catch (ParseException e) {
			return null;
		}
	}

}
