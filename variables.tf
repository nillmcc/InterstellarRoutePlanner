variable "instance_type" {
  type = string                     # The type of the variable, in this case a string
  default = "t2.micro"                 # Default value for the variable
  description = "The type of EC2 instance" # Description of what this variable represents
}

variable "ami" {
  type    = string
  default = "ami-050d140dbea0078a5"
}

variable "region" {
  description = "AWS region where the resources will be deployed."
  type        = string
  default     = "us-west-2"
}
