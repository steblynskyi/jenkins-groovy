def String call()
{
	def repoName = sh(returnStdout: true, script: 'basename $(git remote get-url origin)').trim()
	repoName = repoName[0..-5]
	def s3UploadPath = "${repoName}/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"

	return s3UploadPath
}