# VicMusicFront 🎵

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

VicMusicFront 是一款完全使用 **Jetpack Compose** 开发的现代化 Android 音乐播放器。它不仅提供了丝滑的音乐播放体验，还集成了基于 WebSocket 的实时聊天系统、动态主题换肤以及完整的社交互动功能。
**📥 [点击此处下载体验 (Download APK)](http://api.vicmusic.homes)**


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


### ☀️ 浅色主题 (Light Mode)

|  |  |  |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/77a96970-45cc-4908-bfd2-77d1c1a91445" width="220"> | <img src="https://github.com/user-attachments/assets/35d5b9b4-cd2a-4b59-934e-7f422c8f7eef" width="220"> | <img src="https://github.com/user-attachments/assets/8153f59f-bfd4-4945-90d1-c9cbc471a7d5" width="220"> |
| <img src="https://github.com/user-attachments/assets/1690ab91-0c5e-4966-aaf7-7a169ce6f911" width="220"> | <img src="https://github.com/user-attachments/assets/d163402e-83fa-48fa-8c2f-eb2212ae4b05" width="220"> | <img src="https://github.com/user-attachments/assets/654aad81-e8c0-47f1-8d5f-61bf3f51f259" width="220"> |

### 🌙 深色主题 (Dark Mode)

|  |  |  |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/47643ddf-1327-4c06-a267-35762169bf07" width="220"> | <img src="https://github.com/user-attachments/assets/8297a8f8-95a2-4c68-97be-aacbe80ce729" width="220"> | <img src="https://github.com/user-attachments/assets/a85ff242-66b6-4f87-a48d-0d278b32da50" width="220"> |
|  |  |  |
| <img src="https://github.com/user-attachments/assets/9d0408f2-f50e-426b-b6a3-1f7ad22e09ad" width="220"> | <img src="https://github.com/user-attachments/assets/2cd653a2-9830-40ce-b848-58935b8c6fb0" width="220"> | <img src="https://github.com/user-attachments/assets/a1c3ebe0-b57d-43f9-9244-34cfdbdb6f1f" width="220"> |



## 🚀 快速开始 (Getting Started)

1. **克隆项目**:
   ```bash
   git clone [https://github.com/shin6666678/VicMusicAndroid.git](https://github.com/shin6666678/VicMusicAndroid.git)
