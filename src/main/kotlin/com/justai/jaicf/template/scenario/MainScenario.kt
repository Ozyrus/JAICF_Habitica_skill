package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.aimybox.AimyboxEvent
import com.justai.jaicf.channel.aimybox.aimybox
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
                reactions.say("Перехожу..." )
                var slot = ""
                activator.caila?.run {slot = slots["views"].toString()}
                reactions.aimybox?.response?.action = "changeView"
                reactions.aimybox?.response?.intent = slot

            }
        }

        state("createTask") {
            activators {
                intent("createTask")
            }
            action {
                var taskType = ""
                var taskName:JsonLiteral? = JsonLiteral("Название задачи")
                var taskDescription:JsonLiteral? = JsonLiteral("Описание задачи")
                var taskSentiment:JsonLiteral? = JsonLiteral(true)
                var taskDifficulty:JsonLiteral? = JsonLiteral("easy")
                reactions.say("Перехожу..." )
                activator.caila?.run {
                    taskType = slots["task_type"].toString()
                    slots["Name"]?.let {taskName = JsonLiteral(slots["1"].toString())}
                    slots["Description"]?.let {taskDescription = JsonLiteral(slots["2"].toString())}
                    slots["sentiment"]?.let {taskSentiment = JsonLiteral(slots["sentiment"]?.toBoolean()!!)}
                    slots["difficulty"]?. let {taskDifficulty = JsonLiteral(slots["difficulty"].toString())}
                }
                reactions.aimybox?.response?.action = "createTask"
                reactions.aimybox?.response?.intent = taskType
                taskName?.let { reactions.aimybox?.response?.data?.put("taskName", it) }
                taskDescription?.let { reactions.aimybox?.response?.data?.put("taskDescription", it) }
                taskSentiment?.let { reactions.aimybox?.response?.data?.put("taskSentiment", it) }
                taskDifficulty?.let { reactions.aimybox?.response?.data?.put("taskDifficulty", it) }
            }
        }

        state("fallback", noContext = true) {
            activators {
                catchAll()
            }

            action {
                reactions.say("Я вас не понял")
                reactions.sayRandom("Перейди в настройки", "Перейди в инфо", "Создай сложную задачу с названием Обед и описанием Ресторан", "Создай награду пирожок")
            }
        }
    }
}