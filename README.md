# JFusionBrain

A Java interface to the FusionBrain multi-function IO device.

#Setup

- javax.comm is required, which requires an install of the legacy package from /externallibs
- The JCommUSB library, which you can install into your local repo as well as install to your system
  (it assigns device indexes to your configured USB devices), is also required
- a FusionBrain device, of course  

Install the JComm library into your local repo as follows:
 
mvn install:install-file -Dfile=JCommUSB_3_0.jar -DgroupId=com.icaste.JCommUSB -DartifactId=JCommUSB -Dversion=3.0 -Dpackaging=jar -DgeneratePom=true


