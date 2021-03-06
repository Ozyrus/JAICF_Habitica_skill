package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.aimybox.AimyboxEvent
import com.justai.jaicf.channel.aimybox.aimybox
import com.justai.jaicf.context.ActivatorContext
import com.justai.jaicf.model.scenario.Scenario
import kotlinx.serialization.json.*

object MainScenario : Scenario() {

    init {
        state("Start") {
            globalActivators {
                regex("/start")
                event(AimyboxEvent.START)
            }
            action {
                reactions.say("So let's begin!")
            }
        }

        state("changeView") {
            activators {
                intent("changeView")
            }
            action {
                reactions.say("Перехожу...")
                reactions.aimybox?.response?.action = "changeView"
                reactions.aimybox?.response?.intent = activator.getCailaRequiredSlot("views")
            }
        }

        state("createTask") {
            activators {
                intent("createTask")
            }
            action {
                val taskType = activator.getCailaSlot("taskType").asJsonLiteralOr("")
                reactions.say("Перехожу...")
                reactions.aimybox?.response?.action = "createTask"
                reactions.aimybox?.response?.intent = taskType.content
                reactions.aimybox?.response?.run {
                    data["taskName"] = activator.getCailaSlot("taskName").asJsonLiteralOr("")
                    data["taskDescription"] = activator.getCailaSlot("taskDescription").asJsonLiteralOr("")
                    data["taskSentiment"] = activator.getCailaSlotBool("taskSentiment").asJsonLiteralOr(true)
                    data["taskDifficulty"] = activator.getCailaSlot("taskDifficulty").asJsonLiteralOr("easy")
                }
            }
        }

        fallback {
            reactions.say("Я вас не понял")
            reactions.sayRandom(
                "Перейди в настройки",
                "Перейди в инфо",
                "Создай сложную задачу с названием Обед и описанием Ресторан",
                "Создай награду пирожок"
            )
        }
    }
}

private fun ActivatorContext.getCailaRequiredSlot(k: String): String =
    getCailaSlot(k) ?: error("Missing Caila slot for key: $k")

private fun ActivatorContext.getCailaSlot(k: String): String? =
    caila?.slots?.get(k)

private fun ActivatorContext.getCailaSlotBool(k: String): Boolean? =
    caila?.slots?.get(k)?.toBoolean()

private fun String?.asJsonLiteralOr(other: String) = this?.let { JsonLiteral(this) } ?: JsonLiteral(other)
private fun Boolean?.asJsonLiteralOr(other: Boolean) = this?.let { JsonLiteral(this) } ?: JsonLiteral(other)
