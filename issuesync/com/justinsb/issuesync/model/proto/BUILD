package(default_visibility = ["//visibility:public"])

#proto_library(
#  name = "issues_proto",
#  srcs = ["issues.proto"],
#)
#
#java_proto_library(
#  name = "issues_proto_java",
#  deps = [":issues_proto"],
#)


load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_library")

java_proto_library(
  name = "issues_proto_java",
  protos = [
    "issues.proto"
  ],
  with_grpc = False,
  verbose = 0,
)