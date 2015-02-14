package org.zirbes.cloudinfo

import retrofit.http.*

interface ServiceInfoApi {

    @Headers(['Accept: application/json'])
    @GET('/version')
    VersionInfo version()

    @Headers(['Accept: application/json'])
    @GET('/health')
    Map health()

}
