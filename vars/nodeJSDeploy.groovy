import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        dir("${config.projectDirectory}") {
            nodeJS.checkoutCode()
        }
    }
    if (nodeJS.DEPLOYMENT_STATUS) {
        stage('Deployment Started...') {
            dir("${config.projectDirectory}") {
                nodeJS.deploy()
            }
        }
    }
    if (nodeJS.DEPLOYMENT_STATUS && !nodeJS.ROLLBACK_STATUS) {
        stage('Health Check...') {
            dir("${config.projectDirectory}") {
                nodeJS.healthCheck()
            }
        }
    }
    if (nodeJS.ROLLBACK_STATUS){
        stage('Rollback Started...') {
            dir("${config.projectDirectory}") {
                nodeJS.rollback()
            }
        }
    }

    return nodeJS.getStatus()
}