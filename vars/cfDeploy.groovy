def call(Map params) {
    // capture scm variables
    def scmVars = checkout scm
    String branch = scmVars.GIT_BRANCH

    // or use the environment variable
    branch = env.GIT_BRANCH
    
    if (branch == "master") {
        echo "Deploying ..."
        withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
            sh "CF_HOME=\$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o aurelien -s \"${params.service}\""
            sh "CF_HOME=\$(pwd) cf push thdevops-\${GIT_BRANCH} -p \"${params.target}\""
        }
    }
}
