import com.github.imtiazmasrur.deployments.NodeJSDeployment

def call(Map config) {
    def nodeJS = new NodeJSDeployment(this, config)

    def status = nodeJS.getStatus()
    echo "Deployment Status: ${status}"
}