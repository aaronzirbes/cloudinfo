package org.zirbes.cloudinfo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import retrofit.RetrofitError

import static org.fusesource.jansi.Ansi.*
import static org.fusesource.jansi.Ansi.Color.*


@CompileStatic
@Slf4j
class CloudService {

    static final Map<String, Version> services = [
        'customer'         : Version.BOOT,
        'dealer'           : Version.BOOT,
        'device'           : Version.BOOT,
        'faultcode'        : Version.BOOT,
        'manufacturer'     : Version.BOOT,
        'userpreference'   : Version.BOOT,
        'user'             : Version.NONE,
        'vehicle'          : Version.BOOT,
        'entity-gateway'   : Version.BOOT,
        'search'           : Version.NONE,
        'portal'           : Version.GRUNT,
        'security-gateway' : Version.NONE
    ]


    static final String ENVIRONMENT = 'qa'
    static final String DOMAIN = 'connectedfleet.io'
    static final String PORT = '8080'

    Map getBootHealth(ServiceInfoApi serviceInfoApi) {
        try {
            return serviceInfoApi.health()
        } catch (RetrofitError re) {
            log.error "Unable to reach service"
        }
        return null
    }

    void checkServices() {
        services.each { String service, Version versionType ->
            String uri = "http://${service}-${ENVIRONMENT}.${DOMAIN}"
            String servicePad = service.padRight(32)
            if (versionType != Version.GRUNT) { uri = "${uri}:${PORT}" }

            ServiceInfoApi serviceInfoApi = new RetrofitClientBuilder().withEndpoint(uri).build(ServiceInfoApi)

            Map health = [ (service): 'DOWN' ]
            GruntInfo info
            if (versionType == Version.GRUNT) {
                try {
                    info = serviceInfoApi.gruntVersion()
                    health[service] = 'UP'
                } catch (RetrofitError re) {
                    alignVersion(8, 24, 'ERROR', 'VERSION ENDPOINT NOT FOUND')
                }
                uri = "${uri}:${PORT}"
            } else {
                health = getBootHealth(serviceInfoApi)
            }
            alignService(0, 32, service, health.status)

            if (versionType == Version.BOOT) {
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
            } else if (info) {
                alignVersion(8, 24, 'gitBranch',info.build.gitBranch)
                alignVersion(8, 24, 'gitRevision',info.build.gitRevision)
                alignVersion(8, 24, 'number',info.build.number)
                alignVersion(8, 24, 'tag',info.build.tag)
                alignVersion(8, 24, 'url',info.build.url)
            }
        }
    }

    void checkPortal() {
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

    static enum Version {
        NONE,
        BOOT,
        GRUNT
    }

}
