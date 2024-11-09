// vars/deployDockerImage.groovy
def call(boolean autoDeployBranch, String deploymentName, String deploymentImageName, String deploymentContext, String deploymentNamespace, String shortCommit, String containerName = "")
{
	if(autoDeployBranch == true)
	{
		if(containerName == "")
		{
			echo "containerName is not specified, using deploymentImageName"
			containerName = deploymentImageName
		}

		echo "Deploying from branch: ${env.BRANCH_NAME}"
		sh "kubectl set image deployment/${deploymentName} ${containerName}=steblynskyi-docker.jfrog.io/${deploymentImageName}:${shortCommit} --namespace ${deploymentNamespace} --context ${deploymentContext}"
	}
	else
	{
		echo "Branch: ${env.BRANCH_NAME} is not configured as autoDeployBranch in Jenkinsfile."
		echo "No docker image was deployed."
	}
}