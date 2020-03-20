def call(Map params) {

    sh 'mvn package'

    stash name: 'maven_build'

}