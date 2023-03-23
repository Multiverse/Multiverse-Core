package com.onarandombox.MultiverseCore.utils.settings.node;

import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

public interface NamedValueNode<T> extends TypedValueNode<T> {
    String getName();
}
