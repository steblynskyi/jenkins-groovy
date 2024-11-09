// vars/TestsAndSonarCloudAnalysisForNetCoreV2.groovy
/* Examples
    --Only mandatory parameters without optional
        TestsAndSonarCloudAnalysisForNetCoreV2(projectKey, assemblyVersion, shouldGenerateHtmlCoverageReport)

    --With optional parameters. Optional should go only after mandatory. The order of the passed optional parameters is not important
        TestsAndSonarCloudAnalysisForNetCoreV2(
            projectKey,
            assemblyVersion,
            shouldGenerateHtmlCoverageReport,
            coverletCollectorSettings: "coverlet.collector.settings.xml",
            testAssemblies: testAssemblies)

    --OptionalParameters
        solutionRelativePath
        coverletCollectorSettings
        testAssemblies
        testAssembliesSequential
*/
def call(Map optionalParameters = [:], String projectKey, String assemblyVersion, boolean generateHtmlCoverageReport) {

    def solutionRelativePath = optionalParameters.solutionRelativePath ?: ""
    def coverletCollectorSettings = optionalParameters.coverletCollectorSettings ?: ""
    def testAssemblies = optionalParameters.testAssemblies ?: [] as String[]
    def testAssembliesSequential = optionalParameters.testAssembliesSequential ?: [] as String[]

    testAssembliesSequential = testAssembliesSequential == null ? [] as String[] : testAssembliesSequential;
    def testAssembliesCount = testAssemblies.size() + testAssembliesSequential.size()
    echo "Number of testAssemblies: ${testAssembliesCount}"
    if(generateHtmlCoverageReport)
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
        sh "dotnet restore ${solutionRelativePath}"
        sh "dotnet build ${solutionRelativePath}"

        def commandLineArgs = "--collect:'XPlat Code Coverage' -- " +
                              "DataCollectionRunSettings.DataCollectors.DataCollector.Configuration.Format='opencover' " +
                              "DataCollectionRunSettings.DataCollectors.DataCollector.Configuration.IncludeTestAssembly='false' " +
                              "DataCollectionRunSettings.DataCollectors.DataCollector.Configuration.SkipAutoProps='true' " +
                              "DataCollectionRunSettings.DataCollectors.DataCollector.Configuration.ExcludeByAttribute='TestSDKAutoGeneratedCode,ExcludeFromCodeCoverageAttribute,GeneratedCodeAttribute'"

        if(!(coverletCollectorSettings == null || coverletCollectorSettings.isEmpty()))
        {
            commandLineArgs = "--settings:${coverletCollectorSettings}"
        }

        if(testAssembliesCount > 0)
        {
            echo "Running Consolidated tests"

            if(testAssemblies.size() > 0)
            {
                sh "dotnet new sln --name consolidated.tests"
                for(int i = 0; i < testAssemblies.size(); i++)
                {
                    sh "dotnet sln consolidated.tests.sln add ${testAssemblies[i]}"
                }

                sh "dotnet test consolidated.tests.sln ${commandLineArgs}"
            }

            for(int i = 0; i < testAssembliesSequential.size(); i++)
            {
                def testAssembly = testAssembliesSequential[i]
                echo "-Running Tests: ${testAssembly}"
                sh "dotnet test ${testAssembly} ${commandLineArgs}"
            }
        }
        else
        {
            echo "Running $projectKey tests"
            sh "dotnet test ${solutionRelativePath} ${commandLineArgs}"
        }

        sh "dotnet ${sqScannerMsBuildHome}/SonarScanner.MSBuild.dll end"
    }

    if(generateHtmlCoverageReport)
    {
        pushTestCoverageToS3Bucket()
    }
}
