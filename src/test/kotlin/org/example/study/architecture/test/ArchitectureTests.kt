package com.example.demo.architecture.test

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ArchitectureTests {
    @Test
    fun `Repositoryのテストが全て存在する`() {
        Konsist
            .scopeFromPackage("com.example.demo")
            .classes()
            .withPackage("com.example.demo.repository..")
            .assertTrue {
                it.hasTestClasses()
            }
    }

    @Test
    fun `UseCaseのテストが全て存在する`() {
        Konsist
            .scopeFromPackage("com.example.demo")
            .classes()
            .withPackage("com.example.demo.usecase..")
            .assertTrue {
                it.testClasses().forEach { println(it) }
                it.hasTestClasses()
            }
    }
}
