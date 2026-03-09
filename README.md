![cf75a68906d10462793899c6794deb5c](https://github.com/user-attachments/assets/2545cce6-f913-47bb-a38f-a861f1080a58)# VicMusicFront 🎵

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

- **UI**: Jetpack Compose
- **DI (依赖注入)**: Hilt
- **Database (数据库)**: Room (支持 Flow 响应式观察)
- **Networking (网络)**: Retrofit + OkHttp
- **Real-time (实时通讯)**: WebSockets
- **Async (异步处理)**: Kotlin Coroutines & Flow
- **Image Loading**: Coil (Coil-Compose)
- **Local Storage**: DataStore (ProtoBuf)
- **Media**: Media3 / ExoPlayer

## 📸 应用截图 (Screenshots)

> *浅色主题展示*
![cf75a68906d10462793899c6794deb5c](https://github.com/user-attachments/assets/77a96970-45cc-4908-bfd2-77d1c1a91445)
![7008f577d80f79bdd3f861e050ef2c6a](https://github.com/user-attachments/assets/35d5b9b4-cd2a-4b59-934e-7f422c8f7eef)
![2ca83925bf7e33392524e0fac3a213ae](https://github.com/user-attachments/assets/8153f59f-bfd4-4945-90d1-c9cbc471a7d5)
![db8d1d24![54f8577aa569ee2efdd188f8416c9f48](https://github.com/user-attachments/assets/1690ab91-0c5e-4966-aaf7-7a169ce6f911)
78b008f81f32a0cea04e4729](https://github.com/user-attachments/assets/931a16c3-a0e6-4311-884f-ba486f877dfc)
![87c919c273c1396169946785071e773d](https://github.com/user-attachments/assets/d163402e-83fa-48fa-8c2f-eb2212ae4b05)
![dc6d06a19259187c4db8cf1b4b71674c](https://github.com/user-attachments/assets/654aad81-e8c0-47f1-8d5f-61bf3f51f259)
> *深色主题展示*
![9a2a2fcb94852953b824c8a280ddafe3](https://github.com/user-attachments/assets/47643ddf-1327-4c06-a267-35762169bf07)
![48f04a27abe8a5fd3032d324095cb87e](https://github.com/user-attachments/assets/8297a8f8-95a2-4c68-97be-aacbe80ce729)
![0f8d8da637a29af7c43e981fbcb37001](https://github.com/user-attachments/assets/a85ff242-66b6-4f87-a48d-0d278b32da50)
![0dd7e86a34cef474eac2d04f3c7ea88b](https://github.com/user-attachments/assets/9d0408f2-f50e-426b-b6a3-1f7ad22e09ad)
![fdde27bae094cb252e716f05716454cd](https://github.com/user-attachments/assets/2cd653a2-9830-40ce-b848-58935b8c6fb0)
![f53a22d6852bf3f0ef6aa5d86ff877a1](https://github.com/user-attachments/assets/a1c3ebe0-b57d-43f9-9244-34cfdbdb6f1f)



## 🚀 快速开始 (Getting Started)

1. **克隆项目**:
   ```bash
   git clone [https://github.com/shin6666678/VicMusicFront.git](https://github.com/shin6666678/VicMusicFront.git)
