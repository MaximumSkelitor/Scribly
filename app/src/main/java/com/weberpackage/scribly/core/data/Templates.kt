package com.weberpackage.scribly.core.data

data class SubscriptionTemplate(
    val name: String,
    val category: String,
    val icon: String,
    val defaultPrice: Double = 0.0
)

val popularTemplates = listOf(
    SubscriptionTemplate("Netflix", "Entertainment", "🎬", 15.99),
    SubscriptionTemplate("Spotify", "Music & Audio", "🎵", 9.99),
    SubscriptionTemplate("Apple Music", "Music & Audio", "🎵", 10.99),
    SubscriptionTemplate("YouTube Premium", "Entertainment", "🎬", 13.99),
    SubscriptionTemplate("Disney+", "Entertainment", "🎬", 7.99),
    SubscriptionTemplate("Amazon Prime", "Entertainment", "🎬", 14.99),
    SubscriptionTemplate("Hulu", "Entertainment", "🎬", 7.99),
    SubscriptionTemplate("Xbox Game Pass", "Gaming", "🎮", 9.99),
    SubscriptionTemplate("PlayStation Plus", "Gaming", "🎮", 9.99),
    SubscriptionTemplate("Nintendo Switch Online", "Gaming", "🕹️", 3.99),
    SubscriptionTemplate("iCloud+", "Cloud Storage", "☁️", 0.99),
    SubscriptionTemplate("Google One", "Cloud Storage", "☁️", 1.99),
    SubscriptionTemplate("Dropbox", "Cloud Storage", "☁️", 9.99),
    SubscriptionTemplate("Adobe Creative Cloud", "Software", "🎨", 52.99),
    SubscriptionTemplate("ChatGPT Plus", "Productivity", "💡", 20.00),
    SubscriptionTemplate("GitHub Copilot", "Software", "💻", 10.00),
    SubscriptionTemplate("Microsoft 365", "Productivity", "💼", 6.99),
    SubscriptionTemplate("Notion", "Productivity", "📝", 8.00),
    SubscriptionTemplate("Slack", "Productivity", "📧", 6.67),
    SubscriptionTemplate("Zoom", "Productivity", "📹", 14.99)
)
