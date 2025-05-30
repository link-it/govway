function check_csv(csv, content) {
	rows = csv.split('\n');
	headers = rows[0].split(',');
	
	headersIndex = {}
	for (i = 0; i < headers.length; i++)
		headersIndex[headers[i].replace('\r', '')] = i;
	
	table = {}
	counter = 0
	for (i = 1; i < rows.length; i++) {
		row = rows[i].replace('\r', '').split(',')
		
		if (row.length != headers.length)
			continue;
		
		key = row[headersIndex['purpose_id']] + "_" 
			+ row[headersIndex['requests_count']] + "_" 
			+ row[headersIndex['stato']] + "_" 
			+ row[headersIndex['date']];
		table[key] = true;
		counter++
	}
	
	for (i = 0; i < content.length; i++) {
		key = content[i].purpose_id + "_"
			+ content[i].requests_count + "_"
			+ content[i].status + "_"
			+ content[i].date;
		if (table[key] == null)
			karate.fail("Il contenuto del csv non corrisponde con quello atteso, riga non presente: " + key)
		table[key] = null;
		counter--
	}
	
	if (counter != 0)
		karate.fail("Il contenuto del csv non corrisponde con quello atteso")
}