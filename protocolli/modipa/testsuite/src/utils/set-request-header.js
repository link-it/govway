function(headerName,value) {
   var ret = karate.get("requestHeaders['"+headerName+"']")
   if (!ret) {
      ret = karate.get("requestHeaders['"+karate.lowerCase(headerName)+"']")
   }
   if (ret) {
      ret[0]=value
   }
}
