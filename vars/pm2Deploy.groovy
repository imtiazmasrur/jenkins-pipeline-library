import com.github.imtiazmasrur.deployments.pm2Deployment

def call(Map config) {
    def pm2 = new pm2Deployment(this, config.nodeJSVersion, config.nodeJSPath, config.projectName, config.projectDirectory)

    def status = pm2.getStatus()
    echo "Deployment Status: ${status}"
}