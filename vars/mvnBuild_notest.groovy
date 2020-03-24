def call(Map params) {

    sh 'mvn -DskipTests=true package'

    stash name: 'maven_build'

}