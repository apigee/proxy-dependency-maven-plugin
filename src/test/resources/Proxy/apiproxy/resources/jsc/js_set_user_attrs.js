var upmid = context.getVariable("upmid");
var slCheck = context.getVariable("slCheck");
var plusid = context.getVariable("plusid");
var userpin = context.getVariable("userpin");
var nikePlusCookie = context.getVariable("nikePlusCookie");

upmid=upmid?upmid:"";
slCheck=slCheck?slCheck:"";
plusid=plusid?plusid:"";
userpin=userpin?userpin:"";
nikePlusCookie=nikePlusCookie?nikePlusCookie:"";

context.setVariable("local_usr_attr1","upmid: "+upmid+", slcheck: "+slCheck);
context.setVariable("local_usr_attr2","plusid: "+plusid+", pintoken: "+userpin);
context.setVariable("local_usr_attr3","nikePlusCookie: "+nikePlusCookie);
