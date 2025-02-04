terraform {
  required_version = ">= 1.2.0" # Ensure that the Terraform version is 1.0.0 or higher

  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 4.16"
    }
  }
}

provider "aws" {
  region = var.region
}

resource "aws_instance" "aws_example" {
  ami = var.ami
  instance_type = var.instance_type

}