package com.spinnakersummit.orca.models

import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration

@PluginConfiguration("spinnakersummit.git-dispatch")
data class DispatchConfig(
    val repo: String,
    val org: String,
    val token: String
)