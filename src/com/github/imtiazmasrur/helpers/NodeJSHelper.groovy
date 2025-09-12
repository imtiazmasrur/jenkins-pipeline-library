package com.github.imtiazmasrur.helpers

/**
 * Utility class for Node.js related operations such as checking project status.
 */
class NodeJSHelper implements Serializable {

    def script
    def nodeJSVersion
    def nodeJSPath
    def projectName

    // Constructor to initialize Node.js version and path
    NodeJSHelper(script, Map config) {
        if (!config.nodeJSVersion || !config.nodeJSPath || !config.projectName) {
            throw new Exception("nodeJSVersion, nodeJSPath and projectName are required.")
        }
        this.script = script
        this.nodeJSVersion = config.nodeJSVersion
        this.nodeJSPath = config.nodeJSPath
        this.projectName = config.projectName
    }

    // Function to get the Node.js path
    def getNodeJSPath() {
        return "${nodeJSPath}/v${nodeJSVersion}/bin"
    }

    // Function to check the project is live
    def healthStatus(projectName) {
        def nodePath = getNodeJSPath()
        return script.sh(script: "${nodePath}/pm2 pid ${projectName} | head -n 1", returnStdout: true).trim()
    }

}
