def call()
{
	sh 'dotnet new tool-manifest'
	sh 'dotnet tool install dotnet-reportgenerator-globaltool'
	sh 'dotnet tool run reportgenerator -reports:./**/coverage.opencover.xml -targetdir:./TestCoverageReport'

	def s3UploadPath = getTestCoverageS3BucketPath()
	withAWS(region:'us-east-1', credentials:'aws-s3-test-coverage') {
		s3Upload(file:'TestCoverageReport', bucket:'steblynskyi-dev-test-coverage', path:"${s3UploadPath}")
	}
}