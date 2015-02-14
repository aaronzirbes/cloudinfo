package org.zirbes.cloudinfo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import retrofit.ErrorHandler
import retrofit.RetrofitError
import retrofit.client.Header
import retrofit.client.Response

@CompileStatic
@Slf4j
class RetrofitClientErrorHandler implements ErrorHandler {

    @Override
    Throwable handleError(RetrofitError re) {
        Response response = re.response
        if (response) {
            logRetrofitError(response)
        }

        return re
    }

    protected void logRetrofitError(Response response) {
        log.error "RetrofitError[status]=${response.status}"
        log.error "RetrofitError[reason]=${response.reason}"
        if (response) {
            response.headers.each{ Header header ->
                log.error "RetrofitError[header]=${header}"
            }
            StringWriter sw = new StringWriter()
            response.body?.in()?.eachLine{ String line ->
                sw << "${line}\n"
            }
            log.error "RetrofitError[body]=${sw}"
        }
    }

}

