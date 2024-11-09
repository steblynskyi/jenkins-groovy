// vars/pushToDocker.groovy
def call(boolean isPublishBranch, String deployImageName, String shortCommit, String dockerFile = "Dockerfile")
{
	if(isPublishBranch == true)
	{
		stage "Build Container Image for ${deployImageName}:"
		sh "docker build -f ${dockerFile} -t steblynskyi-docker.jfrog.io/${deployImageName}:${shortCommit} ."

		stage "Push ${deployImageName} to Artifactory"
		sh "docker push steblynskyi-docker.jfrog.io/${deployImageName}:${shortCommit}"
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as isPublishBranch in Jenkinsfile."
		echo "No docker images were built or published."
	}
}