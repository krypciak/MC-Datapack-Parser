# MC-Datapack-Parser
Parser for Minecraft datapacks

Example:
```java
DatapackFunction[] newFunctions = new DatapackFunction[oldDatapack.functions.length];
for (int i = 0; i < oldDatapack.functions.length; i++) {
		DatapackFunction oldFunc = oldDatapack.functions[i];
		newFunctions[i] = new DatapackFunction(oldFunc.submodule(), oldFunc.name(), 
                                    doSomethingWithMcfunctionString(oldFunc.contents()));
}
Datapack newDatapack = new Datapack("MCMPCv7O", oldDatapack.packFormat, "MCMPCv7 Optimized", newFunctions);
newDatapack.parse("~/.minecraft/saves/MCMulator_v7/datapacks/");
```
This reads and converts an existing datapack into a `Datapack` class, iterates over the functions and changes it's contents.  
Then it creates a new `Datapack` and writes it to `~/.minecraft/saves/MCMulator_v7/datapacks/`.  

<br>

# License
Licensed under GNU GPLv3 or later

