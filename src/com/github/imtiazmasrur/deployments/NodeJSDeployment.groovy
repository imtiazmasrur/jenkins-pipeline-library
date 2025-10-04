package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.states.DeploymentState
import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

/**
 * Class to handle Node.js project deployment, health check, and rollback using Git tags.
 * usage:
 * def nodeJS = new NodeJSDeployment(this, config)
 * 
 * Parameters: the config map contains:
 * - nodeJSVersion (required): The version of Node.js to use.
 * - nodeJSPath (required): The base path where Node.js versions are installed.
 * - projectName (required): The name of the project (used for PM2 process management).
 * - projectDirectory (required): The directory of the project to deploy.
 * - additinalBuildCommands (optional): Additional commands to run after the build command.
 * - isBuildRequired (optional): Boolean to indicate if build step is required (default is false). This will run 'npm run build' if true.
 */
class NodeJSDeployment implements Serializable {

    Object script
    Map config
    DeploymentState state
    NodeJSHelper nodeJSHelper
    DeploymentHelper deploymentHelper

    NodeJSDeployment(script, Map config) {
        if (!config.nodeJSVersion || !config.nodeJSPath || !config.projectName || !config.projectDirectory) {
            throw new Exception("nodeJSVersion, nodeJSPath, projectName, and projectDirectory are required.")
        }

        this.script = script
        this.config = config
        this.state = new DeploymentState()
        this.nodeJSHelper = new NodeJSHelper(script, config)
        this.deploymentHelper = new DeploymentHelper(script, config)
    }

    def checkoutCode() {
        deploymentHelper.checkoutCode(state)
    }

    def deploy() {
        try {
            // Execute deployment process
            nodeJSHelper.executeDeployment()

            state.statusMessage = state.deploySuccessMessage()
            script.echo "${state.statusMessage}"
        } catch (Exception e) {
            // If deployment fails, set rollback status to true
            state.rollbackStatus = true

            state.statusMessage = state.deployFailureMessage()
            script.echo "${state.statusMessage} ${e}"
        }
    }

    def healthCheck() {
        // Wait for the project to start
        sleep(15)
        def projectStatus = nodeJSHelper.healthStatus()

        // Check if the project is live
        if (projectStatus) {
            state.statusMessage = state.healthCheckSuccessMessage()
            script.echo "${state.statusMessage}"

            nodeJSHelper.pm2SaveAndLogs()
        } else {
            // If project is not live, set rollback status to true
            state.rollbackStatus = true

            state.statusMessage = state.healthCheckFailureMessage()
            script.echo "${state.statusMessage}"
        }
    }

    def rollback() {
        GitHelper gitHelper = new GitHelper(script)
        // Checkout to the current tag
        gitHelper.gitCheckout(state.currentTag)

        // Execute deployment process
        nodeJSHelper.executeDeployment()

        // Wait for the project to start
        sleep(15)
        nodeJSHelper.pm2SaveAndLogs()

        state.statusMessage = state.rollbackSuccessMessage()
        script.echo "${state.statusMessage}"
    }

    def getStatus() {
        return state.toMap()
    }
}
