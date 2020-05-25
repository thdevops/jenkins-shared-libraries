def call(Map params) {
    def directory = params.directory

    def args = (directory) ? "-pl :${directory}" : ""

    sh "mvn -B -DskipTests=true ${args} clean package"

    stash name: 'maven_build'

}