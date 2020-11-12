function(headerName) {
   var ret = karate.get("requestHeaders['"+headerName+"']")
   if (!ret) {
      ret = karate.get("requestHeaders['"+karate.lowerCase(headerName)+"']")
   }
   if (ret) {
      return ret[0]
   }
   else {
      return null;
   }
}
