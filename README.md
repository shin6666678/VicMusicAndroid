# VicMusicFront 🎵

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

VicMusicFront 是一款完全使用 **Jetpack Compose** 开发的现代化 Android 音乐播放器。它不仅提供了丝滑的音乐播放体验，还集成了基于 WebSocket 的实时聊天系统、动态主题换肤以及完整的社交互动功能。



## ✨ 功能特性 (Features)

- **🎼 核心播放能力**：基于 `Media3/ExoPlayer` 封装，支持歌词同步滚动、黑胶唱片旋转动画及后台服务播放。
- **💬 实时社交聊天**：内置基于 `WebSocket` 的即时通讯系统，支持消息本地持久化（Room）与反向布局渲染。
- **🌈 动态视觉效果**：支持动态主题切换（Dynamic Theme）以及酷炫的 `DynamicGlow` 毛玻璃背景效果。
- **📦 本地音乐管理**：全量扫描本地音频文件，通过 `Repository` 模式进行统一的数据仓库管理。
- **📊 响应式架构**：严格遵循 `Clean Architecture` (简洁架构)，使用 `Flow` + `StateFlow` 实现全链路数据驱动。
- **🤝 社交系统**：包含关注、粉丝、好友列表及发现页推荐功能。

## 🛠️ 技术栈 (Tech Stack)

- **UI**: Jetpack Compose (100% 声明式 UI)
- **DI (依赖注入)**: Hilt
- **Database (数据库)**: Room (支持 Flow 响应式观察)
- **Networking (网络)**: Retrofit + OkHttp
- **Real-time (实时通讯)**: WebSockets
- **Async (异步处理)**: Kotlin Coroutines & Flow
- **Image Loading**: Coil (Coil-Compose)
- **Local Storage**: DataStore (ProtoBuf)
- **Media**: Media3 / ExoPlayer

## 📸 应用截图 (Screenshots)

| 发现页 | 播放详情 | 实时聊天 | 个人中心 | 深色模式 |
| :---: | :---: | :---: |------:|------|
| [Placeholder] | [Placeholder] | [Placeholder] | [Placeholder] |      |

> *注：请在发布前替换为真实的截图或 GIF。*

## 🚀 快速开始 (Getting Started)

1. **克隆项目**:
   ```bash
   git clone [https://github.com/shin6666678/VicMusicFront.git](https://github.com/shin6666678/VicMusicFront.git)