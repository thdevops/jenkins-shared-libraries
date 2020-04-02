def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    unstash 'maven_build'
    sh 'mvn verify'

    if (branch ==~ /release\/(.*)/) {

        def path = pwd()

        def pom = new XmlParser().parse(path + "/pom.xml")

        String artifactId = pom.artifactId.text()
        String version = pom.version.text()
        String packaging = pom.packaging.text()

        String packageName = "${artifactId}-${version}.${packaging}"
        String zipName = "${artifactId}-${version}.tgz"

        sh "tar czvf ${zipName} manifest.yml target/${packageName}"

        // Upload in Artifactory
        rtUpload (
            serverId: 'artifactory-bcgplatinion',
            spec: """{
            "files": [
                {
                "pattern": "${zipName}",
                "target": "thales-devops",
                "props": "app.name=${artifactId};app.version=${version};app.type=maven"
                }
            ]
            }"""
        )
    }
}
