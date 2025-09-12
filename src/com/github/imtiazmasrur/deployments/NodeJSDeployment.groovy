package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

class NodeJSDeployment implements Serializable {

    def static ROLLBACK_STATUS = false
    def static DEPLOYMENT_STATUS = false
    def static STATUS_MESSAGE = ""
    def static LATEST_TAG = ""
    def static BEFORE_LAST_TAG = ""

    def script
    def gitHelper
    def nodeJSHelper

    NodeJSDeployment(script, Map config) {
        if (!config.nodeJSVersion || !config.nodeJSPath || !config.projectName || !config.projectDirectory) {
            throw new Exception("nodeJSVersion, nodeJSPath, projectName, and projectDirectory are required.")
        }

        this.script = script
        this.gitHelper = new GitHelper(script)
        this.nodeJSHelper = new NodeJSHelper(script, [nodeJSVersion: config.nodeJSVersion, nodeJSPath: config.nodeJSPath, projectName: config.projectName])
    }

    def checkoutCode() {
        dir("${config.projectDirectory}") {
            try {
                // Check git status
                gitHelper.gitStatus()
                
                def currentTag = gitHelper.getCurrentTag()

                // if current tag is not found or null then throw error
                if (!currentTag || currentTag == "") {
                    STATUS_MESSAGE = "⛔ Current tag not found, please check your project directory and logs: ${projectDirectory}."
                    script.echo "${STATUS_MESSAGE}"
                    throw new Exception(STATUS_MESSAGE)
                }

                script.echo "⚡️ Current Tag: ${currentTag}"

                def latestTag = gitHelper.getLatestTag()
                def beforeLastTag = gitHelper.getBeforeLastTag()
                
                // Set environment variables
                LATEST_TAG = latestTag
                BEFORE_LAST_TAG = beforeLastTag

                if (currentTag != latestTag) {
                    // Fetch the latest changes
                    gitHelper.gitFetch()
                    // Checkout to the latest tag
                    gitHelper.gitCheckout(LATEST_TAG)

                    // Set deployment status
                    DEPLOYMENT_STATUS = true
                    // Start deployment
                    // deploy()

                    STATUS_MESSAGE = "🔥 Checked out to the latest tag: ${LATEST_TAG}"
                    script.echo "${STATUS_MESSAGE}"
                } else {
                    STATUS_MESSAGE = "✅ Project is already on the latest tag: ${currentTag}"
                    script.echo "${STATUS_MESSAGE}"
                }
            } catch (Exception e) {
                STATUS_MESSAGE = "⛔ Failed to run the project, please check your project directory and logs: ${config.projectDirectory}."
                script.echo "${STATUS_MESSAGE}"
                throw new Exception(STATUS_MESSAGE)
            }
        }
    }

    def build() {
    }

    def deploy() {
        // if (DEPLOYMENT_STATUS) {
            try {
                def node = nodeJSHelper.getNodeJSPath()
                script.sh "npm i"
                script.sh "${node}/pm2 reload ${config.projectName}"

                STATUS_MESSAGE = "🚀 Project deployed successfully. 😎 ${LATEST_TAG}"
                script.echo "${STATUS_MESSAGE}"
            } catch (Exception e) {
                // If deployment fails, set rollback status to true
                ROLLBACK_STATUS = true

                // Start rollback
                // rollback()

                STATUS_MESSAGE = "↩️ Failed to deploy. Preparing for Rollback."
                script.echo "${STATUS_MESSAGE}"
            }
        // }
    }

    def healthCheck() {
        // if (DEPLOYMENT_STATUS && !ROLLBACK_STATUS) {
            // Wait for the project to start
            sleep(15)
            def projectStatus = nodeJSHelper.healthStatus(PROJECT_NAME)

            // Check if the project is live
            if (projectStatus) {
                STATUS_MESSAGE = "🟢 Project is Live. 😎 ${LATEST_TAG}"
                script.echo "${STATUS_MESSAGE}"
            } else {
                // If project is not live, set rollback status to true
                ROLLBACK_STATUS = true

                STATUS_MESSAGE = "🔴 Failed to bring project online. Preparing for Rollback"
                script.echo "${STATUS_MESSAGE}"
            }
        // }
    }

    def rollback() {
        // if (ROLLBACK_STATUS) {
            def nodeJSPath = getNodeJSPath()

            script.sh "git checkout tags/${BEFORE_LAST_TAG}"
            script.sh "npm i"
            script.sh "${nodeJSPath}/pm2 reload ${PROJECT_NAME}"

            STATUS_MESSAGE = "🚀 Rollback completed successfully. 😎 ${BEFORE_LAST_TAG}"
            script.echo "${STATUS_MESSAGE}"
        // }
    }

    def getStatus() {
        // checkoutCode()
        // deploy()
        // healthCheck()
        // rollback()

        return [
                "ROLLBACK_STATUS"  : ROLLBACK_STATUS,
                "DEPLOYMENT_STATUS": DEPLOYMENT_STATUS,
                "STATUS_MESSAGE"   : STATUS_MESSAGE,
                "LATEST_TAG"       : LATEST_TAG,
                "BEFORE_LAST_TAG"  : BEFORE_LAST_TAG
        ]
    }

}