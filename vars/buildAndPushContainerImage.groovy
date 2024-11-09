// vars/buildAndPushContainerImage.groovy
def call(boolean isPublishBranch, String deployImageName, String imageVersion, String dockerFile = "Dockerfile", String context = ".", String buildkitdUrl = "tcp://buildkitd:8080", String frontendVersion = "dockerfile.v0")
{
	if(isPublishBranch == true)
	{
		stage "Build And Push Container Image for ${deployImageName}:"
		sh "buildctl --addr ${buildkitdUrl} build --frontend=${frontendVersion} --local context=${context} --local dockerfile=. --opt filename=${dockerFile} --output type=image,name=steblynskyi-docker.jfrog.io/${deployImageName}:${imageVersion},push=true"
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as isPublishBranch in Jenkinsfile."
		echo "No docker images were built or published."
	}
}