package com.github.imtiazmasrur.helpers

/**
 * Utility class for Git related operations such as fetching tags.
 */
class GitHelper implements Serializable {

    def script

    GitHelper(script) {
        this.script = script
    }

    // Function to get the current Git tag
    String getCurrentTag() {
        return script.sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
    }

    // Function to get the latest Git tag
    String getLatestTag() {
        return script.sh(script: 'git tag -l --sort=-creatordate | head -n 1', returnStdout: true).trim()
    }

    // Function to get before last one tag
    String getBeforeLastTag() {
        return script.sh(script: 'git tag -l --sort=-creatordate | sed -n "2p"', returnStdout: true).trim()
    }

}
