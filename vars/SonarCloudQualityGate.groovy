// vars/SonarCloudQualityGate.groovy
def call(String invalidBuildBranches) {
    def matched = (env.CHANGE_BRANCH ==~ invalidBuildBranches)

    if(env.CHANGE_ID != null && env.CHANGE_ID != "" && !matched){
        timeout(time: 1, unit: 'HOURS') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
    else{
        echo "Skipping QualityGate check"
    }
}