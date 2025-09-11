import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

def call(Map config) {
    // def gitHelper = new GitHelper(this)
    def gitHelper = new GitHelper()
    // def nodeJSHelper = new NodeJSHelper(config.nodeJSVersion, config.nodeJSPath)
    
    // Get the current Git tag
    def currentTag = gitHelper.getCurrentTag()
    echo "Current Git Tag: ${currentTag}"
    return currentTag
}