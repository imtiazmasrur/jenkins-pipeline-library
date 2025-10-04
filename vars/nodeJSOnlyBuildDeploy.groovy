import com.github.imtiazmasrur.deployments.NodeJSOnlyBuildDeployment

/**
 * Jenkins shared library function to only build and deploy a NodeJS project using Git tags.
 *
 * Usage:
 * nodeJSOnlyBuildDeploy(
 *     nodeJSVersion: "20.18.0",
 *     nodeJSPath: "/root/.nvm/versions/node",
 *     projectName: "my-node-app",
 *     projectDirectory: "/var/www/my-node-app",
 *     buildCommand: "npm run build:prod", // Optional. Build command. Default command is "npm run build".
 * )
 *
 * The function will:
 * 1. Check Git status and fetch the latest tags.
 * 2. Checkout the latest tag.
 * 3. Install dependencies and build the project.
 * 4. Perform rollback if the deployment fails.
 *
 * Parameters:
 * - nodeJSVersion: The version of NodeJS to use.
 * - nodeJSPath: The base path where NodeJS versions are installed.
 * - projectName: The name of the project (used for PM2 process management).
 * - projectDirectory: The directory of the project to deploy.
 * - buildCommand: (optional) Build command for your project. Default command is "npm run build".
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
    NodeJSOnlyBuildDeployment nodeJSOnlyBuild = new NodeJSOnlyBuildDeployment(this, config)

    stage("Check Git Status, CheckOut Latest Tag...") {
        dir("${config.projectDirectory}") {
            nodeJSOnlyBuild.checkoutCode()
        }
    }
    if (nodeJSOnlyBuild.state.deploymentStatus) {
        stage("Deployment Started...") {
            dir("${config.projectDirectory}") {
                nodeJSOnlyBuild.deploy()
            }
        }
    }
    if (nodeJSOnlyBuild.state.rollbackStatus) {
        stage("Rollback Started...") {
            dir("${config.projectDirectory}") {
                nodeJSOnlyBuild.rollback()
            }
        }
    }

    return nodeJSOnlyBuild.getStatus()
}
