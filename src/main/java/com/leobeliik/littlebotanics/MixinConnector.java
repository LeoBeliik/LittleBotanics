package com.leobeliik.littlebotanics;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector  implements IMixinConnector {
    @Override
    public void connect() {
        Mixins.addConfiguration("littlebotanics.mixins.json");
    }
}