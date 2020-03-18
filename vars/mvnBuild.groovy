def call(Map params) {

    sh "mvn build"

    stash name: 'maven_build'



}