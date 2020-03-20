def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    String space = params.space

    def path = pwd()

    def pom = new XmlParser().parse(path + "/pom.xml")

    String artifactId = pom.artifactId.text()
    String version = pom.version.text()
    String packaging = pom.packaging.text()

    // Branch overriding when release/* or master
    if (branch == "master") {
        space = "Common Staging Space"
    }

    // Install CloudFoundry CLI
    sh '''
sudo apt-get update && apt-get install -y wget
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | sudo apt-key add -
echo "deb https://packages.cloudfoundry.org/debian stable main" | sudo tee /etc/apt/sources.list.d/cloudfoundry-cli.list
sudo apt-get update
sudo apt-get install cf-cli
    '''

    echo "Deploying to ${space} ..."
    withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
        sh "CF_HOME=\$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o thales-devops -s \"${space}\""
        sh "CF_HOME=\$(pwd) cf push ${artifactId}-\${GIT_BRANCH} -p \"target/${artifactId}-${version}.${packaging}\""
    }
}
