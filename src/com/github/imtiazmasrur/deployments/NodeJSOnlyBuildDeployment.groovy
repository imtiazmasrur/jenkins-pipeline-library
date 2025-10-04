package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.states.DeploymentState
import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper
import com.github.imtiazmasrur.helpers.DeploymentHelper

/**
 * Class to handle Node.js project deployment, health check, and rollback using Git tags.
 * usage:
 * def nodeJS = new NodeJSOnlyBuildDeployment(this, config)
 *
 * Parameters: the config map contains:
 * - nodeJSVersion (required): The version of Node.js to use.
 * - nodeJSPath (required): The base path where Node.js versions are installed.
 * - projectName (required): The name of the project (used for PM2 process management).
 * - projectDirectory (required): The directory of the project to deploy.
 * - buildCommand (optional): Build command for your project. Default command is "npm run build".
 */
class NodeJSOnlyBuildDeployment implements Serializable {

    Object script
    Map config
    DeploymentState state
    NodeJSHelper nodeJSHelper
    DeploymentHelper deploymentHelper

    NodeJSOnlyBuildDeployment(script, Map config) {
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
            // Execute only build process
            nodeJSHelper.onlyBuild()

            state.statusMessage = state.deploySuccessMessage()
            script.echo "${state.statusMessage}"
        } catch (Exception e) {
            // If deployment fails, set rollback status to true
            state.rollbackStatus = true

            state.statusMessage = state.deployFailureMessage()
            script.echo "${state.statusMessage} ${e}"
        }
    }

    def rollback() {
        GitHelper gitHelper = new GitHelper(script)
        // Checkout to the current tag
        gitHelper.gitCheckout(state.currentTag)

        // Execute only build process
        nodeJSHelper.onlyBuild()

        state.statusMessage = state.rollbackSuccessMessage()
        script.echo "${state.statusMessage}"
    }

    def getStatus() {
        return state.toMap()
    }
}
