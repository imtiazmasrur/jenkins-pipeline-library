package com.github.imtiazmasrur.git

/**
 * Utility class for Git related operations such as fetching tags.
 */
class Git {

    // Function to get the current Git tag
    String getCurrentTag() {
        return sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
    }

    // Function to get the latest Git tag
    String getLatestTag() {
        return sh(script: 'git tag -l --sort=-creatordate | head -n 1', returnStdout: true).trim()
    }

    // Function to get before last one tag
    String getBeforeLastTag() {
        return sh(script: 'git tag -l --sort=-creatordate | sed -n "2p"', returnStdout: true).trim()
    }

}
