import sbt._
import Keys._

object CommonSettingsPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def projectSettings = Seq(
  	scalaVersion := "2.11.8",
  	organization := "com.scalawilliam"
  )
}
