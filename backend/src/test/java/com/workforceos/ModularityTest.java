package com.workforceos;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies the modular-monolith boundaries: module detection, no cyclic dependencies,
 * and that closed modules only expose declared interfaces.
 */
class ModularityTest {

    @Test
    void verifiesApplicationModules() {
        ApplicationModules.of(WorkforceOsApplication.class).verify();
    }
}
