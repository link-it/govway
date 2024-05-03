function(headerName) {
   var ret = karate.get("karate.request.header('"+headerName+"')")
   if (!ret) {
      ret = karate.get("karate.request.header('"+karate.lowerCase(headerName)+"')")
   }
   if (ret) {
      return ret[0]
   }
   else {
      return null;
   }
}
