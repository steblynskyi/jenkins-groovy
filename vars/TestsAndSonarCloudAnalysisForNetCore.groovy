// vars/TestsAndSonarCloudAnalysisForNetCore.groovy
def call(String[] testAssemblies, String projectKey, String assemblyVersion, boolean runCodeCoverage = true, String solutionRelativePathname = "", String[] testAssembliesSequential = [] as String[]) {
    testAssembliesSequential = testAssembliesSequential == null ? [] as String[] : testAssembliesSequential;
    def testAssembliesCount = testAssemblies.size() + testAssembliesSequential.size()
    echo "Number of testAssemblies: ${testAssembliesCount}"
    if(testAssembliesCount > 0)
    {
        getTestCoverageUrl()
    }

    def prKey = ""
    def prBranch = ""
    def prBase = ""
    def branch = "/d:sonar.branch.name=\"${env.BRANCH_NAME}\""
    if(env.CHANGE_ID != null && env.CHANGE_ID != ""){
        prKey = "/d:sonar.pullrequest.key=\"${env.CHANGE_ID}\""
        prBranch = "/d:sonar.pullrequest.branch=\"${env.CHANGE_BRANCH}\""
        prBase = "/d:sonar.pullrequest.base=\"${env.CHANGE_TARGET}\""
        branch = ""
    }

    def sqScannerMsBuildHome = tool name: 'SonarScanner for MSBuild', type: 'hudson.plugins.sonar.MsBuildSQRunnerInstallation'
    withSonarQubeEnv('SonarQube Server') {
        sh "dotnet ${sqScannerMsBuildHome}/SonarScanner.MSBuild.dll begin /k:\"${projectKey}\" /o:\"steblynskyi\" /v:\"${assemblyVersion}\" /s:\"${env.WORKSPACE}/SonarQube.Analysis.xml\" ${prKey} ${prBranch} ${prBase} ${branch}"
        sh "dotnet restore ${solutionRelativePathname}"
        sh "dotnet build ${solutionRelativePathname}"

        sh "dotnet new sln --name consolidated.tests"
        for(int i = 0; i < testAssemblies.size(); i++)
        {
            sh "dotnet sln consolidated.tests.sln add ${testAssemblies[i]}"
        }
        echo "Running Consolidated tests"
        sh "dotnet test consolidated.tests.sln --logger \"trx;LogFileName=TestResults.trx\" /p:CollectCoverage=true /p:CoverletOutputFormat=opencover /p:CoverletOutput='./TestResults/coverage.opencover.xml'"

        for(int i = 0; i < testAssembliesSequential.size(); i++)
        {
            def testAssembly = testAssembliesSequential[i]
            echo "-Running Tests: ${testAssembly}"
            sh "dotnet test ${testAssembly} --logger \"trx;LogFileName=TestResults.trx\" /p:CollectCoverage=true /p:CoverletOutputFormat=opencover /p:CoverletOutput='./TestResults/coverage.opencover.xml'"
        }

        sh "dotnet ${sqScannerMsBuildHome}/SonarScanner.MSBuild.dll end"
    }

    if(testAssembliesCount > 0 && runCodeCoverage)
    {
        pushTestCoverageToS3Bucket()
    }
}
