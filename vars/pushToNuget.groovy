// vars/pushToNuget.groovy
def call(boolean isPublishBranch, String[] nugetPackageArray, String assemblyVersion, String versionSuffix, boolean forcePublish)
{
	if(isPublishBranch == true)
	{
		echo "Number of Nuget Packages to push: ${nugetPackageArray.size()}"
		for(int i = 0; i < nugetPackageArray.size(); i++)
		{
			def nugetPackageRootName = nugetPackageArray[i]
			def filesChanged = sh(returnStdout: true, script: 'git show -m --name-only --first-parent --pretty=""').trim()

			publishToNuget(nugetPackageRootName, assemblyVersion, versionSuffix, forcePublish, filesChanged)
		}
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as isPublishBranch in Jenkinsfile."
		echo "No nuget packages were built or published."
	}
}