package global.common.models

import global.common.models.HttpResponseCode.Companion.entries
import global.common.models.HttpResponseCode.Error.ClientError.Companion.entries
import global.common.models.HttpResponseCode.Error.Companion.entries
import global.common.models.HttpResponseCode.Information.Companion.entries
import global.common.models.HttpResponseCode.Redirect.Companion.entries
import global.common.models.HttpResponseCode.Success.Companion.entries

/** HTTP response codes as defined in [RFC 9110](https://httpwg.org/specs/rfc9110.html#overview.of.status.codes). */
open class HttpResponseCode(
    val code: Int,
    val title: String,
    val description: String? = null,
) {
    /** The status code is between 100 and 199. */
    val isInformational get() = code in 100..199

    /** The status code is between 200 and 299. */
    val isSuccess get() = code in 200..299

    /** The status code is between 300 and 399. */
    val isRedirection get() = code in 300..399

    /** The status code is between 400 and 599. */
    val isError get() = code in 400..599

    /** The status code is between 400 and 499. */
    val isClientError get() = code in 400..499

    /** The status code is between 500 and 599. */
    val isServerError get() = code in 500..599

    /** The status code is not listed in RFC 9110. */
    val isOther get() = fromCode(code) == null

    companion object {
        /** Get the [HttpResponseCode] for the given code.
         * @return Known [HttpResponseCode] from [entries] or `null` if not found */
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code }

        val entries
            get() = listOf(
                Continue, SwitchingProtocols, Processing, EarlyHints,
                Ok, Created, Accepted, NonAuthoritativeInformation, NoContent,
                ResetContent, PartialContent, MultiStatus, AlreadyReported,
                IMUsed, MultipleChoices, MovedPermanently, Found, SeeOther,
                NotModified, UseProxy, Unused, TemporaryRedirect, PermanentRedirect,
                BadRequest, Unauthorized, PaymentRequired, Forbidden, NotFound,
                MethodNotAllowed, NotAcceptable, ProxyAuthenticationRequired, RequestTimeout,
                Conflict, Gone, LengthRequired, PreconditionFailed, RequestEntityTooLarge,
                RequestUriTooLong, UnsupportedMediaType, RequestedRangeNotSatisfiable,
                ExpectationFailed, ImATeapot, MisdirectedRequest, UnprocessableEntity, Locked,
                FailedDependency, TooEarly, UpgradeRequired, PreconditionRequired, TooManyRequests,
                RequestHeaderFieldsTooLarge, UnavailableForLegalReasons, InternalServerError,
                NotImplemented, BadGateway, ServiceUnavailable, GatewayTimeout,
                HttpVersionNotSupported, VariantAlsoNegotiates, InsufficientStorage,
                LoopDetected, NotExtended, NetworkAuthenticationRequired
            )
    }

    /** Informational responses (100 – 199) */
    open class Information(code: Int, title: String, description: String? = null) :
        HttpResponseCode(code, title, description) {
        companion object {
            /** Get the [HttpResponseCode] ([Information]) for the given code.
             * @return Known [HttpResponseCode] ([Information]) from [entries] or `null` if not found */
            fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
            val entries
                get() = listOf(
                    Continue, SwitchingProtocols, Processing, EarlyHints
                )
        }
    }

    /** Successful responses (200 – 299) */
    open class Success(code: Int, title: String, description: String? = null) :
        HttpResponseCode(code, title, description) {
        companion object {
            /** Get the [HttpResponseCode] ([Success]) for the given code.
             * @return Known [HttpResponseCode] ([Success]) from [entries] or `null` if not found */
            fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
            val entries
                get() = listOf(
                    Ok, Created, Accepted, NonAuthoritativeInformation, NoContent,
                    ResetContent, PartialContent, MultiStatus, AlreadyReported,
                    IMUsed
                )
        }
    }

    /** Redirection messages (300 – 399) */
    open class Redirect(code: Int, title: String, description: String? = null) :
        HttpResponseCode(code, title, description) {
        companion object {
            /** Get the [HttpResponseCode] ([Redirect]) for the given code.
             * @return Known [HttpResponseCode] ([Redirect]) from [entries] or `null` if not found */
            fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
            val entries
                get() = listOf(
                    MultipleChoices, MovedPermanently, Found, SeeOther,
                    NotModified, UseProxy, Unused, TemporaryRedirect, PermanentRedirect
                )
        }
    }

    /** Client/Server error responses (400 – 599) */
    sealed class Error(code: Int, title: String, description: String? = null) :
        HttpResponseCode(code, title, description) {
        companion object {
            /** Get the [HttpResponseCode] ([Error]) for the given code.
             * @return Known [HttpResponseCode] ([Error]) from [entries] or `null` if not found */
            fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
            val entries
                get() = listOf(
                    BadRequest, Unauthorized, PaymentRequired, Forbidden, NotFound,
                    MethodNotAllowed, NotAcceptable, ProxyAuthenticationRequired, RequestTimeout,
                    Conflict, Gone, LengthRequired, PreconditionFailed, RequestEntityTooLarge,
                    RequestUriTooLong, UnsupportedMediaType, RequestedRangeNotSatisfiable,
                    ExpectationFailed, ImATeapot, MisdirectedRequest, UnprocessableEntity, Locked,
                    FailedDependency, TooEarly, UpgradeRequired, PreconditionRequired,
                    TooManyRequests, RequestHeaderFieldsTooLarge, UnavailableForLegalReasons,
                    InternalServerError, NotImplemented, BadGateway, ServiceUnavailable,
                    GatewayTimeout, HttpVersionNotSupported, VariantAlsoNegotiates,
                    InsufficientStorage, LoopDetected, NotExtended, NetworkAuthenticationRequired
                )
        }

        /** Client error responses (400 – 499) */
        open class ClientError(code: Int, title: String, description: String? = null) :
            Error(code, title, description) {
            companion object {
                /** Get the [HttpResponseCode] ([ClientError]) for the given code.
                 * @return Known [HttpResponseCode] ([ClientError]) from [entries] or `null` if not found */
                fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
                val entries
                    get() = listOf(
                        BadRequest, Unauthorized, PaymentRequired, Forbidden, NotFound,
                        MethodNotAllowed, NotAcceptable, ProxyAuthenticationRequired,
                        RequestTimeout, Conflict, Gone, LengthRequired, PreconditionFailed,
                        RequestEntityTooLarge, RequestUriTooLong, UnsupportedMediaType,
                        RequestedRangeNotSatisfiable, ExpectationFailed, ImATeapot,
                        MisdirectedRequest, UnprocessableEntity, Locked, FailedDependency,
                        TooEarly, UpgradeRequired, PreconditionRequired, TooManyRequests,
                        RequestHeaderFieldsTooLarge, UnavailableForLegalReasons
                    )
            }
        }

        /** Client error responses (500 – 599) */
        open class ServerError(code: Int, title: String, description: String? = null) :
            Error(code, title, description) {
            companion object {
                /** Get the [HttpResponseCode] ([ServerError]) for the given code.
                 * @return Known [HttpResponseCode] ([ServerError]) from [entries] or `null` if not found */
                fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
                val entries
                    get() = listOf(
                        InternalServerError, NotImplemented, BadGateway, ServiceUnavailable,
                        GatewayTimeout, HttpVersionNotSupported, VariantAlsoNegotiates,
                        InsufficientStorage, LoopDetected, NotExtended,
                        NetworkAuthenticationRequired
                    )
            }
        }
    }


    data object NoResponse : HttpResponseCode(0, "No Response")

    /** 100 - This interim response indicates that the client should continue the request or ignore the response if the request is already finished. */
    data object Continue : Information(
        100,
        "Continue",
        "This interim response indicates that the client should continue the request or ignore the response if the request is already finished."
    )

    /** 101 - This code is sent in response to an Upgrade request header from the client and indicates the protocol the server is switching to. */
    data object SwitchingProtocols : Information(
        101,
        "Switching Protocols",
        "This code is sent in response to an Upgrade request header from the client and indicates the protocol the server is switching to."
    )

    /** 102 - This code indicates that the server has received and is processing the request, but no response is available yet. */
    data object Processing : Information(
        102,
        "Processing",
        "This code indicates that the server has received and is processing the request, but no response is available yet."
    )

    /** 103 - This status code is primarily intended to be used with the Link header, letting the user agent start preloading resources while the server prepares a response or preconnect to an origin from which the page will need resources. */
    data object EarlyHints : Information(
        103,
        "Early Hints",
        "This status code is primarily intended to be used with the Link header, letting the user agent start preloading resources while the server prepares a response or preconnect to an origin from which the page will need resources."
    )

    /**
     * 200 - The request succeeded. The result meaning of "success" depends on the HTTP method:
     * - GET: The resource has been fetched and transmitted in the message body.
     * - HEAD: The representation headers are included in the response without any message body.
     * - PUT or POST: The resource describing the result of the action is transmitted in the message body.
     * - TRACE: The message body contains the request message as received by the server.
     */
    data object Ok : Success(
        200,
        "OK",
        "The request succeeded. The result meaning of \"success\" depends on the HTTP method:\n" +
                "- GET: The resource has been fetched and transmitted in the message body.\n" +
                "- HEAD: The representation headers are included in the response without any message body.\n" +
                "- PUT or POST: The resource describing the result of the action is transmitted in the message body.\n" +
                "- TRACE: The message body contains the request message as received by the server."
    )

    /** 201 - The request succeeded, and a new resource was created as a result. This is typically the response sent after POST requests, or some PUT requests. */
    data object Created : Success(
        201,
        "Created",
        "The request succeeded, and a new resource was created as a result. This is typically the response sent after POST requests, or some PUT requests."
    )

    /** 202 - The request has been received but not yet acted upon. It is noncommittal, since there is no way in HTTP to later send an asynchronous response indicating the outcome of the request. It is intended for cases where another process or server handles the request, or for batch processing. */
    data object Accepted : Success(
        202,
        "Accepted",
        "The request has been received but not yet acted upon. It is noncommittal, since there is no way in HTTP to later send an asynchronous response indicating the outcome of the request. It is intended for cases where another process or server handles the request, or for batch processing."
    )

    /** 203 - This response code means the returned metadata is not exactly the same as is available from the origin server, but is collected from a local or a third-party copy. This is mostly used for mirrors or backups of another resource. Except for that specific case, the 200 OK response is preferred to this status. */
    data object NonAuthoritativeInformation : Success(
        203,
        "Non-Authoritative Information",
        "This response code means the returned metadata is not exactly the same as is available from the origin server, but is collected from a local or a third-party copy. This is mostly used for mirrors or backups of another resource. Except for that specific case, the 200 OK response is preferred to this status."
    )

    /** 204 - There is no content to send for this request, but the headers may be useful. The user agent may update its cached headers for this resource with the new ones.  */
    data object NoContent : Success(
        204,
        "No Content",
        "There is no content to send for this request, but the headers may be useful. The user agent may update its cached headers for this resource with the new ones."
    )

    /** 205 - Tells the user agent to reset the document which sent this request. */
    data object ResetContent : Success(
        205,
        "Reset Context",
        "Tells the user agent to reset the document which sent this request."
    )

    /** 206 - This response code is used when the Range header is sent from the client to request only part of a resource. */
    data object PartialContent : Success(
        206,
        "Partial Content",
        "This response code is used when the Range header is sent from the client to request only part of a resource."
    )

    /** 207 - Conveys information about multiple resources, for situations where multiple status codes might be appropriate. */
    data object MultiStatus : Success(
        207,
        "Multi-Status",
        "Conveys information about multiple resources, for situations where multiple status codes might be appropriate."
    )

    /** 208 - Used inside a <dav:propstat> response element to avoid repeatedly enumerating the internal members of multiple bindings to the same collection. */
    data object AlreadyReported : Success(
        208,
        "Already Reported",
        "Used inside a <dav:propstat> response element to avoid repeatedly enumerating the internal members of multiple bindings to the same collection."
    )

    /** 226 - The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance. */
    data object IMUsed : Success(
        226,
        "IM Used",
        "The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance."
    )

    /** 300 - The request has more than one possible response. The user agent or user should choose one of them. (There is no standardized way of choosing one of the responses, but HTML links to the possibilities are recommended so the user can pick.) */
    data object MultipleChoices : Redirect(
        300,
        "Multiple Choices",
        "The request has more than one possible response. The user agent or user should choose one of them. (There is no standardized way of choosing one of the responses, but HTML links to the possibilities are recommended so the user can pick.)"
    )

    /** 301 - The URL of the requested resource has been changed permanently. The new URL is given in the response. */
    data object MovedPermanently : Redirect(
        301,
        "Moved Permanently",
        "The URL of the requested resource has been changed permanently. The new URL is given in the response."
    )

    /** 302 - This response code means that the URI of requested resource has been changed temporarily. Further changes in the URI might be made in the future. Therefore, this same URI should be used by the client in future requests. */
    data object Found : Redirect(
        302,
        "Found",
        "This response code means that the URI of requested resource has been changed temporarily. Further changes in the URI might be made in the future. Therefore, this same URI should be used by the client in future requests. "
    )

    /** 303 - The server sent this response to direct the client to get the requested resource at another URI with a GET request. */
    data object SeeOther : Redirect(
        303,
        "See Other",
        "The server sent this response to direct the client to get the requested resource at another URI with a GET request."
    )

    /** 304 - This is used for caching purposes. It tells the client that the response has not been modified, so the client can continue to use the same cached version of the response. */
    data object NotModified : Redirect(
        304,
        "Not Modified",
        "This is used for caching purposes. It tells the client that the response has not been modified, so the client can continue to use the same cached version of the response."
    )

    /** 305 - Defined in a previous version of the HTTP specification to indicate that a requested response must be accessed by a proxy. It has been deprecated due to security concerns regarding in-band configuration of a proxy. */
    data object UseProxy : Redirect(
        305,
        "Use Proxy",
        "Defined in a previous version of the HTTP specification to indicate that a requested response must be accessed by a proxy. It has been deprecated due to security concerns regarding in-band configuration of a proxy. "
    )

    /** 306 - This response code is no longer used; it is just reserved. It was used in a previous version of the HTTP/1.1 specification. */
    data object Unused : Redirect(
        306,
        "Unused",
        "This response code is no longer used; it is just reserved. It was used in a previous version of the HTTP/1.1 specification."
    )

    /** 307 - The server sends this response to direct the client to get the requested resource at another URI with the same method that was used in the prior request. This has the same semantics as the 302 Found HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request. */
    data object TemporaryRedirect : Redirect(
        307,
        "Temporary Redirect",
        "The server sends this response to direct the client to get the requested resource at another URI with the same method that was used in the prior request. This has the same semantics as the 302 Found HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request."
    )

    /** 308 - This means that the resource is now permanently located at another URI, specified by the Location: HTTP Response header. This has the same semantics as the 301 Moved Permanently HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request. */
    data object PermanentRedirect : Redirect(
        308,
        "Permanent Redirect",
        "This means that the resource is now permanently located at another URI, specified by the Location: HTTP Response header. This has the same semantics as the 301 Moved Permanently HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request. "
    )

    /** 400 - The server cannot or will not process the request due to something that is perceived to be a client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing). */
    data object BadRequest : Error.ClientError(
        400,
        "Bad Request",
        "The server cannot or will not process the request due to something that is perceived to be a client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing)."
    )

    /** 401 - Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated". That is, the client must authenticate itself to get the requested response. */
    data object Unauthorized : Error.ClientError(
        401,
        "Unauthorized",
        "Although the HTTP standard specifies \"unauthorized\", semantically this response means \"unauthenticated\". That is, the client must authenticate itself to get the requested response."
    )

    /** 402 - This response code is reserved for future use. The initial aim for creating this code was using it for digital payment systems, however this status code is used very rarely and no standard convention exists. */
    data object PaymentRequired : Error.ClientError(
        402,
        "Payment Required",
        "This response code is reserved for future use. The initial aim for creating this code was using it for digital payment systems, however this status code is used very rarely and no standard convention exists."
    )

    /** 403 - The client does not have access rights to the content; that is, it is unauthorized, so the server is refusing to give the requested resource. Unlike 401 Unauthorized, the client's identity is known to the server. */
    data object Forbidden : Error.ClientError(
        403,
        "Forbidden",
        "The client does not have access rights to the content; that is, it is unauthorized, so the server is refusing to give the requested resource. Unlike 401 Unauthorized, the client's identity is known to the server. "
    )

    /** 404 - The server cannot find the requested resource. In the browser, this means the URL is not recognized. In an API, this can also mean that the endpoint is valid but the resource itself does not exist. Servers may also send this response instead of 403 Forbidden to hide the existence of a resource from an unauthorized client. This response code is probably the most well known due to its frequent occurrence on the web. */
    data object NotFound : Error.ClientError(
        404,
        "Not Found",
        "The server cannot find the requested resource. In the browser, this means the URL is not recognized. In an API, this can also mean that the endpoint is valid but the resource itself does not exist. Servers may also send this response instead of 403 Forbidden to hide the existence of a resource from an unauthorized client. This response code is probably the most well known due to its frequent occurrence on the web."
    )

    /** 405 - The request method is known by the server but is not supported by the target resource. For example, an API may not allow calling DELETE to remove a resource. */
    data object MethodNotAllowed : Error.ClientError(
        405,
        "Method Not Allowed",
        "The request method is known by the server but is not supported by the target resource. For example, an API may not allow calling DELETE to remove a resource."
    )

    /** 406 - This response is sent when the web server, after performing server-driven content negotiation, doesn't find any content that conforms to the criteria given by the user agent. */
    data object NotAcceptable : Error.ClientError(
        406,
        "Not Acceptable",
        "This response is sent when the web server, after performing server-driven content negotiation, doesn't find any content that conforms to the criteria given by the user agent."
    )

    /** 407 - This is similar to 401 but authentication is needed to be done by a proxy. */
    data object ProxyAuthenticationRequired : Error.ClientError(
        407,
        "Proxy Authentication Required",
        "This is similar to 401 Unauthorized but authentication is needed to be done by a proxy."
    )

    /** 408 - This response is sent on an idle connection by some servers, even without any previous request by the client. It means that the server would like to shut down this unused connection. This response is used much more since some browsers, like Chrome, Firefox 27+, or IE9, use HTTP pre-connection mechanisms to speed up surfing. Also note that some servers merely shut down the connection without sending this message. */
    data object RequestTimeout : Error.ClientError(
        408,
        "Request Timeout",
        "This response is sent on an idle connection by some servers, even without any previous request by the client. It means that the server would like to shut down this unused connection. This response is used much more since some browsers, like Chrome, Firefox 27+, or IE9, use HTTP pre-connection mechanisms to speed up surfing. Also note that some servers merely shut down the connection without sending this message."
    )

    /** 409 - This response is sent when a request conflicts with the current state of the server. */
    data object Conflict : Error.ClientError(
        409,
        "Conflict",
        "This response is sent when a request conflicts with the current state of the server."
    )

    /** 410 - This response is sent when the requested content has been permanently deleted from server, with no forwarding address. Clients are expected to remove their caches and links to the resource. The HTTP specification intends this status code to be used for "limited-time, promotional services". APIs should not feel compelled to indicate resources that have been deleted with this status code. */
    data object Gone : Error.ClientError(
        410,
        "Gone",
        "This response is sent when the requested content has been permanently deleted from server, with no forwarding address. Clients are expected to remove their caches and links to the resource. The HTTP specification intends this status code to be used for \"limited-time, promotional services\". APIs should not feel compelled to indicate resources that have been deleted with this status code."
    )

    /** 411 - Server rejected the request because the Content-Length header field is not defined and the server requires it. */
    data object LengthRequired : Error.ClientError(
        411,
        "Length Required",
        "Server rejected the request because the Content-Length header field is not defined and the server requires it."
    )

    /** 412 - The client has indicated preconditions in its headers which the server does not meet. */
    data object PreconditionFailed : Error.ClientError(
        412,
        "Precondition Failed",
        "The client has indicated preconditions in its headers which the server does not meet."
    )

    /** 413 - The request is larger than the server is willing or able to process. Previously called "Request Entity Too Large". */
    data object RequestEntityTooLarge : Error.ClientError(
        413,
        "Request Entity Too Large",
        "Request entity is larger than limits defined by server. The server might close the connection or return an Retry-After header field."
    )

    /** 414 - The URI requested by the client is longer than the server is willing to interpret. */
    data object RequestUriTooLong : Error.ClientError(
        414,
        "Request URI Too Long",
        "The URI requested by the client is longer than the server is willing to interpret."
    )

    /** 415 - The media format of the requested data is not supported by the server, so the server is rejecting the request. */
    data object UnsupportedMediaType : Error.ClientError(
        415,
        "Unsupported Media Type",
        "The media format of the requested data is not supported by the server, so the server is rejecting the request."
    )

    /** 416 - The range specified by the Range header field in the request cannot be fulfilled. It's possible that the range is outside the size of the target URI's data. */
    data object RequestedRangeNotSatisfiable : Error.ClientError(
        416,
        "Requested Range Not Satisfiable",
        "The range specified by the Range header field in the request cannot be fulfilled. It's possible that the range is outside the size of the target URI's data."
    )

    /** 417 - This response code means the expectation indicated by the Expect request header field cannot be met by the server. */
    data object ExpectationFailed : Error.ClientError(
        417,
        "Expectation Failed",
        "This response code means the expectation indicated by the Expect request header field cannot be met by the server."
    )

    /** 418 - The server refuses the attempt to brew coffee with a teapot. */
    data object ImATeapot : Error.ClientError(
        418,
        "I'm a teapot",
        "The server refuses the attempt to brew coffee with a teapot."
    )

    /** 421 - The request was directed at a server that is not able to produce a response. This can be sent by a server that is not configured to produce responses for the combination of scheme and authority that are included in the request URI. */
    data object MisdirectedRequest : Error.ClientError(
        421,
        "Misdirected Request",
        "The request was directed at a server that is not able to produce a response. This can be sent by a server that is not configured to produce responses for the combination of scheme and authority that are included in the request URI."
    )

    /** 422 - The request was well-formed but was unable to be followed due to semantic errors. */
    data object UnprocessableEntity : Error.ClientError(
        422,
        "Unprocessable Entity",
        "The request was well-formed but was unable to be followed due to semantic errors."
    )

    /** 423 - The resource that is being accessed is locked. */
    data object Locked : Error.ClientError(
        423,
        "Locked",
        "The resource that is being accessed is locked."
    )

    /** 424 - The request failed due to failure of a previous request. */
    data object FailedDependency : Error.ClientError(
        424,
        "Failed Dependency",
        "The request failed due to failure of a previous request."
    )

    /** 425 - Indicates that the server is unwilling to risk processing a request that might be replayed. */
    data object TooEarly : Error.ClientError(
        425,
        "Too Early",
        "Indicates that the server is unwilling to risk processing a request that might be replayed."
    )

    /** 426 - The server refuses to perform the request using the current protocol but might be willing to do so after the client upgrades to a different protocol. The server sends an Upgrade header in a 426 response to indicate the required protocol(s). */
    data object UpgradeRequired : Error.ClientError(
        426,
        "Upgrade Required",
        "The server refuses to perform the request using the current protocol but might be willing to do so after the client upgrades to a different protocol. The server sends an Upgrade header in a 426 response to indicate the required protocol(s). "
    )

    /** 428 - The origin server requires the request to be conditional. This response is intended to prevent the 'lost update' problem, where a client GETs a resource's state, modifies it and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict. */
    data object PreconditionRequired : Error.ClientError(
        428,
        "Precondition Required",
        "The origin server requires the request to be conditional. This response is intended to prevent the 'lost update' problem, where a client GETs a resource's state, modifies it and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict."
    )

    /** 429 - The user has sent too many requests in a given amount of time ("rate limiting"). */
    data object TooManyRequests : Error.ClientError(
        429,
        "Too Many Requests",
        "The user has sent too many requests in a given amount of time (\"rate limiting\")."
    )

    /** 431 - The server is unwilling to process the request because its header fields are too large. The request may be resubmitted after reducing the size of the request header fields.  */
    data object RequestHeaderFieldsTooLarge : Error.ClientError(
        431,
        "Request Header Fields Too Large",
        "The server is unwilling to process the request because its header fields are too large. The request may be resubmitted after reducing the size of the request header fields. "
    )

    /** 451 - The user-agent requested a resource that cannot legally be provided, such as a web page censored by a government. */
    data object UnavailableForLegalReasons : Error.ClientError(
        451,
        "Unavailable For Legal Reasons",
        "The user agent requested a resource that cannot legally be provided, such as a web page censored by a government."
    )

    /** 500 - The server has encountered a situation it does not know how to handle. */
    data object InternalServerError : Error.ServerError(
        500,
        "Internal Server Error",
        "The server has encountered a situation it does not know how to handle."
    )

    /** 501 - The request method is not supported by the server and cannot be handled. The only methods that servers are required to support (and therefore that must not return this code) are GET and HEAD. */
    data object NotImplemented : Error.ServerError(
        501,
        "Not Implemented",
        "The request method is not supported by the server and cannot be handled. The only methods that servers are required to support (and therefore that must not return this code) are GET and HEAD."
    )

    /** 502 - This error response means that the server, while working as a gateway to get a response needed to handle the request, got an invalid response. */
    data object BadGateway : Error.ServerError(
        502,
        "Bad Gateway",
        "This error response means that the server, while working as a gateway to get a response needed to handle the request, got an invalid response."
    )

    /** 503 - The server is not ready to handle the request. Common causes are a server that is down for maintenance or that is overloaded. Note that together with this response, a user-friendly page explaining the problem should be sent. This response should be used for temporary conditions and the Retry-After HTTP header should, if possible, contain the estimated time before the recovery of the service. The webmaster must also take care about the caching-related headers that are sent along with this response, as these temporary condition responses should usually not be cached. */
    data object ServiceUnavailable : Error.ServerError(
        503,
        "Service Unavailable",
        "The server is not ready to handle the request. Common causes are a server that is down for maintenance or that is overloaded. Note that together with this response, a user-friendly page explaining the problem should be sent. This response should be used for temporary conditions and the Retry-After HTTP header should, if possible, contain the estimated time before the recovery of the service. The webmaster must also take care about the caching-related headers that are sent along with this response, as these temporary condition responses should usually not be cached."
    )

    /** 504 - This error response is given when the server is acting as a gateway and cannot get a response in time. */
    data object GatewayTimeout : Error.ServerError(
        504,
        "Gateway Timeout",
        "This error response is given when the server is acting as a gateway and cannot get a response in time."
    )

    /** 505 - The HTTP version used in the request is not supported by the server. */
    data object HttpVersionNotSupported : Error.ServerError(
        505,
        "HTTP Version Not Supported",
        "The HTTP version used in the request is not supported by the server."
    )

    /** 506 - The server has an internal configuration error: the chosen variant resource is configured to engage in transparent content negotiation itself, and is therefore not a proper end point in the negotiation process. */
    data object VariantAlsoNegotiates : Error.ServerError(
        506,
        "Variant Also Negotiates",
        "The server has an internal configuration error: the chosen variant resource is configured to engage in transparent content negotiation itself, and is therefore not a proper end point in the negotiation process."
    )

    /** 507 - The method could not be performed on the resource because the server is unable to store the representation needed to successfully complete the request. */
    data object InsufficientStorage : Error.ServerError(
        507,
        "Insufficient Storage",
        "The method could not be performed on the resource because the server is unable to store the representation needed to successfully complete the request."
    )

    /** 508 - The server detected an infinite loop while processing the request. */
    data object LoopDetected : Error.ServerError(
        508,
        "Loop Detected",
        "The server detected an infinite loop while processing the request."
    )

    /** 510 - Further extensions to the request are required for the server to fulfill it. */
    data object NotExtended : Error.ServerError(
        510,
        "Not Extended",
        "Further extensions to the request are required for the server to fulfill it."
    )

    /** 511 - Indicates that the client needs to authenticate to gain network access. */
    data object NetworkAuthenticationRequired : Error.ServerError(
        511,
        "Network Authentication Required",
        "Indicates that the client needs to authenticate to gain network access."
    )
}

