# JFusionBrain

A Java interface to the [FusionBrain](http://www.mp3car.com/fusion-brain/) multi-function I/O device.

The FusionBrain supports several analog and digital inputs and outputs and is handy in all types of
automation and robotics projects. Read more about it in [the manual](https://github.com/willmeyer/jfusionbrain/blob/master/docs/Fusion%20Brain.doc).

## Requirements & Setup

- The library is designed to work with a FusionBrain (V3) device connected via USB
- The current implementation relies on the JCommUSB package for USB communications
- Setting up JCommUSB requires you to install the `javax.comm` library in `/externallibs`, and to
  install the actual JCommUSB software on your target machine

Install the JComm library into your local repo as follows:
 
`mvn install:install-file -Dfile=JCommUSB_3_0.jar -DgroupId=com.icaste.JCommUSB -DartifactId=JCommUSB -Dversion=3.0 -Dpackaging=jar -DgeneratePom=true`

## Using the Library

See `com.willmeyer.jfusionbrain.FusionBrainV3` for the core interface.


