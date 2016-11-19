java_binary(
    name = "issuesync",
    srcs = glob(["**/*.java"]),
    main_class = "com.justinsb.issuesync.App",
    runtime_deps = [
        "@ch_qos_logback_logback_classic//jar",
        "@ch_qos_logback_logback_core//jar",
    ],
    deps = [
        "@com_google_code_gson_gson//jar",
        "@commons_logging_commons_logging//jar",
        "@org_apache_httpcomponents_httpclient//jar",
        "@org_apache_httpcomponents_httpcore//jar",
        "@org_slf4j_slf4j_api//jar",
        "//issuesync/com/justinsb/issuesync/model/proto:issues_proto_java",
        "//issuesync/com/justinsb/issuesync/model/proto:issues_proto_java_compile_imports",
    ],
)
