var salt = context.getVariable("trusted_headers_salt");
var upmid = context.getVariable("upmid");
var host = context.getVariable("request.header.Host");

//get the time the request was sent:
var now = moment().format('YYYY-MM-DD hh:mm:ss.SSS')+'000' //microsecond is always zero

var unhashed_token = upmid + "|" + now + "|" + host

//generate a hash with the unhashedToken:
var sha512 = CryptoJS.algo.SHA512.create()
sha512.update(salt);
sha512.update(unhashed_token);
var hashed_token = sha512.finalize();

//convert to hex values
var hex_token = hashed_token.toString(CryptoJS.enc.Hex);
var words = CryptoJS.enc.Latin1.parse(hex_token);

//base64 encode the hashedToken:
var base64_token =  CryptoJS.enc.Base64.stringify(words);
context.setVariable("request.header.X-NIKE-SID",host);
context.setVariable("request.header.X-NIKE-TS", now);
context.setVariable("request.header.X-NIKE-TOKEN", base64_token);