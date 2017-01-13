-- TransferMode
ALTER TABLE connettori ADD COLUMN transfer_mode VARCHAR(255);
ALTER TABLE connettori ADD COLUMN transfer_mode_chunk_size INT;

-- Proxy
ALTER TABLE connettori ADD COLUMN proxy INT DEFAULT 0;
ALTER TABLE connettori ADD COLUMN proxy_type VARCHAR(255);
ALTER TABLE connettori ADD COLUMN proxy_hostname VARCHAR(255);
ALTER TABLE connettori ADD COLUMN proxy_port VARCHAR(255);
ALTER TABLE connettori ADD COLUMN proxy_username VARCHAR(255);
ALTER TABLE connettori ADD COLUMN proxy_password VARCHAR(255);

-- Redirect
ALTER TABLE connettori ADD COLUMN redirect_mode VARCHAR(255);
ALTER TABLE connettori ADD COLUMN redirect_max_hop INT;

