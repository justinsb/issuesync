java_binary(
    name = "issuesync",
    srcs = glob(["**/*.java"]),
    main_class = "com.justinsb.issuesync.App",
    deps = [
        "@com_google_code_gson_gson//jar",
        "@org_apache_httpcomponents_httpcore//jar",
        "@org_apache_httpcomponents_httpclient//jar",
        "@commons_logging_commons_logging//jar",
        "//issuesync/com/justinsb/issuesync/model/proto:issues_proto_java",
    ]
)