addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.2.0")

// This resolver declaration is added by default SBT 0.12.x
resolvers += Resolver.url(
  "sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)


addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.1")

addSbtPlugin("com.github.retronym" % "sbt-onejar" % "0.8")
