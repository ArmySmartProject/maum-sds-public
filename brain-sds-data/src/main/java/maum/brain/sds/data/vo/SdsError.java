package maum.brain.sds.data.vo;

public class SdsError {
    private String code; // Error code
    private String error; // Error contents
    private String exceptionStr; // Exception string
    private Exception nowError;

    public SdsError() {
        code = "";
        error = "";
        exceptionStr = "";
    }

    public SdsError(String code, String exceptionStr, Exception e){
        this.code = code;
        this.error = e.toString();
        this.exceptionStr = exceptionStr;
        this.nowError = e;
    }

    public String getCode(){ return code; }

    public String getError(){ return error; }

    public String getExceptionStr(){ return exceptionStr; }

    public String jsonString(){
        return "{ \"code\": \"" + code + "\", \"error\": \"" + error + "\", \"exceptionStr\": \""
                + exceptionStr + "\", \"function\" : "+SdsError.jsonString(nowError)+"  }";
    }

    public String userJson(){
        return "{ \"code\": \"" + code + "\", \"error\": \"" + error + "\", \"exceptionStr\": \""
                + exceptionStr +"\"  }";
    }

    public static String jsonString(Exception e){
        String functionListStr = "[";
        for(int i = 0; i<e.getStackTrace().length; i++){
            StackTraceElement nowErrorElem = e.getStackTrace()[i];
            functionListStr += "{\"File\" : \"" + nowErrorElem.getFileName()
                    + "\", \"Line\" : " + nowErrorElem.getLineNumber()
                    + ", \"Method\" : \"" + nowErrorElem.getMethodName() +
                    "\"}";
            if(i!=e.getStackTrace().length-1){
                functionListStr += ",";
            }else{
                functionListStr += "]";
            }
        }
        return functionListStr;
    }

    @Override
    public String toString() {
        return "SdsError{" +
                "code=" + code + ',' +
                "error='" + error + '\'' + "," +
                "exceptionStr='" + exceptionStr + '\'' +
                '}';
    }

    // Example
    //
    // 1. Static
    //    catch(Exception e) {logger.warn(SdsError.jsonString(e))}
    // 2. To Use Class
    //    catch(Exception e){
    //        SdsError sdsError = new SdsError("Custom Code","String to explain Error",e);
    //        logger.warn(sdsError.jsonString());
    //        logger.warn(sdsError.userJson()); // Remove error line & code
    //    }
}
