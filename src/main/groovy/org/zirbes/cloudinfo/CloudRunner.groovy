package org.zirbes.cloudinfo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class CloudRunner {

    static void main(String[] argv) {
        log.info 'Initializing cloudinfo'

        String environment = 'dev'

        if ( argv.size() == 1 ) {
            environment = argv[0]
        }

        log.info "Checking '${environment}' environment."
        CloudService cloudService = new CloudService()
        cloudService.checkServices(environment)

        log.info 'Exiting cloudinfo'
    }
}
