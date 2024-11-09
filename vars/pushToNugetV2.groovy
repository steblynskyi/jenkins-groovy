// vars/pushToNugetV2.groovy
def call(boolean isPublishBranch, String nugetProjectPath, String nugetPackageName, String assemblyVersion, String versionSuffix, boolean forcePublish)
{
	if(isPublishBranch == true)
	{
		def filesChanged = sh(returnStdout: true, script: 'git show -m --name-only --first-parent --pretty=""').trim()

		publishToNugetV2(nugetProjectPath, nugetPackageName, assemblyVersion, versionSuffix, forcePublish, filesChanged)
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as isPublishBranch in Jenkinsfile."
		echo "No nuget packages were built or published."
	}
}