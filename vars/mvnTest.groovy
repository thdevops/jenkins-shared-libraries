def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    unstash 'maven_build'
    sh 'mvn verify'

//    if (branch ==~ /release\/(.*)/) {

        def path = pwd()

        def pom = new XmlParser().parse(path + "/pom.xml")

        String artifactId = pom.artifactId.text()
        String version = pom.version.text()
        String packaging = pom.packaging.text()

        String packageName = "${artifactId}-${version}.${packaging}"
        String zipName = "${artifactId}-${version}.zip"

        sh "apt-get install -y zip"
        sh "zip ${zipName} manifest.yml target/${packageName}"

        // Upload in Artifactory
        rtUpload (
            serverId: 'artifactory-bcgplatinion',
            spec: """{
            "files": [
                {
                "pattern": "${packageName}.zip",
                "target": "thales-devops",
                "props": "app.name=${artifactId};app.version=${version};app.type=maven"
                }
            ]
            }"""
        )
//    }
}
