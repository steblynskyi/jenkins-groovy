// vars/runKubeJobV2.groovy
def call(String jobName, String deploymentImageName, String deploymentContext, String deploymentNamespace, String shortCommit, Map envVars, String[] cmdArgs, String[] command = ["/bin/sh", "-c"] )
{

    Map kubeJobTmp = [
        apiVersion: "batch/v1",
        kind: "Job",
        metadata: [
            name: "${jobName}-${shortCommit}",
            namespace: "${deploymentNamespace}"
            ],
        spec: [
            ttlSecondsAfterFinished: 2000,
            backoffLimit: 1,
            template: [
                spec: [
                    imagePullSecrets: [[name:"steblynskyi"]],
                    restartPolicy: "Never",
                    containers: [
                        [
                        name: "${jobName}-${shortCommit}",
                        image: "steblynskyi-docker.jfrog.io/${deploymentImageName}:${shortCommit}",
                        imagePullPolicy: "Always"
                        ],
                    ],
                ],
            ],
        ],
    ]

    def emptyEnv = [env: [[:]]]
    def listMap = [[:]]
    if (0 < envVars.size()) {
            envVars.eachWithIndex{ k, v, i ->
                    listMap[i] = [name: k, value: v]
            }
            emptyEnv.env = listMap
            kubeJobTmp.spec.template.spec.containers[0] << emptyEnv
    }

    if(cmdArgs && cmdArgs.length > 0) {
        def emptyArgs = [args: []]
        emptyArgs.args.addAll(cmdArgs)
        kubeJobTmp.spec.template.spec.containers[0] << emptyArgs
    }

    def emptyCommand = [command: []]
    emptyCommand.command.addAll(command)
    kubeJobTmp.spec.template.spec.containers[0] << emptyCommand


    writeYaml file: "${jobName}-${shortCommit}.yaml", data: kubeJobTmp

    sh "kubectl apply -f ${jobName}-${shortCommit}.yaml --context ${deploymentContext}"
    sh """
        kubectl wait --for=condition=complete job/${jobName}-${shortCommit} --timeout=300s --namespace ${deploymentNamespace} --context ${deploymentContext} &
        completion_pid=\$!

        kubectl wait --for=condition=failed job/${jobName}-${shortCommit} --timeout=300s --namespace ${deploymentNamespace} --context ${deploymentContext} && exit 1 &
        failure_pid=\$!

        while true
        do
            if ! ps -p \$completion_pid > /dev/null; then
                echo "Job completed"
                kubectl logs job/${jobName}-${shortCommit} --namespace ${deploymentNamespace} --context ${deploymentContext}
                wait \$completion_pid
                exit \$?
            elif ! ps -p \$failure_pid > /dev/null; then
                echo "Job failed"
                kubectl logs job/${jobName}-${shortCommit} --namespace ${deploymentNamespace} --context ${deploymentContext}
                wait \$failure_pid
                exit \$?
            else
                sleep 5
            fi
        done
    """
}