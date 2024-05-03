function(headerName,value) {
   var ret = karate.get("karate.request.header('"+headerName+"')")
   if (!ret) {
      ret = karate.get("karate.request.header('"+karate.lowerCase(headerName)+"')")
   }
   if (ret) {
      ret[0]=value
   }
}
