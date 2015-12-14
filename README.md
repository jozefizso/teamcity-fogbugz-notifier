TeamCity FogBugz Notifier
==============================================================

This plugin enables TeamCity deeper integration with FogBugz.
The plugin will notify FogBugz about successful and failed build
that match a FogBugz case.

This plugin requires the FogBugz Extended Events plugin to be
installed in FogBugz.

Supported TeamCity servers:
* TeamCity 9.1


## Contributing

### 1. Build
Issue `mvn package` command from the root project to build the plugin.
Resulting package will be placed in **target** directory. 

### 2. Install
To install the plugin, put zip archive to **plugins** directory
under TeamCity data directory.


## License

Copyright (c) 2015 Jozef Izso under [MIT License](LICENSE)
