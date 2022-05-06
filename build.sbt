
scalaVersion := "2.13.8"

name := "cafemanagementsystem-zhttp"
version := "1.0"
val flywayVersion      = "8.5.1"
val logbackVersion= "1.2.11"
val zhttpVersion= "2.0.0-RC4"
val zioJsonVersion="0.1.5"
val quillVersion="3.17.0-RC2"
val h2Version="2.1.212"
val postgresVersion = "42.3.2"
val jwtCoreVersion="9.0.5"
val zioConfigVersion   = "3.0.0-RC2"
val zioVersion         = "1.0.12"



val zioDependencies= Seq(
    "dev.zio"         %% "zio"  %zioVersion
)
val loggingDependencies= Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion
)

val jsonDependencies=Seq(
  "dev.zio" %% "zio-json" % zioJsonVersion
)

val zioConfigDependencies=Seq(
    "dev.zio"         %% "zio-config"          % zioConfigVersion,
        "dev.zio"         %% "zio-config-typesafe" % zioConfigVersion,
        "dev.zio"         %% "zio-config-magnolia" % zioConfigVersion
)
val flyDependencies=Seq(
    "org.flywaydb"     % "flyway-core"         % flywayVersion
)
val quillDependencies= Seq(
  "io.getquill"     %% "quill-jdbc-zio"  % quillVersion
  //"com.h2database" % "h2" % h2Version
)

val jwtDependencies=Seq(
"com.github.jwt-scala" %% "jwt-core" % jwtCoreVersion
)

val postgresDependencies=Seq(
    "org.postgresql"   % "postgresql" % postgresVersion
)
val httpDependencies= Seq(
    "io.d11"          %% "zhttp" %zhttpVersion
)

lazy val backend = (project in file("."))
  .settings(
    libraryDependencies ++=quillDependencies ++ jsonDependencies ++ loggingDependencies 
    ++httpDependencies ++ jwtDependencies++postgresDependencies
    ++ flyDependencies++ zioConfigDependencies ++zioDependencies
  )