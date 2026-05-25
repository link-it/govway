@ignore
Feature: Cleanup utenze test per la feature 'Permessi ACL rs-api-config'

Scenario: Cleanup

    * def DbUtils = Java.type('org.openspcoop2.core.config.rs.testsuite.DbUtils')
    * def db = new DbUtils(govwayDbConfig)
    * eval db.update("DELETE FROM users WHERE login IN ('configS','configD','configO','configDR','configSDR')")
