mappings in SbtNativePackager.Universal in packageZipTarball += file("README.md") -> "README.md"

publishArtifact in (Compile, packageBin) := false

publishArtifact in (Universal, packageZipTarball) := true

publishArtifact in (Compile, packageDoc) := false

Seq(com.atlassian.labs.gitstamp.GitStampPlugin.gitStampSettings: _*)
