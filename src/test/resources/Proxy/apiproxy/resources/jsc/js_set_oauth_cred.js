var client_id = context.getVariable("local_clientid");
var client_secret = context.getVariable("local_secret");
context.setVariable("request.header.Authorization","Basic "+CryptoJS.enc.Base64.stringify(CryptoJS.enc.Latin1.parse(client_id + ':' + client_secret)));
context.setVariable("grant_type","password");
