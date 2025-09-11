import com.github.imtiazmasrur.deployments.pm2Deployment

def call(Map config) {
    def pm2 = new pm2Deployment(this, config.nodeJSVersion, config.nodeJSPath, config.projectName, config.projectDirectory)

    echo pm2.getStatus()

    // Get the current Git tag
    // def currentTag = pm2.gitHelper.getCurrentTag()
    // echo "Current Git Tag: ${currentTag}"
    // def projectStatus = pm2.nodeJSHelper.checkProjectStatus(config.projectName)
    // echo "Node.js Project Status: ${projectStatus}"
}