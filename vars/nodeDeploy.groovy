def call(Map params) {
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    String space = params.space

    def packageJson = readJSON file: 'package.json'

    String artifactId = packageJson.name;

    // Branch overriding when release/* or master
    if (branch == "master") {
        space = "Common Staging Space"
    }

    // Install CloudFoundry CLI
    sh '''
apt-get update && apt-get install -y wget gnupg
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | apt-key add -
echo "deb https://packages.cloudfoundry.org/debian stable main" | tee /etc/apt/sources.list.d/cloudfoundry-cli.list
apt-get update
apt-get install cf-cli
    '''

    echo "Deploying to ${space} ..."
    withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
        sh "CF_HOME=\$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o thales-devops -s \"${space}\""
        sh "CF_HOME=\$(pwd) cf push ${artifactId}-\${GIT_BRANCH}"
    }
}
