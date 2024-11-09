// vars/buildProjects.groovy
def call(String[] projectsToPublish, String assemblyVersion, String versionSuffix)
{
	echo "Number of Db Migrator projectsToPublish: ${projectsToPublish.size()}"
	for(int i = 0; i < projectsToPublish.size(); i++)
	{
		def publishProject = projectsToPublish[i]

		def artifactsPath = '$(pwd)/dbmigrator'
		echo "Building DB Migrator Solution: ${publishProject}"
		sh "dotnet publish ${publishProject} -c Release -o ${artifactsPath} -p:Version=${assemblyVersion}${versionSuffix} -p:AssemblyVersion=${assemblyVersion}"
	}
}