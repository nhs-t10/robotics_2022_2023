package org.firstinspires.ftc.teamcode.managers.telemetry.server;

public class HttpStatusCodeReplies {
    public static final String Continue = "HTTP/1.1 100 Continue\r\nContent-Length: 3\r\n\r\n100";

    public static final String Switching_Protocols = "HTTP/1.1 101 Switching Protocols\r\nContent-Length: 3\r\n\r\n101";

    public static final String Early_Hints = "HTTP/1.1 103 Early Hints\r\nContent-Length: 3\r\n\r\n103";

    public static final String OK = "HTTP/1.1 200 OK\r\nContent-Length: 3\r\n\r\n200";

    public static final String Created = "HTTP/1.1 201 Created\r\nContent-Length: 3\r\n\r\n201";

    public static final String Accepted = "HTTP/1.1 202 Accepted\r\nContent-Length: 3\r\n\r\n202";

    public static final String Non_Authoritative_Information = "HTTP/1.1 203 Non-Authoritative Information\r\nContent-Length: 3\r\n\r\n203";

    public static final String No_Content = "HTTP/1.1 204 No Content\r\nContent-Length: 3\r\n\r\n204";

    public static final String Reset_Content = "HTTP/1.1 205 Reset Content\r\nContent-Length: 3\r\n\r\n205";

    public static final String Partial_Content = "HTTP/1.1 206 Partial Content\r\nContent-Length: 3\r\n\r\n206";

    public static final String Multiple_Choices = "HTTP/1.1 300 Multiple Choices\r\nContent-Length: 3\r\n\r\n300";

    public static final String Moved_Permanently = "HTTP/1.1 301 Moved Permanently\r\nContent-Length: 3\r\n\r\n301";

    public static final String Found = "HTTP/1.1 302 Found\r\nContent-Length: 3\r\n\r\n302";

    public static final String See_Other = "HTTP/1.1 303 See Other\r\nContent-Length: 3\r\n\r\n303";

    public static final String Not_Modified = "HTTP/1.1 304 Not Modified\r\nContent-Length: 3\r\n\r\n304";

    public static final String Temporary_Redirect = "HTTP/1.1 307 Temporary Redirect\r\nContent-Length: 3\r\n\r\n307";

    public static final String Permanent_Redirect = "HTTP/1.1 308 Permanent Redirect\r\nContent-Length: 3\r\n\r\n308";

    public static final String Bad_Request = "HTTP/1.1 400 Bad Request\r\nContent-Length: 3\r\n\r\n400";

    public static final String Unauthorized = "HTTP/1.1 401 Unauthorized\r\nContent-Length: 3\r\n\r\n401";

    public static final String Payment_Required = "HTTP/1.1 402 Payment Required\r\nContent-Length: 3\r\n\r\n402";

    public static final String Forbidden = "HTTP/1.1 403 Forbidden\r\nContent-Length: 3\r\n\r\n403";

    public static final String Not_Found = "HTTP/1.1 404 Not Found\r\nContent-Length: 3\r\n\r\n404";

    public static final String Method_Not_Allowed = "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 3\r\n\r\n405";

    public static final String Not_Acceptable = "HTTP/1.1 406 Not Acceptable\r\nContent-Length: 3\r\n\r\n406";

    public static final String Proxy_Authentication_Required = "HTTP/1.1 407 Proxy Authentication Required\r\nContent-Length: 3\r\n\r\n407";

    public static final String Request_Timeout = "HTTP/1.1 408 Request Timeout\r\nContent-Length: 3\r\n\r\n408";

    public static final String Conflict = "HTTP/1.1 409 Conflict\r\nContent-Length: 3\r\n\r\n409";

    public static final String Gone = "HTTP/1.1 410 Gone\r\nContent-Length: 3\r\n\r\n410";

    public static final String Length_Required = "HTTP/1.1 411 Length Required\r\nContent-Length: 3\r\n\r\n411";

    public static final String Precondition_Failed = "HTTP/1.1 412 Precondition Failed\r\nContent-Length: 3\r\n\r\n412";

    public static final String Payload_Too_Large = "HTTP/1.1 413 Payload Too Large\r\nContent-Length: 3\r\n\r\n413";

    public static final String URI_Too_Long = "HTTP/1.1 414 URI Too Long\r\nContent-Length: 3\r\n\r\n414";

    public static final String Unsupported_Media_Type = "HTTP/1.1 415 Unsupported Media Type\r\nContent-Length: 3\r\n\r\n415";

    public static final String Range_Not_Satisfiable = "HTTP/1.1 416 Range Not Satisfiable\r\nContent-Length: 3\r\n\r\n416";

    public static final String Expectation_Failed = "HTTP/1.1 417 Expectation Failed\r\nContent-Length: 3\r\n\r\n417";

    public static final String I_m_a_teapot = "HTTP/1.1 418 I'm a teapot\r\nContent-Length: 3\r\n\r\n418";

    public static final String Unprocessable_Entity = "HTTP/1.1 422 Unprocessable Entity\r\nContent-Length: 3\r\n\r\n422";

    public static final String Too_Early = "HTTP/1.1 425 Too Early\r\nContent-Length: 3\r\n\r\n425";

    public static final String Upgrade_Required = "HTTP/1.1 426 Upgrade Required\r\nContent-Length: 3\r\n\r\n426";

    public static final String Precondition_Required = "HTTP/1.1 428 Precondition Required\r\nContent-Length: 3\r\n\r\n428";

    public static final String Too_Many_Requests = "HTTP/1.1 429 Too Many Requests\r\nContent-Length: 3\r\n\r\n429";

    public static final String Request_Header_Fields_Too_Large = "HTTP/1.1 431 Request Header Fields Too Large\r\nContent-Length: 3\r\n\r\n431";

    public static final String Unavailable_For_Legal_Reasons = "HTTP/1.1 451 Unavailable For Legal Reasons\r\nContent-Length: 3\r\n\r\n451";

    public static final String Internal_Server_Error = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 3\r\n\r\n500";

    public static final String Not_Implemented = "HTTP/1.1 501 Not Implemented\r\nContent-Length: 3\r\n\r\n501";

    public static final String Bad_Gateway = "HTTP/1.1 502 Bad Gateway\r\nContent-Length: 3\r\n\r\n502";

    public static final String Service_Unavailable = "HTTP/1.1 503 Service Unavailable\r\nContent-Length: 3\r\n\r\n503";

    public static final String Gateway_Timeout = "HTTP/1.1 504 Gateway Timeout\r\nContent-Length: 3\r\n\r\n504";

    public static final String HTTP_Version_Not_Supported = "HTTP/1.1 505 HTTP Version Not Supported\r\nContent-Length: 3\r\n\r\n505";

    public static final String Variant_Also_Negotiates = "HTTP/1.1 506 Variant Also Negotiates\r\nContent-Length: 3\r\n\r\n506";

    public static final String Insufficient_Storage = "HTTP/1.1 507 Insufficient Storage\r\nContent-Length: 3\r\n\r\n507";

    public static final String Loop_Detected = "HTTP/1.1 508 Loop Detected\r\nContent-Length: 3\r\n\r\n508";

    public static final String Not_Extended = "HTTP/1.1 510 Not Extended\r\nContent-Length: 3\r\n\r\n510";

    public static final String Network_Authentication_Required = "HTTP/1.1 511 Network Authentication Required\r\nContent-Length: 3\r\n\r\n511";

    static String Continue(String reason) {
        return "HTTP/1.1 100 Continue\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Switching_Protocols(String reason) {
        return "HTTP/1.1 101 Switching Protocols\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Early_Hints(String reason) {
        return "HTTP/1.1 103 Early Hints\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String OK(String reason) {
        return "HTTP/1.1 200 OK\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Created(String reason) {
        return "HTTP/1.1 201 Created\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Accepted(String reason) {
        return "HTTP/1.1 202 Accepted\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Non_Authoritative_Information(String reason) {
        return "HTTP/1.1 203 Non-Authoritative Information\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String No_Content(String reason) {
        return "HTTP/1.1 204 No Content\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Reset_Content(String reason) {
        return "HTTP/1.1 205 Reset Content\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Partial_Content(String reason) {
        return "HTTP/1.1 206 Partial Content\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Multiple_Choices(String reason) {
        return "HTTP/1.1 300 Multiple Choices\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Moved_Permanently(String reason) {
        return "HTTP/1.1 301 Moved Permanently\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Found(String reason) {
        return "HTTP/1.1 302 Found\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String See_Other(String reason) {
        return "HTTP/1.1 303 See Other\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Not_Modified(String reason) {
        return "HTTP/1.1 304 Not Modified\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Temporary_Redirect(String reason) {
        return "HTTP/1.1 307 Temporary Redirect\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Permanent_Redirect(String reason) {
        return "HTTP/1.1 308 Permanent Redirect\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Bad_Request(String reason) {
        return "HTTP/1.1 400 Bad Request\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Unauthorized(String reason) {
        return "HTTP/1.1 401 Unauthorized\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Payment_Required(String reason) {
        return "HTTP/1.1 402 Payment Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Forbidden(String reason) {
        return "HTTP/1.1 403 Forbidden\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Not_Found(String reason) {
        return "HTTP/1.1 404 Not Found\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Method_Not_Allowed(String reason) {
        return "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Not_Acceptable(String reason) {
        return "HTTP/1.1 406 Not Acceptable\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Proxy_Authentication_Required(String reason) {
        return "HTTP/1.1 407 Proxy Authentication Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Request_Timeout(String reason) {
        return "HTTP/1.1 408 Request Timeout\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Conflict(String reason) {
        return "HTTP/1.1 409 Conflict\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Gone(String reason) {
        return "HTTP/1.1 410 Gone\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Length_Required(String reason) {
        return "HTTP/1.1 411 Length Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Precondition_Failed(String reason) {
        return "HTTP/1.1 412 Precondition Failed\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Payload_Too_Large(String reason) {
        return "HTTP/1.1 413 Payload Too Large\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String URI_Too_Long(String reason) {
        return "HTTP/1.1 414 URI Too Long\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Unsupported_Media_Type(String reason) {
        return "HTTP/1.1 415 Unsupported Media Type\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Range_Not_Satisfiable(String reason) {
        return "HTTP/1.1 416 Range Not Satisfiable\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Expectation_Failed(String reason) {
        return "HTTP/1.1 417 Expectation Failed\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String I_m_a_teapot(String reason) {
        return "HTTP/1.1 418 I'm a teapot\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Unprocessable_Entity(String reason) {
        return "HTTP/1.1 422 Unprocessable Entity\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Too_Early(String reason) {
        return "HTTP/1.1 425 Too Early\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Upgrade_Required(String reason) {
        return "HTTP/1.1 426 Upgrade Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Precondition_Required(String reason) {
        return "HTTP/1.1 428 Precondition Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Too_Many_Requests(String reason) {
        return "HTTP/1.1 429 Too Many Requests\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Request_Header_Fields_Too_Large(String reason) {
        return "HTTP/1.1 431 Request Header Fields Too Large\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Unavailable_For_Legal_Reasons(String reason) {
        return "HTTP/1.1 451 Unavailable For Legal Reasons\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Internal_Server_Error(String reason) {
        return "HTTP/1.1 500 Internal Server Error\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Not_Implemented(String reason) {
        return "HTTP/1.1 501 Not Implemented\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Bad_Gateway(String reason) {
        return "HTTP/1.1 502 Bad Gateway\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Service_Unavailable(String reason) {
        return "HTTP/1.1 503 Service Unavailable\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Gateway_Timeout(String reason) {
        return "HTTP/1.1 504 Gateway Timeout\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String HTTP_Version_Not_Supported(String reason) {
        return "HTTP/1.1 505 HTTP Version Not Supported\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Variant_Also_Negotiates(String reason) {
        return "HTTP/1.1 506 Variant Also Negotiates\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Insufficient_Storage(String reason) {
        return "HTTP/1.1 507 Insufficient Storage\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Loop_Detected(String reason) {
        return "HTTP/1.1 508 Loop Detected\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Not_Extended(String reason) {
        return "HTTP/1.1 510 Not Extended\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }
    static String Network_Authentication_Required(String reason) {
        return "HTTP/1.1 511 Network Authentication Required\r\nContent-Length: " + reason.getBytes().length + "\r\n\r\n" + reason;
    }


}
