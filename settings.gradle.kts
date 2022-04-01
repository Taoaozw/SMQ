rootProject.name = "SMQ"


dependencyResolutionManagement {
    versionCatalogs {

        create("libs") {

            version("vertx", "4.2.6")
            version("kotlin", "1.6.20-RC2")
            version("jackson", "2.13.1")
            version("logback", "1.2.10")
            version("ktoml", "0.2.11")

            library("vertx-core","io.vertx","vertx-core").versionRef("vertx")
            library("vertx-mqtt","io.vertx","vertx-mqtt").versionRef("vertx")
            library("vertx-kt","io.vertx","vertx-lang-kotlin").versionRef("vertx")
            library("vertx-kt-coroutines","io.vertx","vertx-lang-kotlin-coroutines").versionRef("vertx")


            library("jackson-core","com.fasterxml.jackson.core","jackson-core").versionRef("jackson")
            library("jackson-databind","com.fasterxml.jackson.core","jackson-databind").versionRef("jackson")

            library("kt-log","io.github.microutils","kotlin-logging-jvm").version("2.1.21")

            library("logback-core","ch.qos.logback","logback-core").versionRef("logback")
            library("logback-classic","ch.qos.logback","logback-classic").versionRef("logback")

            library("kotlin-coroutine","org.jetbrains.kotlinx","kotlinx-coroutines-core").version("1.6.0")

            library("toml-parser-core","com.akuleshov7","ktoml-core").versionRef("ktoml")
            library("toml-parser-file","com.akuleshov7","ktoml-file").versionRef("ktoml")

            bundle("vertxMq", listOf("vertx-core", "vertx-mqtt", "vertx-kt", "vertx-kt-coroutines"))
            bundle("logback", listOf("logback-core", "logback-classic"))
            bundle("jackson", listOf("jackson-core", "jackson-databind"))
            bundle("ktoml", listOf("toml-parser-core", "toml-parser-file"))



        }
    }
}


