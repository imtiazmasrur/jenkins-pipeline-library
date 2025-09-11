import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

def call(Map config) {
    def gitHelper = new GitHelper(this)
    def nodeJSHelper = new NodeJSHelper(config.nodeJSVersion, config.nodeJSPath, config.projectName)

    // Get the current Git tag
    def currentTag = gitHelper.getCurrentTag()
    echo "Current Git Tag: ${currentTag}"
    def projectStatus = nodeJSHelper.checkProjectStatus(config.projectName)
    echo "Node.js Project Status: ${projectStatus}"
}