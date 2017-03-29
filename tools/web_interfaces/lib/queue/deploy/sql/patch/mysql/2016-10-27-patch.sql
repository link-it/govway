DROP INDEX operations_precedenti ON operations;
CREATE INDEX operations_precedenti ON operations (id,deleted,hostname,timereq DESC);

CREATE INDEX operations_gdo ON operations (timereq DESC);
