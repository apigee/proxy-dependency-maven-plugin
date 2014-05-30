var path = context.getVariable("request.path");
context.setVariable('target.copy.pathsuffix',false);

dynamictargeturi= path;

context.setVariable("uri",encodeURI(uri));