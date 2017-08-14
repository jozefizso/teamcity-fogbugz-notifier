TeamCity FogBugz Notifier
==============================================================

> Send build status information to FogBugz Extended Events plugin.

This plugin integrates TeamCity with FogBugz.
The plugin will notify FogBugz about successful and failed builds
that match a FogBugz case.

This plugin requires the [FogBugz Extended Events](https://github.com/jozefizso/FogBugz-ExtendedEvents) plugin to be
installed in FogBugz.

Supported TeamCity servers:
* TeamCity 2017
* TeamCity 10
* TeamCity 9.1


## Contributing

### 1. Build
Issue `mvn package` command from the root project to build the plugin.
Resulting package will be placed in **target** directory.

### 2. Run

The `tc-sdk:start` goal will automatically download and run the TeamCity server
and install the plugin.

```
mvn package tc-sdk:start
```

[TeamCity plugin goals documentation](https://github.com/JetBrains/teamcity-sdk-maven-plugin#plugin-goals)


## License

Copyright © 2015-2017 Jozef Izso under [MIT License](LICENSE)
