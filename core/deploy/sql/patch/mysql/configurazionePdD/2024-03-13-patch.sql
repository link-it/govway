update configurazione set tracciamento_esiti = CONCAT(tracciamento_esiti, ',53') where tracciamento_esiti is not null;
update configurazione set tracciamento_esiti_pd = CONCAT(tracciamento_esiti_pd, ',53') where tracciamento_esiti_pd is not null;
