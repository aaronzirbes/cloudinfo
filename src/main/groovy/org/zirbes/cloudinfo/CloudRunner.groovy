package org.zirbes.cloudinfo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class CloudRunner {

    static void main(String[] argv) {
        log.info 'Initializing cloudinfo'

        CloudService cloudService = new CloudService()
        cloudService.checkServices()

        log.info 'Exiting cloudinfo'
    }
}
