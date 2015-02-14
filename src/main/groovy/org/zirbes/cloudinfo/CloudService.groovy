package org.zirbes.cloudinfo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import retrofit.RetrofitError

import static org.fusesource.jansi.Ansi.*
import static org.fusesource.jansi.Ansi.Color.*


@CompileStatic
@Slf4j
class CloudService {

    static final Map<String, Boolean> services = [
        'customer'         : true,
        'dealer'           : true,
        'device'           : true,
        'faultcode'        : true,
        'manufacturer'     : true,
        'userpreference'   : true,
        'user'             : false,
        'vehicle'          : true,
        'entity-gateway'   : true,
        'search'           : false,
        'security-gateway' : false
    ]


    static final String ENVIRONMENT = 'qa'
    static final String DOMAIN = 'connectedfleet.io'
    static final String PORT = '8080'

    void checkServices() {
        services.each { String service, Boolean hasVersion ->
            String uri = "http://${service}-${ENVIRONMENT}.${DOMAIN}:${PORT}"
            ServiceInfoApi serviceInfoApi = new RetrofitClientBuilder().withEndpoint(uri).build(ServiceInfoApi)
            Map health = [ (service): 'DOWN' ]
            try {
                health = serviceInfoApi.health()
            } catch (RetrofitError re) {
                log.error "Unable to reach service"
            }

            String servicePad = service.padRight(32)
                alignService(0, 32, service, health.status)
            if (hasVersion) {
                try {
                    VersionInfo version = serviceInfoApi.version()
                    alignVersion(8, 24, 'buildId', version.buildId)
                    alignVersion(8, 24, 'buildNumber', version.buildNumber)
                    alignVersion(8, 24, 'buildTag', version.buildTag)
                    alignVersion(8, 24, 'gitBranch', version.gitBranch)
                    alignVersion(8, 24, 'gitCommit', version.gitCommit)
                    alignVersion(8, 24, 'nodeName', version.nodeName)
                } catch (RetrofitError re) {
                    alignVersion(8, 24, 'ERROR', 'VERSION ENDPOINT NOT FOUND')
                }
            }
        }
    }


    String alignService(Integer indent, Integer allowance, String label, Object value) {

        String val = ''
        if (value == "UP") {
            val = ansi().fg(Color.GREEN).a(value).reset()
        } else {
            val = ansi().fg(Color.RED).a(value).reset()
        }

        String logMessage = ''.padRight(indent) + getColor(label.padRight(allowance)) + ": ${val}"
        log.info logMessage
    }

    String alignVersion(Integer indent, Integer allowance, String label, Object value) {

        String val = getColor("${value}")

        String logMessage = ''.padRight(indent) + getColor(label.padRight(allowance)) + ": ${val}"
        log.info logMessage
    }

    String getColor(String str) {

        Integer colorCount = (Color.values().size() - 3)
        Integer colorNum = Math.abs(str.hashCode()) % colorCount + 1
        Color color = Color.values()[colorNum]
        return ansi().fg(color).a(str).reset()

    }

}
