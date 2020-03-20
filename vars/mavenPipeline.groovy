def call(body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any
        stages {
            stage('Build') { 
                agent {
                    docker {
                        image 'maven:3-jdk-8'
                    }
                }
                steps {
                    mvnBuild()
                }
            }

            stage('Test') {
                agent {
                    docker {
                        image 'maven:3-jdk-8'
                    }
                }
                steps {
                    mvnTest()
                }
            }

            stage('Deploy') {
                agent {
                    docker {
                        image 'debian'
                        args '-u root'
                    }
                }
                steps {
                    unstash 'maven_build'
                    cfDeploy(space: "${pipelineParams.space}")
                }
            }
        }
    }
}
