// vars/SonarCloudAnalysis.groovy
def call(String assemblyVersion) {
    def prKey = ""
    def prBranch = ""
    def prBase = ""
    def branch = "-Dsonar.branch.name=\"${env.BRANCH_NAME}\""
    if(env.CHANGE_ID != null && env.CHANGE_ID != ""){
        prKey = "-Dsonar.pullrequest.key=\"${env.CHANGE_ID}\""
        prBranch = "-Dsonar.pullrequest.branch=\"${env.CHANGE_BRANCH}\""
        prBase = "-Dsonar.pullrequest.base=\"${env.CHANGE_TARGET}\""
        branch = ""
    }

    def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    withSonarQubeEnv('SonarQube Server') {
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectVersion=\"${assemblyVersion}\" ${prKey} ${prBranch} ${prBase} ${branch}"
    }
}