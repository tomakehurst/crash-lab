Crash Lab
=========

Crash Lab is a Java library supporting automated testing of non/cross-functional system characteristics, particularly
resilience, stability and correctness under unstable environmental conditions. It combines
very simple HTTP load generation, parsing and assertions against metrics from [Coda Hale's Metrics](http://metrics.codahale.com/)
library and a [Saboteur](https://github.com/tomakehurst/saboteur) client for injecting network faults.

See [ExampleScenarios](https://github.com/tomakehurst/crash-lab/blob/master/src/test/java/com/tomakehurst/crashlab/ExampleScenarios.java)
for some sample test cases.

Crash Lab is available in Maven Central at the following coordinates:
```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>crash-lab</artifactId>
    <version>0.1</version>
</dependency>
```

