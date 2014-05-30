var accesstoken_valid =false;
var accesstoken_valid = !context.getVariable("oauthV2.failed");
context.setVariable("accesstoken_valid", accesstoken_valid);
