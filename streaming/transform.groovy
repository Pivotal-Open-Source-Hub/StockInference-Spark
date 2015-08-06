if (!payload.containsKey("entryTimestamp")){
   if (payload.containsKey("timestamp")){
      payload.put("entryTimestamp", payload.get("timestamp"))
      payload.remove("timestamp")
   }
   else payload.put("entryTimestamp", System.nanoTime()) 
}
companyName=payload.get("Name")
if (companyName!=null && companyName.indexOf(",")!=-1) payload.put ("Name",companyName.replaceAll(",",""))


return payload
