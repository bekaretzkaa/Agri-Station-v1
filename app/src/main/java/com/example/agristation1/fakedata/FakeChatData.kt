package com.example.agristation1.fakedata

import com.example.agristation1.data.chatDetails.ChatEntity
import com.example.agristation1.data.chatDetails.ChatMessageEntity
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.chatDetails.MessageStatus
import java.time.Instant
import java.time.temporal.ChronoUnit

object FakeChatData {

    val chats = listOf(
        ChatEntity(
            id = 1,
            title = "Погода и одежда",
            createdAt = Instant.now().minus(5, ChronoUnit.HOURS),
            updatedAt = Instant.now().minus(4, ChronoUnit.HOURS)
        ),
        ChatEntity(
            id = 2,
            title = "Рецепт пасты",
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS),
            updatedAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        ChatEntity(
            id = 3,
            title = "План изучения Android",
            createdAt = Instant.now().minus(90, ChronoUnit.MINUTES),
            updatedAt = Instant.now().minus(20, ChronoUnit.MINUTES)
        )
    )

    val messages = listOf(
        // =========================
        // CHAT 1 (7 messages)
        // =========================
        ChatMessageEntity(
            messageId = 1,
            chatId = 1,
            positionInChat = 1,
            text = "Что хотели бы спросить?",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 2,
            chatId = 1,
            positionInChat = 2,
            text = "Хочу понять, как сегодня лучше одеться.",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 3,
            chatId = 1,
            positionInChat = 3,
            text = "Подскажите, пожалуйста, в каком городе вы находитесь и какая температура вас интересует?",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 4,
            chatId = 1,
            positionInChat = 4,
            text = "Допустим, на улице около +12 и возможен дождь.",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 5,
            chatId = 1,
            positionInChat = 5,
            text = "Тогда лучше выбрать лёгкую куртку или ветровку, закрытую обувь и взять зонт.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 6,
            chatId = 1,
            positionInChat = 6,
            text = "А шарф нужен?",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 7,
            chatId = 1,
            positionInChat = 7,
            text = "Если ветрено, лёгкий шарф будет вполне уместен, особенно утром и вечером.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),

        // =========================
        // CHAT 2 (3 messages)
        // =========================
        ChatMessageEntity(
            messageId = 8,
            chatId = 2,
            positionInChat = 1,
            text = "Что хотели бы спросить?",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 9,
            chatId = 2,
            positionInChat = 2,
            text = "Дайте простой рецепт пасты на ужин.",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 10,
            chatId = 2,
            positionInChat = 3,
            text = "Отварите пасту, отдельно обжарьте чеснок на оливковом масле, добавьте сливки или томаты по вкусу, смешайте с пастой и посыпьте сыром.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),

        // =========================
        // CHAT 3 (13 messages)
        // =========================
        ChatMessageEntity(
            messageId = 11,
            chatId = 3,
            positionInChat = 1,
            text = "Что хотели бы спросить?",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 12,
            chatId = 3,
            positionInChat = 2,
            text = "Хочу выучить Android разработку с нуля.",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 13,
            chatId = 3,
            positionInChat = 3,
            text = "Отличная цель. У вас уже есть опыт в Kotlin или программировании?",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 14,
            chatId = 3,
            positionInChat = 4,
            text = "Немного знаю Kotlin, но Android ещё не изучал.",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 15,
            chatId = 3,
            positionInChat = 5,
            text = "Тогда начните с основ Android: Activity, жизненный цикл, навигация и работа с ресурсами.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 16,
            chatId = 3,
            positionInChat = 6,
            text = "А Jetpack Compose стоит учить сразу?",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 17,
            chatId = 3,
            positionInChat = 7,
            text = "Да, сейчас Compose очень актуален. Сначала освойте layout, state и navigation в Compose.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 18,
            chatId = 3,
            positionInChat = 8,
            text = "Нужно ли параллельно учить XML?",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 19,
            chatId = 3,
            positionInChat = 9,
            text = "Для новых проектов можно делать упор на Compose, но базово понимать XML всё же полезно для чтения старых проектов.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 20,
            chatId = 3,
            positionInChat = 10,
            text = "Когда подключать Room и Retrofit?",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 21,
            chatId = 3,
            positionInChat = 11,
            text = "После базового UI: сначала сделайте пару экранов, затем подключите Retrofit для API и Room для локального хранения.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 22,
            chatId = 3,
            positionInChat = 12,
            text = "Можете предложить порядок тем на месяц?",
            role = MessageRole.USER,
            status = MessageStatus.SENT
        ),
        ChatMessageEntity(
            messageId = 23,
            chatId = 3,
            positionInChat = 13,
            text = "Да: 1-я неделя — Compose и state, 2-я — navigation и архитектура, 3-я — Retrofit и coroutines, 4-я — Room, DI и сборка небольшого pet-project.",
            role = MessageRole.ASSISTANT,
            status = MessageStatus.SENT
        )
    )
}