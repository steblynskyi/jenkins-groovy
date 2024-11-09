// vars/pushToDocker.groovy
def call(boolean isPublishBranch, String deployImageName, String shortCommit)
{
	deployImageName = "${deployImageName}.dbmigrator"
	pushToDocker(isPublishBranch, deployImageName, shortCommit, "Dockerfile.dbmigrator")
}