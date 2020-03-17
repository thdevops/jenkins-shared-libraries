def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    String space = params.space

    def path = pwd()

    def pom = new XmlParser().parse(path + "/pom.xml")

    String artifactId = pom.artifactId.text()
    String version = pom.version.text()
    String packaging = pom.packaging.text()

    if (branch == "master") {
        space = "Common Staging Space"

        // Upload in Artifactory
        rtUpload (
            serverId: 'artifactory-bcgplatinion',
            spec: """{
            "files": [
                {
                "pattern": "target/${artifactId}-${version}.${packaging}",
                "target": "thales-devops"
                }
            ]
            }"""
        )
    }

    if (branch == "release") {
        // TODO : deploy using Artifactory
    } else {
        echo "Deploying to ${space} ..."
        withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
            sh "CF_HOME=\$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o thales-devops -s \"${space}\""
            sh "CF_HOME=\$(pwd) cf push thdevops-\${GIT_BRANCH} -p \"target/${artifactId}-${version}.${packaging}\""
        }
    }
}
