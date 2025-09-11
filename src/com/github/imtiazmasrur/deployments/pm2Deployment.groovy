package com.github.imtiazmasrur.deployments

import com.github.imtiazmasrur.helpers.GitHelper
import com.github.imtiazmasrur.helpers.NodeJSHelper

class pm2Deployment implements Serializable {

    def script
    def gitHelper
    def nodeJSHelper

    pm2Deployment(script, nodeJSVersion, nodeJSPath, projectName) {
        this.script = script
        this.gitHelper = new GitHelper(script)
        this.nodeJSHelper = new NodeJSHelper(script, nodeJSVersion, nodeJSPath, projectName)
    }

    def gitCheckout() {
    }
    
    def installDependencies() {
    }

    def build() {
    }

    def deploy() {
    }

    def restart() {
    }

    def healthCheck() {
    }

    def rollback() {
    }

}