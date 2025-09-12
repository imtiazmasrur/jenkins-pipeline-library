import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        dir("${config.projectDirectory}") {
            nodeJS.checkoutCode()
        }
    }
    stage('Deployment Started...') {
        expression { return nodeJS.DEPLOYMENT_STATUS }

        dir("${config.projectDirectory}") {
            nodeJS.deploy()
        }
    }
    stage('Health Check...') {
        expression { return nodeJS.DEPLOYMENT_STATUS && !nodeJS.ROLLBACK_STATUS }

        dir("${config.projectDirectory}") {
            nodeJS.healthCheck()
        }
    }
    stage('Rollback Started...') {
        expression { return nodeJS.ROLLBACK_STATUS }

        dir("${config.projectDirectory}") {
            nodeJS.rollback()
        }
    }

    return nodeJS.getStatus()
}