java.text.DateFormat df = new java.text.SimpleDateFormat("mm/dd/yyyy hh:mma");
df.setTimeZone(java.util.TimeZone.getTimeZone("EST"));
java.util.Date date = df.parse(payload.get("LastTradeDate")+" "+payload.get("LastTradeTime"));

payload.put("tradeTimestamp", date.getTime())
payload.put("entryTimestamp", System.nanoTime())


return payload
