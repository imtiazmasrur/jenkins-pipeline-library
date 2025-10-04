import com.github.imtiazmasrur.deployments.NodeJSDeployment

/**
 * Jenkins shared library function to deploy a Node.js project using Git tags.
 *
 * Usage:
 * nodeJSDeploy(
 *     nodeJSVersion: '20.18.0',
 *     nodeJSPath: '/root/.nvm/versions/node',
 *     projectName: 'my-node-app',
 *     projectDirectory: '/var/www/my-node-app',
 *     additinalBuildCommands: 'npm run lint && npm run test', // Optional
 *     isBuildRequired: true // Optional, default is false. This will run 'npm run build' if true.
 * )
 *
 * The function will:
 * 1. Check Git status and fetch the latest tags.
 * 2. Checkout the latest tag.
 * 3. Install dependencies and build the project if required.
 * 4. Restart the PM2 process.
 * 5. Perform a health check and rollback if the deployment fails.
 *
 * Parameters:
 * - nodeJSVersion: The version of Node.js to use.
 * - nodeJSPath: The base path where Node.js versions are installed.
 * - projectName: The name of the project (used for PM2 process management).
 * - projectDirectory: The directory of the project to deploy.
 * - additinalBuildCommands (optional): Additional commands to run after the build command.
 * - isBuildRequired (optional): Boolean to indicate if build step is required (default is false). This will run 'npm run build' if true.
 *
 * Returns:
 * A map containing deployment status, rollback status, and status message.
 * Example return value:
 * [
 *     DEPLOYMENT_STATUS: true,
 *     ROLLBACK_STATUS: false,
 *     STATUS_MESSAGE: "🚀 Project deployed successfully. 😎 v1.2.3"
 *     CURRENT_TAG: "v1.2.2",
 *     LATEST_TAG: "v1.2.3"
 * ]
 */
def call(Map config) {
    NodeJSDeployment nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        dir("${config.projectDirectory}") {
            nodeJS.checkoutCode()
        }
    }
    if (nodeJS.state.deploymentStatus) {
        stage('Deployment Started...') {
            dir("${config.projectDirectory}") {
                nodeJS.deploy()
            }
        }
    }
    if (nodeJS.state.deploymentStatus && !nodeJS.state.rollbackStatus) {
        stage('Health Check...') {
            dir("${config.projectDirectory}") {
                nodeJS.healthCheck()
            }
        }
    }
    if (nodeJS.state.rollbackStatus){
        stage('Rollback Started...') {
            dir("${config.projectDirectory}") {
                nodeJS.rollback()
            }
        }
    }

    return nodeJS.getStatus()
}
