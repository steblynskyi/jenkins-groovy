// vars/publishToNugetV2.groovy
def call(String nugetProjectPath, String nugetPackageName, String assemblyVersion, String versionSuffix, boolean forcePublish, String filesChanged)
{
	def target = "steblynskyi-NuGet-local"
	echo "Files Changed: ${filesChanged}"

	def updateNuGetPackage = filesChanged.contains("${nugetProjectPath}/${nugetPackageName}") || filesChanged.contains("Jenkinsfile") || forcePublish
	echo "Update NuGet Package: ${updateNuGetPackage}"

	if(updateNuGetPackage == true)
	{
		def fullNugetPackageName = "${nugetPackageName}.${assemblyVersion}${versionSuffix}.nupkg";
		def presentWorkingDir = "${env.WORKSPACE}";
		def nugetOutputDir = "${presentWorkingDir}/${nugetProjectPath}/pub";

		echo "Publishing NuGet Package: ${nugetPackageName}"
		//Create NuGet package
        dotnetPackShellScript = "dotnet pack ${nugetProjectPath} --configuration Release --output ${nugetOutputDir} --include-source --include-symbols -p:Version=${assemblyVersion}${versionSuffix} -p:AssemblyVersion=${assemblyVersion}"
		sh "${dotnetPackShellScript}"

		def packagePath =  "${nugetOutputDir}/${fullNugetPackageName}"
		def server = Artifactory.server 'packagepublish'
		def uploadSpec = """{
			"files": [
				{
					"pattern": "${packagePath}",
					"target": "${target}"
				}
			]
		}"""
		server.upload(uploadSpec)

		// Upload to Artifactory.
		def buildInfo1 = server.upload spec: uploadSpec

		// Publish the build to Artifactory
		server.publishBuildInfo buildInfo1
	}
	else
	{
		echo "NuGet Package: ${nugetProjectPath} does not require update."
	}
}