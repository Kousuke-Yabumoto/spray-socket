name := """spray-socket"""

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers ++= Seq(
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
  "spray repo" at "http://repo.spray.io/",
  "Spray OAuth repo" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

// Change this to another test framework if you prefer
libraryDependencies ++= {
  val akkaV  = "2.3.5"
  val sprayV = "1.3.1"
  Seq(
    "org.scalatest"        %% "scalatest"            % "2.2.4"  % "test",
    "io.spray"             %% "spray-json"           % "1.2.6",
    "io.spray"             %% "spray-can"            % sprayV,
    "io.spray"             %% "spray-routing"        % sprayV,
    "io.spray"             %% "spray-testkit"        % sprayV,
    "com.typesafe.akka"    %% "akka-actor"           % akkaV,
    "com.typesafe"         %  "config"                % "1.2.1",
    "org.specs2"           %% "specs2"               % "2.4.1"    % "test",
    "junit"                %  "junit"                % "4.8.1"    % "test",
    "joda-time"            %  "joda-time"            % "2.4",
    "org.joda"             %  "joda-convert"         % "1.6",
    "com.wandoulabs.akka"  %% "spray-websocket"      % "0.1.4"
  )
}

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"

