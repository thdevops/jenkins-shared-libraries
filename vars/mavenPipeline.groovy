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
                    sh 'mvn package' 
                    stash name: 'maven_build'
                }
            }

            stage('Test') {
                steps {
                    unstash 'maven_build'
                    sh 'mvn verify'
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
