package org.zirbes.cloudinfo

import groovy.transform.CompileStatic

@CompileStatic
class VersionInfo {

    String buildId
    String buildNumber
    String buildTag
    String gitBranch
    String gitCommit
    String nodeName

}
