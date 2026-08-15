package com.workforceos;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enforces the dependency-inversion guardrail: domain packages depend only on the
 * Java/domain abstractions, never on adapters or framework persistence types.
 */
@AnalyzeClasses(packages = "com.workforceos")
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_is_isolated_from_adapters_and_frameworks =
            noClasses().that().resideInAPackage("..domain..")
                    .and().doNotHaveSimpleName("package-info")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..adapter..",
                            "org.springframework..",
                            "jakarta.persistence..");
}
