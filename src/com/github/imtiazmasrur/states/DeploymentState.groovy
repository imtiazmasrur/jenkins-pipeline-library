package com.github.imtiazmasrur.states

class DeploymentState implements Serializable {
    
    boolean rollbackStatus = false
    boolean deploymentStatus = false
    String statusMessage = ""
    String currentTag = ""
    String latestTag = ""

    Map toMap() {
        return [
            "ROLLBACK_STATUS"  : rollbackStatus,
            "DEPLOYMENT_STATUS": deploymentStatus,
            "STATUS_MESSAGE"   : statusMessage,
            "CURRENT_TAG"      : currentTag,
            "LATEST_TAG"       : latestTag
        ]
    }

    def deploySuccessMessage() {
        return "🚀 Deployed successfully. 😎 ${latestTag}"
    }

    def deployFailureMessage() {
        return "⚠️ Failed to deploy. Preparing for Rollback."
    }

    def healthCheckSuccessMessage() {
        return "🟢 Project is Live. ${latestTag}"
    }

    def healthCheckFailureMessage() {
        return "🔴 Failed to bring project online. Preparing for Rollback"
    }

    def rollbackSuccessMessage() {
        return "🚀 Rollback completed successfully. 😎 ${currentTag}"
    }
}
