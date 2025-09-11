package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

class pm2Deployment implements Serializable {

    def static ROLLBACK_STATUS = false
    def static DEPLOYMENT_STATUS = false
    def static STATUS_MESSAGE = ""
    def static LATEST_TAG = ""
    def static BEFORE_LAST_TAG = ""

    def script
    def gitHelper
    def nodeJSHelper

    pm2Deployment(script, nodeJSVersion, nodeJSPath, projectName, projectDirectory) {
        this.script = script
        this.gitHelper = new GitHelper(script)
        this.nodeJSHelper = new NodeJSHelper(script, nodeJSVersion, nodeJSPath, projectName)
    }

    def checkoutCode() {
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

            def latestTag = gitHelper.getLatestTag()
            def beforeLastTag = gitHelper.getBeforeLastTag()

            script.echo "⚡️ Current Tag: ${currentTag}"

            if (currentTag != latestTag) {
                // Fetch the latest changes
                gitHelper.gitFetch()

                // Set environment variables
                LATEST_TAG = latestTag
                BEFORE_LAST_TAG = beforeLastTag

                script.echo "🔥 Latest Tag: ${LATEST_TAG}"
                script.echo "🍀 Before Last Tag: ${BEFORE_LAST_TAG}"
                
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
            STATUS_MESSAGE = "⛔ Failed to run the project, please check your project directory and logs: ${projectDirectory}."
            script.echo "${STATUS_MESSAGE}"
            throw new Exception(STATUS_MESSAGE)
        }
    }

    def installDependencies() {
        // Checkout the latest code
        def checkoutCode = checkoutCode()
        if (!checkoutCode) {
            STATUS_MESSAGE = "⛔ Failed to checkout the latest code, please check your project directory and logs: ${projectDirectory}."
            script.echo "${STATUS_MESSAGE}"
            throw new Exception(STATUS_MESSAGE)
        }
    }

    def build() {
    }

    def deploy() {
        script.echo "Deploying the project..."
        return this
    }

    def restart() {
        script.echo "Restarting the project..."
        return this
    }

    def healthCheck() {
        script.echo "Checking the health of the project..."
        return this
    }

    def rollback() {
        script.echo "Rolling back the project..."
    }

    def getStatus() {
        checkoutCode()

        deploy().restart().healthCheck().rollback()
        return [
                "ROLLBACK_STATUS"  : ROLLBACK_STATUS,
                "DEPLOYMENT_STATUS": DEPLOYMENT_STATUS,
                "STATUS_MESSAGE"   : STATUS_MESSAGE,
                "LATEST_TAG"       : LATEST_TAG,
                "BEFORE_LAST_TAG"  : BEFORE_LAST_TAG
        ]
    }

}