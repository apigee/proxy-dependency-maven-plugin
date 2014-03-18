//This is a verbatim conversion of an exiting python in 3.8

//sample reference
//<user_attribute1>upmid:1627339, slCheck:,</user_attribute1>
//<user_attribute2>plusid:8f3d0c63-1e43-42ef-b1d0-acd5cd3a25d7, pinToken:,</user_attribute2>
//<user_attribute3>nikePlusCookie:,</user_attribute3>

var userattr1 = context.getVariable('kms.user_attribute1');
var userattr2 = context.getVariable('kms.user_attribute2');
var userattr3 = context.getVariable('kms.user_attribute3');
var oldFormat = false;

if(userattr1 == null || userattr1 == ""){
	userattr1 = context.getVariable('request.queryparam.user_attribute1');
}
if(userattr2 == null || userattr2 == ""){
	userattr2 = context.getVariable('request.queryparam.user_attribute2');
}
if(userattr3 == null || userattr3 == ""){
	userattr3 = context.getVariable('request.queryparam.user_attribute3');
}
if(userattr1 && (userattr1 != null || userattr1 != "")){
	tempattr = userattr1.split(",")[0].split(":")[1];
	if (tempattr !== null || tempattr == "") {
		context.setVariable('upmid', tempattr);
		
	} else{ 
		//oldFormat
		oldFormat = true;
		context.setVariable('upmid', userattr1);
		if (userattr1.match(/slCheck/gi)){
			context.setVariable('slcheck', userattr1.split(",")[1].split(":")[1]);
		}
	};	

}

if(userattr2 && (userattr2 != null || userattr2 != "")){
	context.setVariable('plusUserId', userattr2.split(",")[0].split(":")[1]);
	if (userattr2.match(/pin/gi)){
	   context.setVariable('pintoken', userattr2.split(",")[1].split(":")[1]);
	}
	
	if(oldFormat){
		context.setVariable('slcheck',userattr2);
	}
}

if(userattr3 && (userattr3 != null || userattr3 != "")){
	context.setVariable('nikePlusCookie', userattr3.split(",")[0].split(":")[1]);
	if(oldFormat){
		context.setVariable('plusUserId', userattr3);
	}

}

