def call()
{
	def s3UploadPath = getTestCoverageS3BucketPath()

	echo "Test Coverage Url Path"
	echo "https://utils-test-coverage-web.qa-steblynskyi.com/${s3UploadPath}index.htm"
}