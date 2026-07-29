# libs/

ここに置く jar は **任意**（コンパイルはスタブで通るため不要）。

本物のクラスでビルド・動作確認したい場合のみ、ホスト（Processing）環境から以下を置く:

- Processing の `core.jar`
- nclLibraryForP5 の jar（`jp.ncl.dmx.artnet` / `jp.ncl.display.area` を含む）
- sorauta artnet の jar（`net.sorauta.artnet.ArtNetSender`）

ここに置いた `*.jar` は `compileOnly` に乗る（生成 jar には含まれない）。`*.jar` は `.gitignore` 済み。

```
1 フレームに複数の送信器を使う場合は、読み戻しを 1 回にまとめる:

    loadPixels();                                  // フレームに 1 回だけ
    grade.applyInPlaceRect(pixels, width, x0, y0, x1, y1);   // 必要な矩形だけ補正
    rgbSender.setSendEnabled(on); rgbSender.sendFromPixels(pixels);
    rbSender.setSendEnabled(on);  rbSender.sendFromPixels(pixels);
    updatePixels();                                // フレームに 1 回だけ

sendFromScreen() は呼ぶたびに画面全体を読み戻すため、1 フレームに 2 回呼ぶと
往復も 2 回走る（1920×1080 では約 200 万画素 ×2）。
```
