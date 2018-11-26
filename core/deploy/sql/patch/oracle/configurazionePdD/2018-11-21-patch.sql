-- Gestione CORS in PorteDelegate
ALTER TABLE porte_delegate ADD cors_stato VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_tipo VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_all_allow_origins VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_allow_credentials VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_allow_max_age NUMBER;
ALTER TABLE porte_delegate ADD cors_allow_max_age_seconds NUMBER;
ALTER TABLE porte_delegate ADD cors_allow_origins CLOB;
ALTER TABLE porte_delegate ADD cors_allow_headers CLOB;
ALTER TABLE porte_delegate ADD cors_allow_methods CLOB;
ALTER TABLE porte_delegate ADD cors_allow_expose_headers CLOB;

-- Response caching in PorteDelegate
ALTER TABLE porte_delegate ADD response_cache_stato VARCHAR2(255);
ALTER TABLE porte_delegate ADD response_cache_seconds NUMBER;
ALTER TABLE porte_delegate ADD response_cache_max_msg_size NUMBER;
ALTER TABLE porte_delegate ADD response_cache_hash_url VARCHAR2(255);
ALTER TABLE porte_delegate ADD response_cache_hash_headers VARCHAR2(255);
ALTER TABLE porte_delegate ADD response_cache_hash_payload VARCHAR2(255);

-- Gestione CORS in PorteApplicative
ALTER TABLE porte_applicative ADD cors_stato VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_tipo VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_all_allow_origins VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_allow_credentials VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_allow_max_age NUMBER;
ALTER TABLE porte_applicative ADD cors_allow_max_age_seconds NUMBER;
ALTER TABLE porte_applicative ADD cors_allow_origins CLOB;
ALTER TABLE porte_applicative ADD cors_allow_headers CLOB;
ALTER TABLE porte_applicative ADD cors_allow_methods CLOB;
ALTER TABLE porte_applicative ADD cors_allow_expose_headers CLOB;

-- Response caching in PorteApplicative
ALTER TABLE porte_applicative ADD response_cache_stato VARCHAR2(255);
ALTER TABLE porte_applicative ADD response_cache_seconds NUMBER;
ALTER TABLE porte_applicative ADD response_cache_max_msg_size NUMBER;
ALTER TABLE porte_applicative ADD response_cache_hash_url VARCHAR2(255);
ALTER TABLE porte_applicative ADD response_cache_hash_headers VARCHAR2(255);
ALTER TABLE porte_applicative ADD response_cache_hash_payload VARCHAR2(255);

-- Gestione CORS
ALTER TABLE configurazione ADD cors_stato VARCHAR2(255);
ALTER TABLE configurazione ADD cors_tipo VARCHAR2(255);
ALTER TABLE configurazione ADD cors_all_allow_origins VARCHAR2(255);
ALTER TABLE configurazione ADD cors_allow_credentials VARCHAR2(255);
ALTER TABLE configurazione ADD cors_allow_max_age NUMBER;
ALTER TABLE configurazione ADD cors_allow_max_age_seconds NUMBER;
ALTER TABLE configurazione ADD cors_allow_origins CLOB;
ALTER TABLE configurazione ADD cors_allow_headers CLOB;
ALTER TABLE configurazione ADD cors_allow_methods CLOB;
ALTER TABLE configurazione ADD cors_allow_expose_headers CLOB;

-- Response caching
ALTER TABLE configurazione ADD response_cache_stato VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_seconds NUMBER;
ALTER TABLE configurazione ADD response_cache_max_msg_size NUMBER;
ALTER TABLE configurazione ADD response_cache_hash_url VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_hash_headers VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_hash_payload VARCHAR2(255);

-- Cache per il response caching
ALTER TABLE configurazione ADD response_cache_statocache VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_dimensionecache VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_algoritmocache VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_idlecache VARCHAR2(255);
ALTER TABLE configurazione ADD response_cache_lifecache VARCHAR2(255);
