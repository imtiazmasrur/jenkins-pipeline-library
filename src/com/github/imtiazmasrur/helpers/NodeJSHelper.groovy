package com.github.imtiazmasrur.helpers

/**
 * Utility class for Node.js related operations such as checking project status.
 */
class NodeJSHelper implements Serializable {

    // Node.js version and installation path
    String nodeJSVersion
    String nodeJSPath
    String projectName

    // Constructor to initialize Node.js version and path
    NodeJSHelper(String version, String path) {
        this.nodeJSVersion = version
        this.nodeJSPath = path
    }

    // Function to get the Node.js path
    String getNodeJSPath() {
        return "${this.nodeJSPath}/v${this.nodeJSVersion}/bin"
    }

    // Function to check the project is live
    String checkProjectStatus(String projectName) {
        String nodeJSPath = this.getNodeJSPath()
        return sh(script: "${nodeJSPath}/pm2 pid ${projectName} | head -n 1", returnStdout: true).trim()
    }

}
