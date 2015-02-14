package org.zirbes.cloudinfo

import groovy.transform.CompileStatic

@CompileStatic
class GruntInfo {

    String app
    Build build

    static class Build {
        String gitBranch
        String gitRevision
        String number
        String tag
        String url
    }
}
