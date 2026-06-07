# 平板智能时钟：旧安卓平板桌面工具

平板智能时钟是给旧安卓平板做的轻量桌面/常驻屏应用，最初适配 Lenovo TAB E8 3+32G、Android 8.0。它把跑不动新版微信、QQ 的旧平板改造成一个专用设备：大字翻页时钟、天气、番茄钟、音乐控制台、待办事项和常用网页入口。

## 下载 APK

- [平板智能时钟.apk](outputs/%E5%B9%B3%E6%9D%BF%E6%99%BA%E8%83%BD%E6%97%B6%E9%92%9F.apk)
- [ReviveBoard.apk](outputs/ReviveBoard.apk)

安装后应用名显示为 **平板智能时钟**。如果想把它当主屏幕使用，可以在 Android 的默认桌面设置里选择平板智能时钟。

## 界面预览

### 主页

![平板智能时钟主页](docs/images/home.jpg)

### 点击卡片后的详情页

| 全屏翻页时钟 | 音乐与待办 | 专注番茄钟 |
| --- | --- | --- |
| ![全屏翻页时钟详情页](docs/images/clock-detail.jpg) | ![音乐与待办详情页](docs/images/music-detail.jpg) | ![专注番茄钟详情页](docs/images/focus-detail.jpg) |

## 功能

- 大字翻页时钟，支持网络时间校时，离线时回退系统时间
- 实时天气，支持城市搜索，例如北京、福州、泉州、厦门等
- 番茄钟和白噪音，到时间弹窗提醒
- 音乐控制台，通过 Android 媒体会话/通知栏读取歌名、歌手、专辑图和播放状态
- 待办事项，支持添加、勾选完成、删除和清空完成
- 快捷入口，默认百度、B站、股票
- 常亮、低亮、主题背景切换
- 可作为 Android HOME 桌面应用

## 适合谁

适合 Lenovo TAB E8、老安卓平板、旧手机、备用屏、床头钟、学习番茄钟、厨房屏、家庭留言屏、老人常用网页入口、局域网控制屏等场景。

它不是为了替代新版微信、QQ 或短视频 App，而是把旧设备变成一个安静、固定、可用的专用屏幕。

## 源码位置

主源码：

```text
work/ReviveBoard/src/com/codex/tabe8revive/MainActivity.java
```

资源文件：

```text
work/ReviveBoard/res/
```

APK 输出：

```text
outputs/平板智能时钟.apk
outputs/ReviveBoard.apk
```

## 构建说明

这个项目是原生 Java Android 项目，目前使用本地 Android SDK 手动构建，不依赖 Gradle。开发机上使用的 SDK 在 D 盘，避免占用 C 盘空间。

关键工具包括：

- `aapt2`
- `javac`
- `d8`
- `zipalign`
- `apksigner`

Android 最低版本设置为 API 23，目标版本为 API 26，方便旧设备安装运行。

## 关键词

平板智能时钟、旧平板复活、安卓旧平板、Lenovo TAB E8、Android 8、翻页时钟、番茄钟、天气桌面、音乐控制台、待办事项、旧手机再利用、平板桌面工具。
