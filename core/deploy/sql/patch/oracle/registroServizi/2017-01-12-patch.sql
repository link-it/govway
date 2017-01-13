-- TransferMode
ALTER TABLE connettori ADD transfer_mode VARCHAR(255);
ALTER TABLE connettori ADD transfer_mode_chunk_size NUMBER;

-- Proxy
ALTER TABLE connettori ADD proxy NUMBER DEFAULT 0;
ALTER TABLE connettori ADD proxy_type VARCHAR(255);
ALTER TABLE connettori ADD proxy_hostname VARCHAR(255);
ALTER TABLE connettori ADD proxy_port VARCHAR(255);
ALTER TABLE connettori ADD proxy_username VARCHAR(255);
ALTER TABLE connettori ADD proxy_password VARCHAR(255);

-- Redirect
ALTER TABLE connettori ADD redirect_mode VARCHAR(255);
ALTER TABLE connettori ADD redirect_max_hop NUMBER;

