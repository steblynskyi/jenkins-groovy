// vars/nvm.groovy
def call(String nodeversion= "18")
{
	sh "source ~/.bashrc > /dev/null 2>&1 && nvm install ${nodeversion} > /dev/null 2>&1" 

}