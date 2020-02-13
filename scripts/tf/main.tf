provider "google" {
  project = "devfest-paris20"
  region = "europe-west1"
  zone =  "europe-west1-b"
  version = "~> 3.8"
}

data "google_project" "project" {

}

resource "google_project_service" "compute_service" {
  service = "compute.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "dataflow_service" {
  service = "dataflow.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "pubsub_service" {
  service = "pubsub.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "bigtable_service" {
  service = "bigtable.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "bigtableadmin_service" {
  service = "bigtableadmin.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_project_service" "bigtabletableadmin_service" {
  service = "bigtabletableadmin.googleapis.com"
  project = data.google_project.project.project_id
  disable_dependent_services = true
  disable_on_destroy = true
}

resource "google_storage_bucket" "default" {
  name     = data.google_project.project.project_id
  location = "EUROPE-WEST1"
  storage_class = "REGIONAL"
  force_destroy = true
}

resource "google_pubsub_topic" "aead-streaming" {
  name = "aead-streaming"
  project = data.google_project.project.project_id
}

resource "google_pubsub_subscription" "aead-streaming" {
  name  = "aead-streaming"
  topic = google_pubsub_topic.aead-streaming.name
  project = data.google_project.project.project_id
}

resource "google_bigquery_dataset" "df_dataset" {
  dataset_id                  = "df"
  friendly_name               = "df"
  location                    = "EU"
  delete_contents_on_destroy = true
  project = data.google_project.project.project_id
}

resource "google_bigquery_dataset" "df_secure_dataset" {
  dataset_id                  = "df_secure"
  friendly_name               = "df_secure"
  location                    = "EU"
  delete_contents_on_destroy = true
  project = data.google_project.project.project_id
}

resource "google_bigtable_instance" "instance" {
  name = "aead-instance"
  instance_type = "DEVELOPMENT"
  project = data.google_project.project.project_id

  cluster {
    cluster_id   = "aead-instance-cluster"
    zone = "europe-west1-b"
    storage_type = "HDD"
  }
}

resource "google_bigtable_table" "table" {
  name          = "keysets"
  instance_name = google_bigtable_instance.instance.name
  project = data.google_project.project.project_id
  column_family {
      family = "cf"
  }
}