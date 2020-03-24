def call(Map params) {

    sh './mvnw -DskipTests=true package'

    stash name: 'maven_build'

}