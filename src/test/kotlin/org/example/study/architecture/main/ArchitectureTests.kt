package org.example.study.architecture.main

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ArchitectureTests {
    @Test
    fun `コントローラークラス名がControllerで終わる`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("org.example.study.controller..")
            .assertTrue { it.name.endsWith("Controller") }
    }

    @Test
    fun `UseCaseクラス名がUseCaseで終わる`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("org.example.study.usecase..")
            .assertTrue { it.name.endsWith("UseCase") }
    }

    @Test
    fun `リポジトリクラス名がRepositoryで終わる`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("org.example.study.repository..")
            .filter { !it.resideInPackage("..impl..") }
            .filter { !it.resideInPackage("..entity..") }
            .filter { !it.resideInPackage("..jpa..") }
            .assertTrue { it.name.endsWith("Repository") }
    }

    @Test
    fun `RepositoryはUseCaseパッケージから依存される`() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val repository = Layer("Repository", "org.example.study.repository..")
                val useCase = Layer("UseCase", "org.example.study.usecase..")
                useCase.dependsOn(repository)
            }
    }

    @Test
    fun `UseCaseはControllerから依存される`() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val controller = Layer("Controller", "org.example.study.controller..")
                val useCase = Layer("UseCase", "org.example.study.usecase..")
                controller.dependsOn(useCase)
            }
    }

    @Test
    fun `RepositoryはModelに依存し、Modelには依存がない`() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val repository = Layer("Repository", "org.example.study.repository..")
                val model = Layer("Model", "org.example.study.domain.model..")
                repository.dependsOn(model)
                model.dependsOnNothing()
            }
    }
}
