package com.github.imtiazmasrur.helpers

/**
 * Utility class for Git related operations such as fetching tags.
 */
class GitHelper implements Serializable {
    def script

    GitHelper(script) {
        this.script = script
    }

    // Function to check is it a git repo
    def isGitRepo() {
        return script.sh(script: "git rev-parse --git-dir", returnStdout: true).trim()
    }

    // Function to count total tags
    def getTotalTags() {
        def totalTags = script.sh(script: "git tag | wc -l", returnStdout: true).trim()
        script.echo "🔆 Total Tag(s): ${totalTags}"
        return totalTags
    }

    // Function to check git status
    def gitStatus() {
        script.sh "git status"
        script.echo "✅ Git status checked successfully."
    }

    // Function to git fetch
    def gitFetch() {
        script.sh "git fetch --all --tags"
        script.echo "🪣 Git fetch completed successfully."
    }

    def gitCheckout(tag) {
        script.sh "git checkout tags/${tag}"
    }

    // Function to get the current Git tag
    def getCurrentTag() {
        def currentTag = script.sh(script: "git describe --tags --abbrev=0", returnStdout: true).trim()
        script.echo "⚡️ Current Tag: ${currentTag}"
        return currentTag
    }

    // Function to get the latest Git tag
    def getLatestTag() {
        def latestTag = script.sh(script: "git tag -l --sort=-creatordate | head -n 1", returnStdout: true).trim()
        script.echo "🔥 Latest Tag: ${latestTag}"
        return latestTag
    }

    // Function to get before last one tag
    def getBeforeLastTag() {
        def beforeLastTag = script.sh(script: "git tag -l --sort=-creatordate | sed -n '2p'", returnStdout: true).trim()
        script.echo "🍀 Before Last Tag: ${beforeLastTag}"
        return beforeLastTag
    }

}
