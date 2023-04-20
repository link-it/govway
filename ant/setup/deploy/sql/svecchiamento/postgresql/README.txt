# Esecuzione dello script per postgres
export PGPASSWORD=govwaypass; psql govwaydb govwayuser < svecchiamento.sql" >> "svecchiamento-$(date  +'%d').stdout.log" 2> "svecchiamento-$(date  +'%d').stderr.log" 

