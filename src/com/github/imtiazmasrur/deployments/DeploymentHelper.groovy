package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.states.DeploymentState

/**
 * Helper class to manage deployment operations such as checking out code from Git.
 * Parameters: the config map contains:
 * - projectDirectory (required): The directory of the project to deploy.
 */
class DeploymentHelper implements Serializable {

    Object script
    GitHelper gitHelper
    Map config

    DeploymentHelper(script, Map config) {
        if (!config.projectDirectory) {
            throw new Exception("projectDirectory is required.")
        }
        this.script = script
        this.gitHelper = new GitHelper(script)
        this.config = config
    }

    def checkoutCode(DeploymentState state) {
        try {
            // If not git repository then throw error
            if (!gitHelper.isGitRepo()) {
                state.statusMessage = "❌ Not a git repo. Please check your project directory and logs: ${config.projectDirectory}."
                script.echo "${state.statusMessage}"
                throw new Exception(state.statusMessage)
            }

            // Check git status and fetch the latest changes
            gitHelper.gitStatus()
            gitHelper.gitFetch()
            
            def currentTag = gitHelper.getCurrentTag()
            state.currentTag = currentTag

            // if current tag is not found or null then throw error
            if (!currentTag || currentTag == "") {
                state.statusMessage = "❗ No tag found. Please check your project directory and logs: ${config.projectDirectory}."
                script.echo "${state.statusMessage}"
                throw new Exception(state.statusMessage)
            }

            def latestTag = gitHelper.getLatestTag()
            state.latestTag = latestTag

            if (currentTag != latestTag) {
                // Checkout to the latest tag
                gitHelper.gitCheckout(state.latestTag)

                // Set deployment status
                state.deploymentStatus = true

                state.statusMessage = "🔥 Checked out to the latest tag: ${state.latestTag}"
                script.echo "${state.statusMessage}"
            } else {
                state.statusMessage = "✅ Project is already on the latest tag: ${currentTag}"
                script.echo "${state.statusMessage}"
            }
        } catch (Exception e) {
            state.statusMessage = "⛔ Failed to run the project, please check your project directory and logs: ${config.projectDirectory}."
            script.echo "${state.statusMessage}"
            throw new Exception(e)
        }
    }
}
