package com.github.imtiazmasrur.helpers

/**
 * Utility class for Node.js related operations.
 *
 * Parameters:
 * - nodeJSVersion (required): The version of Node.js to use.
 * - nodeJSPath (required): The base path where Node.js versions are installed.
 * - projectName (required): The name of the project (used for PM2 process management).
 * - additinalBuildCommands (optional): Additional commands to run after the build command.
 * - isBuildRequired (optional): Boolean to indicate if build step is required (default is false).
 */
class NodeJSHelper implements Serializable {

    Object script
    Map config

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

    // Execute the Node.js deployment process
    def executeDeployment() {
        def node = getNodeJSPath()

        script.sh "npm i"
        if (config.additinalBuildCommands) {
            script.sh "${config.additinalBuildCommands}"
        }
        if (config.isBuildRequired) {
            script.sh "npm run build"
        }
        script.sh "${node}/pm2 reload ${config.projectName}"
    }

    // Function to check the project is live
    def healthStatus() {
        def node = getNodeJSPath()
        return script.sh(script: "${node}/pm2 pid ${config.projectName} | head -n 1", returnStdout: true).trim()
    }

    // Function to print pm2 logs
    def pm2SaveAndLogs(lines = 25) {
        def node = getNodeJSPath()
        
        script.sh "${node}/pm2 save"

        script.echo "======================== PM2 LOGS ========================"
        script.sh "${node}/pm2 logs ${config.projectName} --lines ${lines} --nostream"
    }

}
