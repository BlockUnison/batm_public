package com.generalbytes.batm.server.extensions.extra.decent;

import java.util.HashSet;
import java.util.Set;

public class JavaDecentExtension extends DecentExtension {
    @Override
    public Set<Class> getChatCommands() {
        return new HashSet<Class>() ;
    }
}
