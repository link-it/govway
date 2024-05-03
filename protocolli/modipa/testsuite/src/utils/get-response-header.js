function(headerName) {
   var ret = karate.get("karate.response.header('"+headerName+"')")
   if (!ret) {
      ret = karate.get("karate.response.header('"+karate.lowerCase(headerName)+"')")
   }
   if (ret) {
      return ret[0]
   }
   else {
      return null;
   }
}
