function(headerName,value) {
   var ret = karate.get("responseHeaders['"+headerName+"']")
   if (!ret) {
      ret = karate.get("responseHeaders['"+karate.lowerCase(headerName)+"']")
   }
   if (ret) {
      return ret[0]=value
   }
}
