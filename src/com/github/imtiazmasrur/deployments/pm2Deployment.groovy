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
            script.sh "git status"
            script.echo "✅ Git status checked successfully."

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
                script.sh "git fetch"
                script.echo "🪣 Git fetch completed successfully."

                // Set environment variables
                LATEST_TAG = latestTag
                BEFORE_LAST_TAG = beforeLastTag

                script.echo "🔥 Latest Tag: ${LATEST_TAG}"
                script.echo "🍀 Before Last Tag: ${BEFORE_LAST_TAG}"

                script.sh "git checkout tags/${LATEST_TAG}"

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
    }

    def build() {
    }

    def deploy() {
    }

    def restart() {
    }

    def healthCheck() {
    }

    def rollback() {
    }

}