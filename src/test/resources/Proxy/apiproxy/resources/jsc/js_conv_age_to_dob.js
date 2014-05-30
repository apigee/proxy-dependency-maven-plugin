var age = context.getVariable("local_age");
var isNumber=function (n) { return !isNaN(parseFloat(n)) && isFinite(n);}
if(isNumber(age)){
    var userJSON=context.getVariable('local_userjson');
    var userObj=JSON.parse(userJSON);
    userObj.User.dobDay="1";
    userObj.User.dobMonth="1";
    userObj.User.dobYear=(new Date().getFullYear()-age).toString();
    context.setVariable('request.formparam.object',JSON.stringify(userObj));
}
