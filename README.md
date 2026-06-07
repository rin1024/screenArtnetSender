# screenArtnetSender

「画面（PApplet の pixels）に描かれたものを ArtNet で実機 LED へ送る」機能を、演出・描画から切り離した薄いラッパ jar。

設計の背景・全体像は [documents/architecture/awaodori_artnet_design.md](../../../../documents/architecture/awaodori_artnet_design.md) を参照。

## 提供クラス

| クラス | 役割 |
|--------|------|
| `ScreenArtnetSender` | PApplet + デバイス XML を受け取り、毎フレーム「色補正→サンプル→送信」を行う |
| `ColorGrade` | 明るさ・コントラスト・彩度の補正（イミュータブル、Processing 非依存） |
| `ArtnetSetupException` | 初期化失敗（非チェック例外） |

## 依存とビルド方針

実行時は **ホスト（Processing）側の本物の jar** が以下を供給する前提:

- Processing `core.jar`（`processing.core.PApplet`）
- nclLibraryForP5（`jp.ncl.dmx.artnet.*` / `jp.ncl.display.area.Area`）
- sorauta artnet（`net.sorauta.artnet.ArtNetSender`）

これらはリポジトリに含めないため、**コンパイル時のみのスタブ**を `src/stubs/java` に置き、`compileOnly` で参照する。
生成 jar には `net.sorauta.screenartnet.*` のみが含まれ、スタブは入らない。

```bash
./gradlew build      # build/libs/screenArtnetSender-<version>.jar を生成
```

本物のクラスでビルドしたい場合は `libs/` に jar を置く（[libs/README.md](libs/README.md)）。

## 使い方（ホスト側 draw ループ）

```java
// setup()
sender = new ScreenArtnetSender(this, devicesXml, deviceFolder);

// draw() 末尾（毎フレーム・アニメーションスレッドからのみ）
sender.setSendEnabled(sendEnabledFlag);
sender.setColorGrade(new ColorGrade(briValue, conValue, satValue)); // 不変スナップショット
sender.sendFromScreen(true); // true=画面に補正を反映(WYSIWYG・コピー無し)
```

スレッド規約は `ScreenArtnetSender` の Javadoc を参照（送信経路はアニメーションスレッド限定）。
