// vars/runUnitTests.groovy
def call(String[] testAssemblies, boolean runCodeCoverage = true)
{
	echo "Number of testAssemblies: ${testAssemblies.size()}"

	if(testAssemblies.size() > 0)
	{
		getTestCoverageUrl()
	}

	echo "--Generating MSBuild Response file."
	//generates MSBuild Response file to reference for msbuild settings.
	sh "echo /p:CollectCoverage=${runCodeCoverage} > coverage.rsp"

	for(int i = 0; i < testAssemblies.size(); i++)
	{
		def testAssembly = testAssemblies[i]
		echo "-Running Tests: ${testAssembly}"
		sh "dotnet test ${testAssembly} @coverage.rsp"
	}

	if(testAssemblies.size() > 0 && runCodeCoverage)
	{
		pushTestCoverageToS3Bucket()
	}
}