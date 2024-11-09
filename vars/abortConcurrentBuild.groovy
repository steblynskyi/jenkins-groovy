// vars/abortConcurrentBuild.groovy
def call(boolean abortConBuild)
{
    properties([
        disableConcurrentBuilds(abortPrevious: abortConBuild)
    ])
}