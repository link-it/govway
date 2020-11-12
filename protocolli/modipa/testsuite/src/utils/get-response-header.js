function(headerName) {
   var ret = karate.get("responseHeaders['"+headerName+"']")
   if (!ret) {
      ret = karate.get("responseHeaders['"+karate.lowerCase(headerName)+"']")
   }
   if (ret) {
      return ret[0]
   }
   else {
      return null;
   }
}
