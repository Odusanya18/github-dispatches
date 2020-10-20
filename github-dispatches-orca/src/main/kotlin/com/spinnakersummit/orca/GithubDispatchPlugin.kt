package com.spinnakersummit.orca

import com.netflix.spinnaker.orca.api.pipeline.graph.StageDefinitionBuilder
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import com.spinnakersummit.orca.tasks.CallGithubDispatchTask
import org.pf4j.Extension
import org.pf4j.Plugin
import org.pf4j.PluginWrapper

class GithubDispatchPlugin(wrapper: PluginWrapper): Plugin(wrapper)

@Extension
class GithubDispatchStage : StageDefinitionBuilder {
    override fun taskGraph(stage: StageExecution, builder: TaskNode.Builder) {
        builder
                .withTask("callDispatchTask", CallGithubDispatchTask::class.java)
    }
}