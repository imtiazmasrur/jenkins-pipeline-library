package com.github.imtiazmasrur.helpers

/**
 * Utility class for Git related operations such as fetching tags.
 */
class GitHelper implements Serializable {
    def script

    GitHelper(script) {
        this.script = script
    }

    def gitStatus() {
        script.sh "git status"
        script.echo "✅ Git status checked successfully."
    }

    // function to git fetch
    def gitFetch() {
        script.sh "git fetch --all --tags"
        script.echo "🪣 Git fetch completed successfully."
    }

    def gitCheckout(tag) {
        script.sh "git checkout tags/${tag}"
    }

    // Function to get the current Git tag
    def getCurrentTag() {
        return script.sh(script: "git describe --tags --abbrev=0", returnStdout: true).trim()
    }

    // Function to get the latest Git tag
    def getLatestTag() {
        return script.sh(script: "git tag -l --sort=-creatordate | head -n 1", returnStdout: true).trim()
    }

    // Function to get before last one tag
    def getBeforeLastTag() {
        return script.sh(script: "git tag -l --sort=-creatordate | sed -n '2p'", returnStdout: true).trim()
    }

}
