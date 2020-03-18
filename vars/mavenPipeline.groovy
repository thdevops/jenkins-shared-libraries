def call(body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent {
            docker {
                image 'tenjaa/maven-cf'
            }
        }
        stages {
            stage('Build') { 
                steps {
                    build()
                }
            }

            stage('Test') {
                steps {
                    test()
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
