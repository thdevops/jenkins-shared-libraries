def call(body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent {
            docker {
                image 'maven:3-jdk-8'
                args '-u root'
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
                    cfDeploy(space: "${pipelineParams.space}")
                }
            }
        }
    }
}
