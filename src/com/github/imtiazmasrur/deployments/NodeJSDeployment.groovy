package com.github.imtiazmasrur.deployments

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
 * - isBuildRequired (optional): Boolean to indicate if build step is required (default is false).
 *
 */
class NodeJSDeployment implements Serializable {

    def static ROLLBACK_STATUS = false
    def static DEPLOYMENT_STATUS = false
    def static STATUS_MESSAGE = ""
    def static CURRENT_TAG = ""
    def static LATEST_TAG = ""

    def script
    def gitHelper
    def nodeJSHelper
    def config

    NodeJSDeployment(script, Map config) {
        if (!config.nodeJSVersion || !config.nodeJSPath || !config.projectName || !config.projectDirectory) {
            throw new Exception("nodeJSVersion, nodeJSPath, projectName, and projectDirectory are required.")
        }

        this.script = script
        this.gitHelper = new GitHelper(script)
        this.nodeJSHelper = new NodeJSHelper(script, config)
        this.config = config
    }

    def checkoutCode() {
        try {
            // If not git repository then throw error
            if (!gitHelper.isGitRepo()) {
                STATUS_MESSAGE = "❌ Not a git repo. Please check your project directory and logs: ${config.projectDirectory}."
                script.echo "${STATUS_MESSAGE}"
                throw new Exception(STATUS_MESSAGE)
            }

            // Check git status and fetch the latest changes
            gitHelper.gitStatus()
            gitHelper.gitFetch()
            
            def currentTag = gitHelper.getCurrentTag()
            CURRENT_TAG = currentTag

            // if current tag is not found or null then throw error
            if (!currentTag || currentTag == "") {
                STATUS_MESSAGE = "⚠️ No tag found. Please check your project directory and logs: ${config.projectDirectory}."
                script.echo "${STATUS_MESSAGE}"
                throw new Exception(e)
            }

            def latestTag = gitHelper.getLatestTag()
            LATEST_TAG = latestTag

            if (currentTag != latestTag) {
                // Checkout to the latest tag
                gitHelper.gitCheckout(LATEST_TAG)

                // Set deployment status
                DEPLOYMENT_STATUS = true

                STATUS_MESSAGE = "🔥 Checked out to the latest tag: ${LATEST_TAG}"
                script.echo "${STATUS_MESSAGE}"
            } else {
                STATUS_MESSAGE = "✅ Project is already on the latest tag: ${currentTag}"
                script.echo "${STATUS_MESSAGE}"
            }
        } catch (Exception e) {
            STATUS_MESSAGE = "⛔ Failed to run the project, please check your project directory and logs: ${config.projectDirectory}."
            script.echo "${STATUS_MESSAGE}"
            throw new Exception(e)
        }
    }

    def deploy() {
        try {
            // Execute deployment process
            nodeJSHelper.executeDeployment()

            STATUS_MESSAGE = "🚀 Project deployed successfully. 😎 ${LATEST_TAG}"
            script.echo "${STATUS_MESSAGE}"
        } catch (Exception e) {
            // If deployment fails, set rollback status to true
            ROLLBACK_STATUS = true

            STATUS_MESSAGE = "↩️ Failed to deploy. Preparing for Rollback."
            script.echo "${STATUS_MESSAGE} ${e}"
        }
    }

    def healthCheck() {
        // Wait for the project to start
        sleep(15)
        def projectStatus = nodeJSHelper.healthStatus()

        // Check if the project is live
        if (projectStatus) {
            STATUS_MESSAGE = "🟢 Project is Live. 😎 ${LATEST_TAG}"
            script.echo "${STATUS_MESSAGE}"

            nodeJSHelper.pm2SaveAndLogs()
        } else {
            // If project is not live, set rollback status to true
            ROLLBACK_STATUS = true

            STATUS_MESSAGE = "🔴 Failed to bring project online. Preparing for Rollback"
            script.echo "${STATUS_MESSAGE}"
        }
    }

    def rollback() {
        // Checkout to the current tag
        gitHelper.gitCheckout(CURRENT_TAG)

        // Execute deployment process
        nodeJSHelper.executeDeployment()

        // Wait for the project to start
        sleep(15)
        nodeJSHelper.pm2SaveAndLogs()

        STATUS_MESSAGE = "🚀 Rollback completed successfully. 😎 ${CURRENT_TAG}"
        script.echo "${STATUS_MESSAGE}"
    }

    def getStatus() {
        return [
                "ROLLBACK_STATUS"  : ROLLBACK_STATUS,
                "DEPLOYMENT_STATUS": DEPLOYMENT_STATUS,
                "STATUS_MESSAGE"   : STATUS_MESSAGE,
                "CURRENT_TAG"      : CURRENT_TAG,
                "LATEST_TAG"       : LATEST_TAG
        ]
    }

}