package com.onarandombox.MultiverseCore.inject.registration;

import org.jvnet.hk2.annotations.Service;

@Service
class DoNotRegisterRegistrationFilter implements RegistrationFilter {

    @Override
    public boolean shouldRegister(Object object) {
        return object.getClass().getAnnotation(DoNotRegister.class) == null;
    }
}
