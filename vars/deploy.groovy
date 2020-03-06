def call(Map config) {
  echo "Deploying ..."
  sh 'gcloud app deploy'
}
