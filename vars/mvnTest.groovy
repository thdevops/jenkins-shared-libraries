def call(Map params) {
    def directory = params.directory

    def args = (directory) ? "-pl :${directory}" : ""

    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    unstash 'maven_build'

    sh "pwd"
    sh "ls -alh"
    sh "cat pom.xml"

    sh "mvn ${args} verify"

    //if (branch ==~ /release\/(.*)/) {
    if (branch == "master" || branch == "develop") {
/*        def path = pwd()

        def xmlContent = readFile(path + "/pom.xml")

        def pom = new XmlParser().parseText(xmlContent)

        String version = pom.version.text()
        if (!version) version = pom.parent.version.text()

        def endsWithSnapshot = version.endsWith("-SNAPSHOT")*/
/*
        if ((branch == "master" && endsWithSnapshot == true) || (branch == "develop" && endsWithSnapshot == false)) {
            // Bad version : snapshot on master, or release on develop. Crash the pipeline.
            currentBuild.result = 'FAILURE'
            error("Bad version ${version} for branch ${branch}")
        }
*/
//        sh 'mvn deploy'

        def path = pwd()

        if (directory) path = path + "/" + directory

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
