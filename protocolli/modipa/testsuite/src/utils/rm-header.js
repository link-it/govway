function rmHeaderCaseInsensitive(headers, headerName) {
  var lowerCaseHeaderName = headerName.toLowerCase();
  for (var key in headers) {
    karate.log("1: " + key);
    if (headers.hasOwnProperty(key)) {
      if (key.toLowerCase() === lowerCaseHeaderName) {
            karate.log("2: " + key);
            delete headers[key];
        return;
      }
    }
  }
}

