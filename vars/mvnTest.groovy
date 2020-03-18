def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    unstash 'maven_build'
    sh 'mvn verify'

    def path = pwd()

    def pom = new XmlParser().parse(path + "/pom.xml")

    String artifactId = pom.artifactId.text()
    String version = pom.version.text()
    String packaging = pom.packaging.text()

    String packageName = "${artifactId}-${version}.${packaging}"

    if (branch == "master") {
        // Upload in Artifactory
        rtUpload (
            serverId: 'artifactory-bcgplatinion',
            spec: """{
            "files": [
                {
                "pattern": "target/${packageName}",
                "target": "thales-devops"
                }
            ]
            }"""
        )
    }
}
