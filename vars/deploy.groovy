def call(Map config = [:]) {
  sh "gcloud app deploy"
}
