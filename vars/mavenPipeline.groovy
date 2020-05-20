def call(body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent {
            kubernetes {
                defaultContainer 'builder'
            }
        }
        stages {
            stage('Build') { 
                steps {
                    mvnBuild()
                }
            }

            stage('Test') {
                steps {
                    mvnTest()
                }
            }

            stage('Deploy') {
                steps {
                    unstash 'maven_build'
                    cfDeploy(url: "${pipelineParams.url}", org: "${pipelineParams.org}", space: "${pipelineParams.space}")
                }
            }
        }
    }
}
