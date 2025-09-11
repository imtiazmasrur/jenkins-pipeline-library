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
    NodeJSHelper(script, nodeJSVersion, nodeJSPath, projectName) {
        this.script = script
        this.nodeJSVersion = nodeJSVersion
        this.nodeJSPath = nodeJSPath
        this.projectName = projectName
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
