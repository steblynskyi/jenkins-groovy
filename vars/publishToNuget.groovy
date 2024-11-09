// vars/publishToNuget.groovy
def call(String nugetPackageRootName, String assemblyVersion, String versionSuffix, boolean forcePublish, String filesChanged)
{
	def target = "steblynskyi-NuGet-local"
	echo "Files Changed: ${filesChanged}"

	def updateNuGetPackage = filesChanged.contains(nugetPackageRootName) || filesChanged.contains("Jenkinsfile") || forcePublish
	echo "Update NuGet Package: ${updateNuGetPackage}"

	if(updateNuGetPackage == true)
	{
		def nugetPackagePathParts = nugetPackageRootName.split('/');
		def nugetPackageNamePart = nugetPackagePathParts[nugetPackagePathParts.length-1];
		def nugetPackageName = "${nugetPackageNamePart}.${assemblyVersion}${versionSuffix}.nupkg";
		def presentWorkingDir = "${env.WORKSPACE}";
		def nugetOutputDir = "${presentWorkingDir}/${nugetPackageRootName}/pub";

		echo "Publishing NuGet Package: ${nugetPackageName}"
		//Create NuGet package
        dotnetPackShellScript = "dotnet pack ${nugetPackageRootName} --configuration Release --output ${nugetOutputDir} --include-source --include-symbols -p:Version=${assemblyVersion}${versionSuffix} -p:AssemblyVersion=${assemblyVersion}"
		sh "${dotnetPackShellScript}"

		def packagePath =  "${nugetOutputDir}/${nugetPackageName}"
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
		echo "NuGet Package: ${nugetPackageRootName} does not require update."
	}
}