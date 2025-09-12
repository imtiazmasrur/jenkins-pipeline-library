import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    stage('Check Git Status, CheckOut Latest Tag...') {
        steps {
            script {
                nodeJS.checkoutCode()
            }
        }
    }
    // stage('Deployment Started...') {
    //     when {
    //         expression { return nodeJS.DEPLOYMENT_STATUS }
    //     }
    //     steps {
    //         script {
    //             nodeJS.deploy()
    //         }
    //     }
    // }
    // stage('Health Check...') {
    //     when {
    //         expression { return nodeJS.DEPLOYMENT_STATUS && !nodeJS.ROLLBACK_STATUS }
    //     }
    //     steps {
    //         script {
    //             nodeJS.healthCheck()
    //         }
    //     }
    // }
    // stage('Rollback Started...') {
    //     when {
    //         expression { return nodeJS.ROLLBACK_STATUS }
    //     }
    //     steps {
    //         script {
    //             nodeJS.rollback()
    //         }
    //     }
    // }

    def status = nodeJS.getStatus()

    echo "Deployment Status: ${status}"
}