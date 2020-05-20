def call(Map params) {
    def directory = (params.directory) ? params.directory : "."

    dir (directory) {

        def scmVars = checkout scm
        String branch = scmVars.GIT_BRANCH

        def envVars = env.getEnvironment()

        String url = params.url
        if (!url || url == "null")  url = envVars['DEFAULT_CF_URL']
        String org = params.org
        if (!org || org == "null") org = envVars['DEFAULT_CF_ORG']
        String space = params.space
        if (!space || space == "null") space = envVars['DEFAULT_CF_SPACE']

        def path = pwd()

    print(path)

        def xmlContent = readFile(path + "/pom.xml")
    print xmlContent
        def pom = new XmlParser().parseText(xmlContent)

        String artifactId = pom.artifactId.text()
        String version = pom.version.text()
        String packaging = pom.packaging.text()

        if (!version) version = pom.parent.version.text()

        if (!packaging || packaging != "war") packaging = "jar"

        def urlBranch = branch.replaceAll('/', '').toLowerCase()
        def urlOrg = org.replaceAll('/', '').replaceAll(' ', '').toLowerCase()
        def urlSpace = space.replaceAll('/', '').replaceAll(' ', '').toLowerCase()

        def routeArgs = "-n ${artifactId}-${urlOrg}-${urlBranch} -d ${url}"

        // Branch overriding when master
        if (branch == "master") {
            org = "cicd"
            //space = "Common Staging Space"
            space = "staging"

            routeArgs = "-n ${artifactId}-${urlOrg}-staging -d ${url}"
        }

        def vars = "--var URL_SPACE=${urlSpace} --var URL_ORG=${urlOrg}  --var CF_SPACE=${space} --var CF_ORG=${org} --var BRANCH=${urlBranch} --var CF_URL=${url}"

        echo "Deploying to ${space} ..."

        sh "cf login --skip-ssl-validation -a api.${url} -u \"${env.DEFAULT_CF_USER}\" -p \"${env.DEFAULT_CF_PASSWD}\" -o \"${org}\" -s \"${space}\""
        sh "cf push ${artifactId}-\${GIT_BRANCH} -p \"target/${artifactId}-${version}.${packaging}\" ${routeArgs} ${vars}"

        // Set endpoints suffix to "develop" to point to Common Staging Space
        sh "cf set-env ${artifactId}-\${GIT_BRANCH} API_ENDPOINT_ENVIRONMENT master"
        sh "cf restage ${artifactId}-\${GIT_BRANCH}"

    }
}
