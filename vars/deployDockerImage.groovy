// vars/deployDockerImage.groovy
def call(boolean isDeployBranch, String deployImageName, String deployContext, String deployNamespace, String shortCommit, String containerName = "")
{
	if(isDeployBranch == true)
	{
		if(containerName == "")
		{
			echo "containerName is not specified, using deploymentImageName"
			containerName = deployImageName
		}

		echo "Deploying from branch: ${env.BRANCH_NAME}"
		sh "kubectl set image deployment/${deployImageName}-${deployContext} ${containerName}=steblynskyi-docker.jfrog.io/${deployImageName}:${shortCommit} --namespace ${deployNamespace} --context ${deployContext}"
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as isDeployBranch in Jenkinsfile."
		echo "No docker image was deployed."
	}
}