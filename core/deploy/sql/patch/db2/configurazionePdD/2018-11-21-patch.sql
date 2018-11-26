-- Gestione CORS in PorteDelegate
ALTER TABLE porte_delegate ADD cors_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD cors_tipo VARCHAR(255);
ALTER TABLE porte_delegate ADD cors_all_allow_origins VARCHAR(255);
ALTER TABLE porte_delegate ADD cors_allow_credentials VARCHAR(255);
ALTER TABLE porte_delegate ADD cors_allow_max_age INT;
ALTER TABLE porte_delegate ADD cors_allow_max_age_seconds INT;
ALTER TABLE porte_delegate ADD cors_allow_origins CLOB;
ALTER TABLE porte_delegate ADD cors_allow_headers CLOB;
ALTER TABLE porte_delegate ADD cors_allow_methods CLOB;
ALTER TABLE porte_delegate ADD cors_allow_expose_headers CLOB;

-- Response caching in PorteDelegate
ALTER TABLE porte_delegate ADD response_cache_stato VARCHAR(255);
ALTER TABLE porte_delegate ADD response_cache_seconds INT;
ALTER TABLE porte_delegate ADD response_cache_max_msg_size BIGINT;
ALTER TABLE porte_delegate ADD response_cache_hash_url VARCHAR(255);
ALTER TABLE porte_delegate ADD response_cache_hash_headers VARCHAR(255);
ALTER TABLE porte_delegate ADD response_cache_hash_payload VARCHAR(255);

-- Gestione CORS in PorteApplicative
ALTER TABLE porte_applicative ADD cors_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD cors_tipo VARCHAR(255);
ALTER TABLE porte_applicative ADD cors_all_allow_origins VARCHAR(255);
ALTER TABLE porte_applicative ADD cors_allow_credentials VARCHAR(255);
ALTER TABLE porte_applicative ADD cors_allow_max_age INT;
ALTER TABLE porte_applicative ADD cors_allow_max_age_seconds INT;
ALTER TABLE porte_applicative ADD cors_allow_origins CLOB;
ALTER TABLE porte_applicative ADD cors_allow_headers CLOB;
ALTER TABLE porte_applicative ADD cors_allow_methods CLOB;
ALTER TABLE porte_applicative ADD cors_allow_expose_headers CLOB;

-- Response caching in PorteApplicative
ALTER TABLE porte_applicative ADD response_cache_stato VARCHAR(255);
ALTER TABLE porte_applicative ADD response_cache_seconds INT;
ALTER TABLE porte_applicative ADD response_cache_max_msg_size BIGINT;
ALTER TABLE porte_applicative ADD response_cache_hash_url VARCHAR(255);
ALTER TABLE porte_applicative ADD response_cache_hash_headers VARCHAR(255);
ALTER TABLE porte_applicative ADD response_cache_hash_payload VARCHAR(255);

-- Gestione CORS
ALTER TABLE configurazione ADD cors_stato VARCHAR(255);
ALTER TABLE configurazione ADD cors_tipo VARCHAR(255);
ALTER TABLE configurazione ADD cors_all_allow_origins VARCHAR(255);
ALTER TABLE configurazione ADD cors_allow_credentials VARCHAR(255);
ALTER TABLE configurazione ADD cors_allow_max_age INT;
ALTER TABLE configurazione ADD cors_allow_max_age_seconds INT;
ALTER TABLE configurazione ADD cors_allow_origins CLOB;
ALTER TABLE configurazione ADD cors_allow_headers CLOB;
ALTER TABLE configurazione ADD cors_allow_methods CLOB;
ALTER TABLE configurazione ADD cors_allow_expose_headers CLOB;

-- Response caching
ALTER TABLE configurazione ADD response_cache_stato VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_seconds INT;
ALTER TABLE configurazione ADD response_cache_max_msg_size BIGINT;
ALTER TABLE configurazione ADD response_cache_hash_url VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_hash_headers VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_hash_payload VARCHAR(255);

-- Cache per il response caching
ALTER TABLE configurazione ADD response_cache_statocache VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD response_cache_lifecache VARCHAR(255);
