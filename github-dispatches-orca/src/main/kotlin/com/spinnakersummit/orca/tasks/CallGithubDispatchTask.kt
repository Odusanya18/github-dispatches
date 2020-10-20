package com.spinnakersummit.orca.tasks

import com.netflix.spinnaker.kork.plugins.api.PluginSdks
import com.netflix.spinnaker.kork.plugins.api.httpclient.HttpClient
import com.netflix.spinnaker.kork.plugins.api.httpclient.HttpClientConfig
import com.netflix.spinnaker.kork.plugins.api.httpclient.Request
import com.netflix.spinnaker.kork.plugins.api.httpclient.Response
import com.netflix.spinnaker.orca.api.pipeline.Task
import com.netflix.spinnaker.orca.api.pipeline.TaskResult
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import com.spinnakersummit.orca.models.DispatchConfig
import com.spinnakersummit.orca.models.DispatchEvent
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

class CallGithubDispatchTask(pluginSdk: PluginSdks,private val dispatchConfig: DispatchConfig) : Task {
    private val client: HttpClient
    init {
        pluginSdk
            .http()
            .configure(
                "dispatch-client",
                "https://api.github.com/repos/${dispatchConfig.org}/${dispatchConfig.repo}",
                HttpClientConfig())
        client = pluginSdk
            .http()
            .get("dispatch-client")
    }

    override fun execute(stage: StageExecution): TaskResult {
        val yayEvent = DispatchEvent("testEvent", "Yaaay! It works!")
        val dispatchResponse = dispatch(yayEvent)
        val body = readBody(dispatchResponse.body)

        if (dispatchResponse.isError)return contextDispatch(ExecutionStatus.TERMINAL, body)
        return contextDispatch(ExecutionStatus.SUCCEEDED, body)
    }

    private fun contextDispatch(executionStatus: ExecutionStatus, dispatchRes: String) =
        TaskResult
            .builder(executionStatus)
            .context(mapOf("dispatch" to dispatchRes))
            .build()

    private fun dispatch(dispatchEvent: DispatchEvent) : Response {
        val request = Request("call-dispatch", "dispatches")
        request.body = dispatchEvent
        request.headers["Authorization"] = dispatchConfig.token

        return client.post(request)
    }

    private fun readBody(boi: InputStream) : String {
        val reader = InputStreamReader(boi)
        val bodyContent = reader.readText()

        reader.close()
        boi.close()

        return bodyContent
    }
}