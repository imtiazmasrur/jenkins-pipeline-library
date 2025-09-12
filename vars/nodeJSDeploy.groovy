import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        dir("${config.projectDirectory}") {
            nodeJS.checkoutCode()
        }
    }
    stage('Deployment Started...') {
        dir("${config.projectDirectory}") {
            when {
                expression { return nodeJS.DEPLOYMENT_STATUS }
            }
            nodeJS.deploy()
        }
    }
    stage('Health Check...') {
        dir("${config.projectDirectory}") {
            when {
                expression { return nodeJS.DEPLOYMENT_STATUS && !nodeJS.ROLLBACK_STATUS }
            }
            nodeJS.healthCheck()
        }
    }
    stage('Rollback Started...') {
        dir("${config.projectDirectory}") {
            when {
                expression { return nodeJS.ROLLBACK_STATUS }
            }
            nodeJS.rollback()
        }
    }

    def status = nodeJS.getStatus()

    echo "Deployment Status: ${status}"
}