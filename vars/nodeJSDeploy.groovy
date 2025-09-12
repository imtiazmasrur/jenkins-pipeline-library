import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        nodeJS.checkoutCode()
    }
    stage('Deployment Started...') {
        nodeJS.deploy()
    }
    stage('Health Check...') {
        nodeJS.healthCheck()
    }
    stage('Rollback if required...') {
        nodeJS.rollback()
    }

    def status = nodeJS.getStatus()

    echo "Deployment Status: ${status}"
}