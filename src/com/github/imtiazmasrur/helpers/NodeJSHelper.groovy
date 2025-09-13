package com.github.imtiazmasrur.helpers

/**
 * Utility class for Node.js related operations such as checking project status.
 */
class NodeJSHelper implements Serializable {

    def script
    def config

    // Constructor to initialize Node.js version and path
    NodeJSHelper(script, Map config) {
        if (!config.nodeJSVersion || !config.nodeJSPath || !config.projectName) {
            throw new Exception("nodeJSVersion, nodeJSPath and projectName are required.")
        }
        this.script = script
        this.config = config
    }

    // Function to get the Node.js path
    def getNodeJSPath() {
        return "${config.nodeJSPath}/v${config.nodeJSVersion}/bin"
    }

    // Function to check the project is live
    def healthStatus(projectName) {
        def node = getNodeJSPath()
        return script.sh(script: "${node}/pm2 pid ${config.projectName} | head -n 1", returnStdout: true).trim()
    }

}
