package org.zirbes.cloudinfo

import groovy.util.logging.Slf4j
import retrofit.RestAdapter

@Slf4j
class RetrofitLogger implements RestAdapter.Log {

    @Override
    void log(String message) {
        log.debug(message)
    }
}

