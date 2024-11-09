// vars/nugetPublish.groovy
def call(String nugetPackageRootName, String assemblyVersion, String versionSuffix, boolean forcePublish, String filesChanged)
{
	def target = "steblynskyi-NuGet-local"
	echo "Files Changed: ${filesChanged}"

	def updateNuGetPackage = filesChanged.contains(nugetPackageRootName) || filesChanged.contains("Jenkinsfile") || forcePublish
	echo "Update NuGet Package: ${updateNuGetPackage}"

	if(updateNuGetPackage == true)
	{
		echo "Publishing NuGet Package: ${nugetPackageRootName}.${assemblyVersion}${versionSuffix}"
		//Create NuGet package
        dotnetPackShellScript = "dotnet pack ${nugetPackageRootName} --configuration Release --output pub --include-source --include-symbols -p:Version=${assemblyVersion}${versionSuffix} -p:AssemblyVersion=${assemblyVersion}"
		sh "${dotnetPackShellScript}"

		def packagePath =  "${nugetPackageRootName}/pub/${nugetPackageRootName}.${assemblyVersion}-*.nupkg"
		def server = Artifactory.server 'packagepublish'
		def uploadSpec = """{
			"files": [
				{
					"pattern": "${nugetPackageRootName}*.nupkg",
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