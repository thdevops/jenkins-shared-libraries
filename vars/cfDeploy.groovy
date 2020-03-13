def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    String space = params.space

    def path = pwd()

    def pom = new XmlSlurper().parse(path + "/pom.xml")

    String artifactId = pom.artifactId.text()
    String version = pom.version.text()
    String packaging = pom.packaging.text()

    if (branch == "master") {
        space = "Common Staging Space"
    }

    echo "Deploying ..."
    withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
        sh "CF_HOME=\$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o aurelien -s \"${space}\""
        sh "CF_HOME=\$(pwd) cf push thdevops-\${GIT_BRANCH} -p \"${artifactId}-${version}.${packaging}\""
    }
}
